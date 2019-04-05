package com.company.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCategory extends StandardCategory {
    private String productName;

    public ProductCategory(String productName, double thirdCategoryId) {
        this.productName = productName;
        setThirdCateId((int) thirdCategoryId);
    }

    public void copyCategory(StandardCategory other) {
        if (null == other) {
            return;
        }

        setFirstCate(other.getFirstCate());
        setFirstCateId(other.getFirstCateId());
        setSecondCate(other.getSecondCate());
        setSecondCateId(other.getSecondCateId());
        setThirdCate(other.getThirdCate());
        setThirdCateId(other.getThirdCateId());

    }
}
