package com.tuling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tuling.entity.MessageContent;
import com.tuling.entity.OrderInfo;
import org.springframework.core.annotation.Order;

/**
* @desc: 类的描述:订单服务业务逻辑类
* @version: 1.0
*/
public interface IOrderInfoService {

    /**
     * 方法实现说明:订单保存
     * @param orderInfo:订单实体
     */
    void saveOrderInfo(OrderInfo orderInfo, MessageContent messageContent);

    void saveOrderInfoWithMessage(OrderInfo orderInfo) throws JsonProcessingException;
}
