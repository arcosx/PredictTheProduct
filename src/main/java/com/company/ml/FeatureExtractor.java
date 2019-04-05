package com.company.ml;

import com.company.common.CategoryUtils;
import com.company.conf.AppConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class FeatureExtractor {


    private AppConfigProperties appConfigProperties;


    private JavaSparkContext sparkContext;


    private SQLContext sqlContext;


    private NlpTokenizer nlpTokenizer;

    public FeatureExtractor(AppConfigProperties appConfigProperties, JavaSparkContext sparkContext, SQLContext sqlContext, NlpTokenizer nlpTokenizer) {
        this.appConfigProperties = appConfigProperties;
        this.sparkContext = sparkContext;
        this.sqlContext = sqlContext;
        this.nlpTokenizer = nlpTokenizer;
        log.info("Start PostConstruct...");
        tokenizer = new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");
        hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(appConfigProperties.getNumFeatures());
        loadModel();
    }

    private Tokenizer tokenizer;

    private HashingTF hashingTF;

    private IDFModel idfModel;


    /**
     * 从本地加载训练好的tf-idf模型
     */
    public synchronized void loadModel() {
        if (StringUtils.isNotBlank(appConfigProperties.getIdfModelFile())
                && CategoryUtils.isFileExist(appConfigProperties.getIdfModelFile())) {
            try {
                idfModel = IDFModel.load(appConfigProperties.getIdfModelFile());
                log.info("Successfully loading tf-idf model from {}", appConfigProperties.getIdfModelFile());
            } catch (Exception e) {
                log.error("Cannot load tf-idf model from {}", appConfigProperties.getIdfModelFile(), e);
            }
        }
    }

    /**
     * 生成特征向量
     *
     * @param names 商品名称列表
     * @return 特征向量
     */
    public Dataset<Row> extract(List<String> names) {
        for (int i = 0;i<names.size();i++){
            System.out.println(names.get(i));
        }
        List<String> terms = nlpTokenizer.segment(names);
        List<String[]> segmentNames = new ArrayList<>();
        for (int i = 0, length = names.size(); i < length; i++) {
            segmentNames.add(new String[]{StringUtils.strip(names.get(i)), StringUtils.strip(terms.get(i))});
        }
        SparkSession sparkSession;
        JavaRDD<Row> textRowRDD = sparkContext.parallelize(segmentNames)
                .map(RowFactory::create);
        StructType schema = new StructType(new StructField[]{
                new StructField("origin", DataTypes.StringType, false, Metadata.empty()),
                new StructField("text", DataTypes.StringType, false, Metadata.empty())
        });
        Dataset<Row> sentenceDataset = sqlContext.createDataFrame(textRowRDD, schema);
        Dataset<Row> wordsDataset = tokenizer.transform(sentenceDataset);
        Dataset<Row> featurizedDataset = hashingTF.transform(wordsDataset);
        return idfModel.transform(featurizedDataset);
    }
}
