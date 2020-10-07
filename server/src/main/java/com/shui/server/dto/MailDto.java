package com.shui.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MailDto implements Serializable{

    private String subject; //邮件主题
    private String content; //邮件内容
    private String[] tos;   //接收人
}