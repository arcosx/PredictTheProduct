package com.company.ml;

import com.company.Main;
import com.company.common.CategoryUtils;
import com.company.common.ProductCategory;
import com.company.common.StandardCategory;
import com.company.conf.AppConfigProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PredictProduct {

    private Map<Integer, StandardCategory> standardCategoryVoMap = new HashMap<>();

    private CategoryModel categoryModel;

    public  PredictProduct(AppConfigProperties appConfigProperties){
        try (InputStream is = Main.class.getResourceAsStream("/category.json")) {
            String jsonString = IOUtils.toString(is);
            TypeReference<List<StandardCategory>> categoryVoTypeReference = new TypeReference<List<StandardCategory>>() {
            };
            List<StandardCategory> voList = CategoryUtils.fromJson(jsonString, categoryVoTypeReference);
            if (CollectionUtils.isEmpty(voList)) {
                System.out.println("Cannot load category json file");
                return;
            }
            voList.forEach(vo -> this.standardCategoryVoMap.put(vo.getThirdCateId(), vo));
        } catch (IOException e) {
            System.out.println("Cannot load category json file");
        }
        SparkSession sparkSession = SparkSession
                .builder()
                .master(appConfigProperties.getMasterUrl())
                .appName(appConfigProperties.getAppName())
                .config("spark.cores.max",1)
                .config("spark.executor.memory","1g")
                .config("spark.driver.memory","512m")
                .config("spark.network.timeout","5000")
                .getOrCreate();
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());
        NlpTokenizer nlpTokenizer = new NlpTokenizer(appConfigProperties);
        BayesClassification bayesClassification = new BayesClassification(appConfigProperties);
        FeatureExtractor featureExtractor = new FeatureExtractor(appConfigProperties, sparkSession,javaSparkContext, nlpTokenizer);
        this.categoryModel = new CategoryModel(featureExtractor, bayesClassification);
        this.categoryModel.loadModel();
    }
    public List<ProductCategory> predict(List<String> productNames){
        List<ProductCategory> temp = categoryModel.predict(productNames);
        return temp.stream()
                .peek(categoryVo -> {
                    StandardCategory standardVo = standardCategoryVoMap.get(categoryVo.getThirdCateId());
                    categoryVo.copyCategory(standardVo);
                })
                .collect(Collectors.toList());
    }
}
