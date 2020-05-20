package com.tuling.bo;

import lombok.Data;

import java.io.Serializable;

/**
* @desc: 类的描述:消息文本对象
* @version: 1.0
 *
*/
@Data
public class MsgTxtBo implements Serializable {
    //订单id
    private long orderNo;
    //商品id
    private int productNo;
    //消息id
    private String msgId;
}
