package com.shui.server.service;

import com.shui.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@EnableAsync
public class MailService {

    private static final Logger log= LoggerFactory.getLogger(MailService.class);

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Environment env;


    /**
     *  发送简单文本文件
     */
    @Async
    public void sendSimpleEmail(final MailDto dto){
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(env.getProperty("mail.send.from"));
            message.setTo(dto.getTos());
            message.setSubject(dto.getSubject());
            message.setText(dto.getContent());

            mailSender.send(message);
            log.info("发送简单文本文件-发送成功!");

        }catch (Exception e){
            log.error("发送简单文本文件-发生异常： ",e.fillInStackTrace());
        }
    }

    /**
     *  发送花哨邮件
     */
    @Async
    public void sendHTMLMail(final MailDto dto){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true,"utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(dto.getTos());
            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getContent(),true);

            mailSender.send(message);
            log.info("发送花哨邮件-发送成功!");
        }catch (Exception e){
            log.error("发送花哨邮件-发生异常：", e.fillInStackTrace());
        }
    }
}































