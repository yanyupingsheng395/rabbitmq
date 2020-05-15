package com.tuling.entity;

import lombok.Data;

import java.util.Date;

/**
* @desc: 类的描述:消息日志表
* @version: 1.0
*/
@Data
public class MessageContent {

    //设置消息ID，UUID随机数
    private String msgId;
    //订单编号
    private long orderNo;
    //消息创建时间
    private Date createTime;
    //消息更新时间
    private Date updateTime;
    //消息的状态，(0,"发送中"),(1,"mq的broker确认接受到消息"),(2,"没有对应交换机"),(3,"没有对应的路由"),(4,"消费端成功消费消息")',
    private Integer msgStatus;
    //消息操作的交换机名称
    private String exchange;
    //消息对应的路由key
    private String routingKey;
    //消息发送失败错误原因
    private String errCause;
    //消息重发的最大次数
    private Integer maxRetry;
    //消息重发的次数
    private Integer currentRetry=0;
    //商品ID
    private Integer productNo;
}
