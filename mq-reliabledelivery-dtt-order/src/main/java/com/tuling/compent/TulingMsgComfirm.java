package com.tuling.compent;

import com.tuling.entity.MessageContent;
import com.tuling.enumuration.MsgStatusEnum;
import com.tuling.mapper.MsgContentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @desc: 类的描述:消息确认组件，判断消息是否已经发送成功。对应图中的（Y：消息可达）
* @version: 1.0
*/
@Component
@Slf4j
public class TulingMsgComfirm implements RabbitTemplate.ConfirmCallback{

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String msgId = correlationData.getId();
        //查看消息应答，如果ack为A，表示mq接收到消息。
        if(ack) {
            log.info("消息Id:{}对应的消息被broker签收成功",msgId);
            updateMsgStatusWithAck(msgId);
        }else{
            log.warn("消息Id:{}对应的消息被broker签收失败:{}",msgId,cause);
            updateMsgStatusWithNack(msgId,cause);
        }
    }

    /**
     * 消息发送成功，更新消息表。
     * @param msgId:消息ID
     */
    private void updateMsgStatusWithAck(String msgId) {
        MessageContent messageContent = builderUpdateContent(msgId);
        messageContent.setMsgStatus(MsgStatusEnum.SENDING_SUCCESS.getCode());
        msgContentMapper.updateMsgStatus(messageContent);
    }

    /**
     * 消息发送失败后，更新数据库中消息表状态为2
     * @param msgId
     * @param cause
     */
    private void updateMsgStatusWithNack(String msgId,String cause){
        MessageContent messageContent = builderUpdateContent(msgId);
        messageContent.setMsgStatus(MsgStatusEnum.SENDING_FAIL.getCode());
        messageContent.setErrCause(cause);
        msgContentMapper.updateMsgStatus(messageContent);
    }

    /**
     * 构建消息
     * @param msgId
     * @return
     */
    private MessageContent builderUpdateContent(String msgId) {
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgId(msgId);
        messageContent.setUpdateTime(new Date());
        return messageContent;
    }

}
