package com.company;
import com.company.common.CategoryUtils;
import com.company.common.ProductCategory;
import com.company.common.StandardCategory;
import com.company.conf.*;
import com.company.ml.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        // 加载词典
        Map<Integer, StandardCategory> standardCategoryVoMap = new HashMap<>();
        final TypeReference<List<StandardCategory>> categoryVoTypeReference = new TypeReference<List<StandardCategory>>() {
        };
        try (InputStream is = Main.class.getResourceAsStream("/category.json")) {
            String jsonString = IOUtils.toString(is);
            List<StandardCategory> voList = CategoryUtils.fromJson(jsonString, categoryVoTypeReference);
            if (CollectionUtils.isEmpty(voList)) {
                System.out.println("Cannot load category json file");
                return;
            }
            voList.forEach(vo -> standardCategoryVoMap.put(vo.getThirdCateId(), vo));
        } catch (IOException e) {
           System.out.println("Cannot load category json file");
        }
        // 定义输入
        List<String> input = new LinkedList<>();
        input.add("【买2送1】海棠花苗 腊梅树苗 梅花树盆景 樱花苗 紫藤苗 桂花树苗盆栽室内外绿植花卉观花庭院阳台 西府海棠 三年苗");
        AppConfigProperties appConfigProperties = new AppConfigProperties();
        SparkConfigProperties sparkConfigProperties = new SparkConfigProperties();
        System.out.println(sparkConfigProperties);
        SparkConf sparkConf = new SparkConf().setAppName(sparkConfigProperties.getAppName())
                .setMaster(sparkConfigProperties.getMasterUrl());
        sparkConf.set("spark.cores.max","1");
        sparkConf.set("spark.executor.memory","1g");
        sparkConf.set("spark.driver.memory","512m");
        sparkConf.set("spark.network.timeout","5000");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
//        Trainer trainer = new Trainer(javaSparkContext,sqlContext,appConfigProperties);
//        trainer.trainWithTfIdf();
        NlpTokenizer nlpTokenizer = new NlpTokenizer(appConfigProperties);
        nlpTokenizer.init();
        BayesClassification bayesClassification = new BayesClassification(appConfigProperties);
        FeatureExtractor featureExtractor = new FeatureExtractor(appConfigProperties,javaSparkContext,sqlContext,nlpTokenizer);
        featureExtractor.init();
        CategoryModel categoryModel = new CategoryModel(featureExtractor,bayesClassification);
        categoryModel.loadModel();
        List<ProductCategory> temp = categoryModel.predict(input);
        List<ProductCategory> res = temp.stream()
                .peek(categoryVo -> {
                    StandardCategory standardVo = standardCategoryVoMap.get(categoryVo.getThirdCateId());
                    categoryVo.copyCategory(standardVo);
                })
                .collect(Collectors.toList());
        System.out.println(res.get(0).getProductName());
        System.out.println(res.get(0).getFirstCate());
    }
}
