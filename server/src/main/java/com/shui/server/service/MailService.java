package com.shui.server.service;

import com.shui.dto.MailDto;

public interface MailService {

    /**
     * 发送简单文本文件
     */
    void sendSimpleEmail(final MailDto dto);

    /**
     * 发送花哨邮件
     */
    void sendHtmlMail(final MailDto dto);
}
