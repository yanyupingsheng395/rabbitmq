package com.tuling.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuling.bo.MsgTxtBo;
import com.tuling.entity.MessageContent;
import com.tuling.enumuration.MsgStatusEnum;
import com.tuling.mapper.MsgContentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
* @desc: 类的描述:消息不可达监听，对应图中第三步后面那步（N：消息不可达）多常见于没有队列接收。。
* @version: 1.0
 *
*/
@Component
@Slf4j
public class TulingMsgRetrunListener implements RabbitTemplate.ReturnCallback {

    @Autowired
    private MsgContentMapper msgContentMapper;

    /**
     * 消息不可达，调用returnListener监听
     * @param message     发送的消息
     * @param replyCode   失败的回应码
     * @param replyText   返回回应的消息
     * @param exchange   交换机名称
     * @param routingKey 路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            //将返回的失败消息，转成消息文本对象实体，然后重新构建消息，并更新消息状态
            MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(),MsgTxtBo.class);
            log.info("无法路由消息内容:{},cause:{}",msgTxtBo,replyText);
            //构建消息对象
            MessageContent messageContent = new MessageContent();
            //消息失败原因
            messageContent.setErrCause(replyText);
            messageContent.setUpdateTime(new Date());
            //将消息状态改为2
            messageContent.setMsgStatus(MsgStatusEnum.SENDING_FAIL.getCode());
            messageContent.setMsgId(msgTxtBo.getMsgId());
            //更新消息表，将状态改为2，发送失败。等待分布式定时任务查到，在重新发送消息。
            msgContentMapper.updateMsgStatus(messageContent);
        }catch (Exception e) {
            log.error("更新消息表异常:{}",e);
        }
    }
}
