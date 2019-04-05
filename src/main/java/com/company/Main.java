package com.company;
import com.company.common.ProductCategory;
import com.company.conf.AppConfigProperties;

import com.company.ml.PredictProduct;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> input = new LinkedList<>();
        // 读取 CSV 文件
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/wgb/Code/e-business/product_category/data/Raw.csv"));
            reader.readLine();
            String line;
            while((line=reader.readLine())!=null){
                String[] item = line.split(",");
                String last = item[item.length-1];
                String rawString = last.substring(1,last.length()-1);
                input.add(rawString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppConfigProperties appConfigProperties = new AppConfigProperties();
        PredictProduct predictProduct = new PredictProduct(appConfigProperties);
        List<ProductCategory> res = predictProduct.predict(input);
        // 写入CSV文件
        try {
            File csv = new File("/Users/wgb/Code/e-business/product_category/data/Result.csv"); // CSV数据文件

            BufferedWriter bw = new BufferedWriter(new FileWriter(csv,true)); // 附加
            for (int i = 0;i<res.size();i++){
                bw.write(res.get(i).getProductName()+","+res.get(i).getFirstCate()+","+res.get(i).getSecondCate()+","+res.get(i).getThirdCate());
                bw.newLine();
                System.out.println(res.get(i).getProductName());
                System.out.println(res.get(i).getFirstCate());
                System.out.println(res.get(i).getSecondCate());
                System.out.println(res.get(i).getThirdCate());
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
