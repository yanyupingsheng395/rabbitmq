package com.tuling.entity;

import lombok.Data;

/**
 * 商品表
 */
@Data
public class ProductInfo {
    //商品ID
    private Integer productNo;
    //商品名称
    private String productName;
    //商品数量
    private String productNum;
}
