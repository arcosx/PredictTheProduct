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

    public ProductCategory copyCategory(StandardCategory other) {
        if (null == other) {
            return this;
        }

        setFirstCate(other.getFirstCate());
        setFirstCateId(other.getFirstCateId());
        setSecondCate(other.getSecondCate());
        setSecondCateId(other.getSecondCateId());
        setThirdCate(other.getThirdCate());
        setThirdCateId(other.getThirdCateId());

        return this;
    }
}
