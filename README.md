### PredictTheProduct 中文电商商品预测

本项目是 https://github.com/jingpeicomp/product-category-predict 的简化版本。

### 功能：对淘宝等电商网站上的商品标题进行商品类别预测


| 商品标题                                                                           | 一级类别           | 二级类别 | 三级类别 |
| ---------------------------------------------------------------------------------- | ------------------ | -------- | -------- |
| 【工厂直供】冬季加绒牛仔裤女高腰加厚保暖韩版弹力显瘦外穿小脚裤子2018新款 黑色 28码 | 服饰内衣           | 男装     | 加绒裤   |
| 热水袋充电防爆暖水袋注水煖宝宝暖宫暖手宝女韩版毛绒布萌萌可爱                       | 家居家装           | 生活日用 | 保暖防护 |
| 山野里 水果干零食大礼包组合芒果干草莓干杏干黄桃干果脯混合装                        | 食品饮料、保健食品 | 休闲食品 | 蜜饯果干 |

### 算法思路

见[原仓库](https://github.com/jingpeicomp/product-category-predict) 

### 特点：

**抽取核心逻辑，去掉了 Spring 框架，可以直接使用，更加方便的集成到其他系统**
**去掉了模型训练的步骤，内置准确度达 85%的模型**
**更新了 SparkAPI，支持 Spark 最新版本 2.4.0**

在这里感谢原作者 @jingpeicomp，这是一个非常有意义的项目。

### 用法

```java
AppConfigProperties appConfigProperties = new AppConfigProperties();
PredictProduct predictProduct = new PredictProduct(appConfigProperties);
List<ProductCategory> res = predictProduct.predict(input);
```

### Demo 程序运行步骤

1. 运行 Spark Standalone Mode 参考[链接](https://spark.apache.org/docs/latest/spark-standalone.html)
2. 在appConfigProperties中修改配置
   1. 数据地址product_category/data
   2. 模型地址product_category/model
   3. 特征向量维度 10000
   4. Spark Master 地址 如果是Standalone 应该是`spark://127.0.0.1:7077`
   5. Spark应用名称

3. 打包程序得到 jar 包
   `mvn clean package -Dmaven.test.skip=true`

4. 提交 jar 包到 Spark 进行运算
   `bin/spark-submit --master spark://your_spark_master --class "com.company.Main" XXX.jar`

### 运行效果
见 [Result.csv](https://github.com/arcosx/PredictTheProduct/blob/master/data/Result.csv)