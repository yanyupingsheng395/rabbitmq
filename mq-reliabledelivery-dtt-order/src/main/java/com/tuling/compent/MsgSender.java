package com.tuling.compent;

import com.tuling.bo.MsgTxtBo;
import com.tuling.constants.MqConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
* @desc: 类的描述:消息发送组件
 *
*/
@Component
@Slf4j
public class MsgSender implements InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TulingMsgComfirm tulingMsgComfirm;

    @Autowired
    private TulingMsgRetrunListener tulingMsgRetrunListener;

    /**
     * 方法实现说明:真正的发送消息
     * @param msgTxtBo:发送的消息对象
     */
    public  void senderMsg(MsgTxtBo msgTxtBo){
        log.info("发送的消息ID:{}",msgTxtBo.getMsgId());
        //这个值必须唯一，这是在消息失败后，用于识别消息的唯一ID
        CorrelationData correlationData = new CorrelationData(msgTxtBo.getMsgId());
        //发送消息。
        rabbitTemplate.convertAndSend(MqConst.ORDER_TO_PRODUCT_EXCHANGE_NAME,MqConst.ORDER_TO_PRODUCT_ROUTING_KEY,msgTxtBo,correlationData);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(tulingMsgComfirm);
        rabbitTemplate.setReturnCallback(tulingMsgRetrunListener);
        //设置消息转换器，将失败原因转成json格式
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
    }
}
