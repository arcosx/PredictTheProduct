package com.company;
import com.company.common.ProductCategory;
import com.company.conf.AppConfigProperties;

import com.company.ml.PredictProduct;
import org.apache.spark.sql.sources.In;

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
//        --------------------求商品类目以及搜索词类目---------------------
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
//  ------------------------数字化表格-----------------------
//        Connection conn;
//        Statement stmt;
//
//        HashMap<String,Integer> userIdMap = new HashMap<>();
//        HashMap<String,Integer> actionMap = getActionMap();
//        HashMap<String,Integer> sortReqMap = getSortReqMap();
//        HashMap<String,Integer> categoryMap = new HashMap<>();
//        HashMap<String,Integer> itemMap = new HashMap<>();
//        System.out.println(actionMap);
//        System.out.println(sortReqMap);
//        try {
//            conn = DriverManager.getConnection(DB_URL,USER,PASS);
//            stmt = conn.createStatement();
//            // 获取所有信息
//            String sql;
//            sql = "select userid,action,sortreq,category,item,time from action;";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            while (rs.next()){
//                String userId = rs.getString("userid");
//                String action = rs.getString("action");
//                String sortreq = rs.getString("sortreq");
//                String category = rs.getString("category");
//                String item = rs.getString("item");
//                String time = rs.getString("time");
//                int newUserId = insertIfNotExist(userIdMap,userId);
//                int newAction = actionMap.getOrDefault(action,0);
//                int newSortReq = sortReqMap.getOrDefault(sortreq,0);
//                int newCategory = insertIfNotExist(categoryMap,category);
//                int newItem = insertIfNotExist(itemMap,item);
//                String newSql = String.format("INSERT INTO new_action (userid,action,sortreq,category,item,time) VALUES (%d, %d,%d,%d,%d,'%s');",
//                        newUserId,newAction,newSortReq,newCategory,newItem,time);
//                System.out.println(newSql);
//                stmt.addBatch(newSql);
//            }
//            stmt.execute("truncate table new_action;");
//            stmt.executeBatch();
//            stmt.clearBatch();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }
    private static HashMap<String,Integer> getActionMap(){
        HashMap<String,Integer> action = new HashMap<>();
        action.put("浏览商品",1);
        action.put("关键字检索",2);
        action.put("加入购物车",3);
        action.put("浏览品类",4);
        action.put("订单",5);
        action.put("支付",6);
        action.put("浏览评论",7);
        action.put("领劵",8);
        return action;
    }

    private static int insertIfNotExist(HashMap<String,Integer> hashMap,String key){
        int newValue = 0;
        if (hashMap.containsKey(key)){
            newValue = hashMap.get(key);
        }else {
            final double d = Math.random();
            final int randomInt = (int)(d*1000000);
            hashMap.put(key,randomInt);
            newValue = randomInt;
        }
        return newValue;
    }

    private static HashMap<String,Integer> getSortReqMap(){
        HashMap<String,Integer> sortReq = new HashMap<>();
        sortReq.put("综合排序",1);
        sortReq.put("综合",1);
        sortReq.put("默认",1);

        sortReq.put("销量优先",2);
        sortReq.put("销量",2);
        sortReq.put("排序：销量;",2);
        sortReq.put("销量排序",2);

        sortReq.put("价格最高",3);
        sortReq.put("价格由高到低",3);
        sortReq.put("价格从高到低",3);
        sortReq.put("排序：价格降序;",3);

        sortReq.put("价格最低",4);
        sortReq.put("价格从低到高",4);
        sortReq.put("价格由低到高",4);
        sortReq.put("排序：价格升序;",4);
        sortReq.put("价格升序",4);

        sortReq.put("最新上架",5);
        sortReq.put("新上",5);
        sortReq.put("排序：上新;",5);
        sortReq.put("上新排序",5);

        sortReq.put("店铺排序",6);

        sortReq.put("评价最多",7);

        sortReq.put("信用排序",8);

        sortReq.put("排序：促销;",9);
        return sortReq;
    }
}
