package com.company.conf;

import lombok.Data;

import java.util.Properties;

@Data
public class SparkConfigProperties {
    /**
     * spark master url
     */
    private String masterUrl = "spark://arcosx.local:7077";

    /**
     * Spark应用名称
     */
    private String appName = "heheda";

    /**
     * job jar
     */
    private String dependenceJar = "/Users/wgb/Code/e-business/product_category/target/product_category-1.0-SNAPSHOT.jar";

    /**
     * spark配置属性
     */
    private Properties properties;
}
