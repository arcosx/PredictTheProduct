package com.company.ml;
import com.company.common.CategoryUtils;
import com.company.conf.AppConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.ml.classification.NaiveBayesModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import javax.annotation.PostConstruct;
import java.io.Serializable;


@Slf4j
public class BayesClassification implements Serializable {


    private AppConfigProperties appConfigProperties;

    private NaiveBayesModel model;

    public BayesClassification(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @PostConstruct
    public void init() {
        loadModel();
    }

    /**
     * 从本地加载训练好的贝叶斯模型
     */
    public synchronized void loadModel() {
        if (StringUtils.isNotBlank(appConfigProperties.getBayesModelFile())
                && CategoryUtils.isFileExist(appConfigProperties.getBayesModelFile())) {
            try {
                model = NaiveBayesModel.load(appConfigProperties.getBayesModelFile());
                log.info("Successfully loading bayes model from {}", appConfigProperties.getBayesModelFile());
            } catch (Exception e) {
                log.error("Cannot load bayes model from {}", appConfigProperties.getBayesModelFile(), e);
            }
        }
    }


    /**
     * 对数据进行分类
     *
     * @param features 待分类数据
     * @return 数据预测结果
     */
    public Dataset<Row> classify(Dataset<Row> features) {
        return model.transform(features);
    }
}
