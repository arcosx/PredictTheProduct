#!/usr/bin/env bash
mvn clean package -Dmaven.test.skip=true
/usr/local/Cellar/apache-spark/2.4.0/libexec/bin/spark-submit --master spark://arcosx.local:7077 --class "com.company.Main" /Users/wgb/Code/e-business/product_category/target/product_category-1.0-SNAPSHOT-jar-with-dependencies.jar