package com.company.ml;

import com.company.Main;
import com.company.common.CategoryUtils;
import com.company.common.ProductCategory;
import com.company.common.StandardCategory;
import com.company.conf.AppConfigProperties;
import com.company.conf.SparkConfigProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PredictProduct {

    private AppConfigProperties appConfigProperties;
    private SparkConfigProperties sparkConfigProperties;
    private Map<Integer, StandardCategory> standardCategoryVoMap = new HashMap<>();
    private final TypeReference<List<StandardCategory>> categoryVoTypeReference = new TypeReference<List<StandardCategory>>() {
    };

    private CategoryModel categoryModel;

    public  PredictProduct(AppConfigProperties appConfigProperties, SparkConfigProperties sparkConfigProperties){
        this.appConfigProperties = appConfigProperties;
        this.sparkConfigProperties = sparkConfigProperties;
        try (InputStream is = Main.class.getResourceAsStream("/category.json")) {
            String jsonString = IOUtils.toString(is);
            List<StandardCategory> voList = CategoryUtils.fromJson(jsonString, this.categoryVoTypeReference);
            if (CollectionUtils.isEmpty(voList)) {
                System.out.println("Cannot load category json file");
                return;
            }
            voList.forEach(vo -> this.standardCategoryVoMap.put(vo.getThirdCateId(), vo));
        } catch (IOException e) {
            System.out.println("Cannot load category json file");
        }
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(sparkConfigProperties.getAppName());
        sparkConf.setMaster(sparkConfigProperties.getMasterUrl());
        sparkConf.set("spark.cores.max", "1");
        sparkConf.set("spark.executor.memory", "1g");
        sparkConf.set("spark.driver.memory", "512m");
        sparkConf.set("spark.network.timeout", "5000");

        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        NlpTokenizer nlpTokenizer = new NlpTokenizer(appConfigProperties);
        nlpTokenizer.init();
        BayesClassification bayesClassification = new BayesClassification(appConfigProperties);
        FeatureExtractor featureExtractor = new FeatureExtractor(appConfigProperties, javaSparkContext, sqlContext, nlpTokenizer);
        featureExtractor.init();
        this.categoryModel = new CategoryModel(featureExtractor, bayesClassification);
        this.categoryModel.loadModel();
    }
    public List<ProductCategory> predict(List<String> productNames){
        List<ProductCategory> temp = categoryModel.predict(productNames);
        List<ProductCategory> res = temp.stream()
                .peek(categoryVo -> {
                    StandardCategory standardVo = standardCategoryVoMap.get(categoryVo.getThirdCateId());
                    categoryVo.copyCategory(standardVo);
                })
                .collect(Collectors.toList());
        return res;
    }
}
