package com.shui.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Item {

    private Integer id;
    private String name;
    private String code;
    // 库存
    private Long stock;
    // 采购时间
    private Date purchaseTime;
    private Integer isActive;
    private Date createTime;
    private Date updateTime;

}