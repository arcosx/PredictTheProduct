#!/usr/bin/env bash
mvn clean package -Dmaven.test.skip=true
/Users/wgb/Code/spark-2.2.1-bin-hadoop2.7/bin/spark-submit --master spark://arcosx.local:7077 --class "com.company.Main" /Users/wgb/Code/e-business/PredictTheProduct/target/product_category-1.0-SNAPSHOT-jar-with-dependencies.jar