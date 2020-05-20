package com.tuling.mapper;

import com.tuling.entity.OrderInfo;

/**
* @desc: 类的描述:订单信息mapper
* @version: 1.0
 *
*/
public interface OrderInfoMapper {

    /**
     * 方法实现说明:订单保存
     * @param orderInfo:订单实体
     */
    int saveOrderInfo(OrderInfo orderInfo);
}
