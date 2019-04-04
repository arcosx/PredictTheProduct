package com.company.conf;

import lombok.Data;

import java.io.File;

@Data
public class AppConfigProperties {
    /**
     * 数据目录
     */
    private String dataPath = "/Users/wgb/Code/e-business/product_category/data";

    /**
     * 训练好的模型数据目录
     */
    private String modelPath = "/Users/wgb/Code/e-business/product_category/model";

    /**
     * 特征向量维度
     */
    private int numFeatures = 10000;

    /**
     * 系统启动时，如果本地没有模型文件，是否启动训练
     */
    private boolean isTrainWhenStart = true;

    /**
     * idf model文件
     *
     * @return tf-idf模型文件路径
     */
    public String getIdfModelFile() {
        return String.join(File.separator, modelPath, "idf");
    }

    /**
     * bayes model file
     *
     * @return 贝叶斯模型文件路径
     */
    public String getBayesModelFile() {
        return String.join(File.separator, modelPath, "bayes");
    }

    /**
     * 训练数据集
     *
     * @return 训练数据文件路径
     */
    public String getTrainDataFile() {
        return String.join(File.separator, dataPath, "train.data");
    }

    /**
     * 商品分词字典
     *
     * @return 商品领域词典文件
     */
    public String getDictFile() {
        return String.join(File.separator, dataPath, "product.dict");
    }
}
