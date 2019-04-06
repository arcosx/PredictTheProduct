package com.company;
import com.company.common.ProductCategory;
import com.company.conf.AppConfigProperties;

import com.company.ml.PredictProduct;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main {
    static final String DB_URL = "jdbc:mysql://localhost:3306/eb";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        List<String> input = new LinkedList<>();
        HashMap<Integer,Integer> numToID = new HashMap<>();
        //读取数据库文件
        Connection conn;
        Statement stmt;
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            // 商品分类
            String sql;
            sql = "select id,item from action where item <> '';";
            ResultSet rs = stmt.executeQuery(sql);
            int num = 0;
            while (rs.next()){
                int id = rs.getInt("id");
                numToID.put(num,id);
                num++;
                String item = rs.getString("item");
                input.add(item);
            }
            AppConfigProperties appConfigProperties = new AppConfigProperties();
            PredictProduct predictProduct = new PredictProduct(appConfigProperties);
            List<ProductCategory> res = predictProduct.predict(input);
            System.out.println("预测完成");
            String newSql;
            for (int i = 0;i<res.size();i++){
                newSql = String.format("UPDATE `action` SET category = '%s' WHERE id = %d;",res.get(i).getSecondCate(),numToID.get(i));
                stmt.addBatch(newSql);
            }
            System.out.println("预测完成");
            stmt.executeBatch();
            // 关键词检索分类
            stmt.clearBatch();
            input.clear();
            numToID.clear();
            res.clear();
            sql  = "select id ,queryword from action where queryword <> '' and category is null";
            rs = stmt.executeQuery(sql);
            num = 0;
            while (rs.next()){
                int id = rs.getInt("id");
                numToID.put(num,id);
                num++;
                String item = rs.getString("queryword");
                input.add(item);
            }
            res = predictProduct.predict(input);
            System.out.println("预测完成");
            for (int i = 0;i<res.size();i++){
                newSql = String.format("UPDATE `action` SET category = '%s' WHERE id = %d;",res.get(i).getSecondCate(),numToID.get(i));
                stmt.addBatch(newSql);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
