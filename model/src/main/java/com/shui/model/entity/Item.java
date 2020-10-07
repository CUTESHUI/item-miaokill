package com.shui.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Item {

    private Integer id;
    private String name;
    private String code;
    private Long stock;         // 库存
    private Date purchaseTime;  // 采购时间
    private Integer isActive;
    private Date createTime;
    private Date updateTime;

}