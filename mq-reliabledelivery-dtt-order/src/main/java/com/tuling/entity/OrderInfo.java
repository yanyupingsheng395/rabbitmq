package com.tuling.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
* @desc: 类的描述:订单实体
* @version: 1.0
 *
*/
@ToString
@Getter
@Setter
public class OrderInfo {
    //订单ID
    private long orderNo;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //购买者名字
    private String userName;
    //金额
    private double money;
    //商品ID
    private Integer productNo;
}
