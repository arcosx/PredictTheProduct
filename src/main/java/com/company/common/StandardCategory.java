package com.company.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardCategory implements Serializable {
    /**
     * 三级分类ID
     */
    private int thirdCateId;

    /**
     * 三级分类名称
     */
    private String thirdCate;

    /**
     * 二级分类ID
     */
    private int secondCateId;

    /**
     * 二级分类名称
     */
    private String secondCate;

    /**
     * 一级分类ID
     */
    private int firstCateId;

    /**
     * 一级分类名称
     */
    private String firstCate;
}
