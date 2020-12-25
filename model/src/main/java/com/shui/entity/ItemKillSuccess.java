package com.shui.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class ItemKillSuccess {

    private String code;
    private Integer itemId;
    private Integer killId;
    private String userId;
    private Byte status;
    private Date createTime;
    /**
     *  当前时间与下单时间之差
     */
    private Integer diffTime;
}