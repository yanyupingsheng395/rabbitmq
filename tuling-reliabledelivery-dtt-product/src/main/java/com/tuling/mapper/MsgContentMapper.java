package com.tuling.mapper;

import com.tuling.entity.MessageContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @desc: 类的描述:消息内容mapper
* @version: 1.0
*/
public interface MsgContentMapper {

    /**
     * 方法实现说明:保存消息
     * @param messageContent:消息对象
     * @return:
     */
    int saveMsgContent(MessageContent messageContent);

    /**
     * 更新消息状态
     * @param messageContent
     * @return
     */
    int updateMsgStatus(MessageContent messageContent);

    List<MessageContent> qryNeedRetryMsg(@Param("msgStatus") Integer status, @Param("timeDiff") Integer timeDiff);

    void updateMsgRetryCount(String msgId);


}
