package com.tuling.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tuling.bo.MsgTxtBo;
import com.tuling.compent.MsgSender;
import com.tuling.constants.MqConst;
import com.tuling.entity.MessageContent;
import com.tuling.entity.OrderInfo;
import com.tuling.enumuration.MsgStatusEnum;
import com.tuling.mapper.MsgContentMapper;
import com.tuling.mapper.OrderInfoMapper;
import com.tuling.service.IOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
* @desc: 类的描述
 *
*/
@Slf4j
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Autowired
    private MsgSender msgSender;

    /**
     * 1、构建消息，
     * 2、保存数据到数据库中，
     * 3、并发送消息
     * @param orderInfo
     * @throws JsonProcessingException
     */
    public void saveOrderInfoWithMessage(OrderInfo orderInfo) throws JsonProcessingException {
        //构建消息对象
        MessageContent messageContent = builderMessageContent(orderInfo.getOrderNo(),orderInfo.getProductNo());
        //保存数据库
        saveOrderInfo(orderInfo,messageContent);
        //构建消息发送对象
        MsgTxtBo msgTxtBo = new MsgTxtBo();
        msgTxtBo.setMsgId(messageContent.getMsgId());
        msgTxtBo.setOrderNo(orderInfo.getOrderNo());
        msgTxtBo.setProductNo(orderInfo.getProductNo());
        //发送消息
        msgSender.senderMsg(msgTxtBo);
    }

    /**
     * 将订单信息插入订单表，将消息信息插入消息表
     * @param orderInfo:订单实体
     * @param messageContent
     */
    @Transactional
    @Override
    public void saveOrderInfo(OrderInfo orderInfo,MessageContent messageContent) {
        try {
            //将订单信息插入订单表
            orderInfoMapper.saveOrderInfo(orderInfo);
            //插入消息表
            msgContentMapper.saveMsgContent(messageContent);
        }catch (Exception e) {
            log.error("操作数据库失败:{}",e);
            throw new RuntimeException("操作数据库失败");
        }
    }

    /**
     * 方法实现说明:构建消息对象
     * @return:MessageContent 消息实体
     */
    private MessageContent builderMessageContent(long orderNo,Integer productNo) {
        MessageContent messageContent = new MessageContent();
        String msgId = UUID.randomUUID().toString();
        //消息ID
        messageContent.setMsgId(msgId);
        //消息产生时间
        messageContent.setCreateTime(new Date());
        //消息更新时间
        messageContent.setUpdateTime(new Date());
        //消息发送到的交换机
        messageContent.setExchange(MqConst.ORDER_TO_PRODUCT_EXCHANGE_NAME);
        //消息发送的队列名称
        messageContent.setRoutingKey(MqConst.ORDER_TO_PRODUCT_QUEUE_NAME);
        //消息发送的状态码（为0，正在发送中）
        messageContent.setMsgStatus(MsgStatusEnum.SENDING.getCode());
        //订单ID
        messageContent.setOrderNo(orderNo);
        //商品ID
        messageContent.setProductNo(productNo);
        //消息发送失败后，重发的最大次数
        messageContent.setMaxRetry(MqConst.MSG_RETRY_COUNT);
        return messageContent;
    }
}
