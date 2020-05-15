package com.tuling.compent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.tuling.bo.MsgTxtBo;
import com.tuling.entity.MessageContent;
import com.tuling.enumration.MsgStatusEnum;
import com.tuling.exception.BizExp;
import com.tuling.mapper.MsgContentMapper;
import com.tuling.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 *这个类模拟第6步，消费者消费消息。
 * 有两种情况：
 * 1.在减库存的时候，发生异常。减库存的方法中有事务，那么事务回滚，不扣除库存，并update消息状态为失败。后续由定时任务重新发送消息。
 * 2.库存扣减成功，还得将消息状态改为成功。消费成功后，会给生产者应答ack，生产者的confirmListener一直在监听，
 *      发生网络抖动，应答的时候失败。就会走catch里面。将状态改成4.因为已经扣减库存了。定时任务会查消息状态不是3的消息，这样还会发送消息。造成消息多发。
 */
@Component
@Slf4j
public class MqConsumer {

    /**队列名称*/
    public static final String ORDER_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";
    //分布式锁。
    public static final String LOCK_KEY="LOCK_KEY";

    @Autowired
    private IProductService productService;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 没有加分布式锁的版本,可能存在重复消费问题
     * @param message
     * @param channel
     * @throws IOException*/

    @RabbitListener(queues = {ORDER_TO_PRODUCT_QUEUE_NAME})
    @RabbitHandler
    public void consumerMsg(Message message, Channel channel) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        //获取消息
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(),MsgTxtBo.class);

        log.info("消费消息:{}",msgTxtBo);
        Long deliveryTag = (Long) message.getMessageProperties().getDeliveryTag();

        try {
            //扣减库存，如果扣减成功。那么将消息状态变成3（消费成功！）
            productService.updateProductStore(msgTxtBo);

            //System.out.println(1/0);
            //消息应答。告诉生产者消费成功。
            channel.basicAck(deliveryTag,false);
        }catch (Exception e) {
            //更新msg表为消费失败
            //更新消息表状态
            MessageContent messageContent = new MessageContent();
            messageContent.setMsgId(msgTxtBo.getMsgId());
            messageContent.setUpdateTime(new Date());
            messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
            msgContentMapper.updateMsgStatus(messageContent);

            channel.basicReject(deliveryTag,false);
        }
    }


  /*  @RabbitListener(queues = {ORDER_TO_PRODUCT_QUEUE_NAME})
    @RabbitHandler
    public void consumerMsgWithLock(Message message, Channel channel) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        MsgTxtBo msgTxtBo = objectMapper.readValue(message.getBody(), MsgTxtBo.class);
        Long deliveryTag = (Long) message.getMessageProperties().getDeliveryTag();

        if (redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + msgTxtBo.getMsgId(), msgTxtBo.getMsgId())) {
            log.info("消费消息:{}", msgTxtBo);
            try {
                //更新消息表也业务表
                productService.updateProductStore(msgTxtBo);
                //消息签收
                System.out.println(1/0);
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                *//**
                 * 更新数据库异常说明业务没有操作成功需要删除分布式锁
                 *//*
                               if (e instanceof BizExp) {
                    BizExp bizExp = (BizExp) e;
                    log.info("数据业务异常:{},即将删除分布式锁", bizExp.getErrMsg());
                    //删除分布式锁
                    redisTemplate.delete(LOCK_KEY);
                }

                //更新消息表状态
                MessageContent messageContent = new MessageContent();
                messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
                messageContent.setUpdateTime(new Date());
                messageContent.setErrCause(e.getMessage());
                messageContent.setMsgId(msgTxtBo.getMsgId());
                msgContentMapper.updateMsgStatus(messageContent);
                channel.basicReject(deliveryTag,false);
            }

        } else {
            log.warn("请不要重复消费消息{}", msgTxtBo);
            channel.basicReject(deliveryTag,false);
        }

    }*/
}
