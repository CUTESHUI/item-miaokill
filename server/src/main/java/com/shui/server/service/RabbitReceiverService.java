package com.shui.server.service;

import com.shui.dto.KillSuccessUserInfo;
import com.shui.entity.ItemKillSuccess;
import com.shui.server.mapper.ItemKillSuccessMapper;
import com.shui.dto.MailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ接收消息服务
 */
@Slf4j
@Service
public class RabbitReceiverService {

    @Autowired
    private MailService mailService;
    @Autowired
    private Environment env;
    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀成功
     * 异步接收邮件通知
     */
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"}, containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info){
        try {
            log.info("秒杀异步邮件通知-接收消息:{}", info);

            final String content = String.format(env.getProperty("mail.kill.item.success.content"), info.getItemName(), info.getCode());
            MailDto mailDto = new MailDto(env.getProperty("mail.kill.item.success.subject"), content, new String[]{info.getEmail()});
            mailService.sendHtmlMail(mailDto);

        }catch (Exception e){
            log.error("秒杀异步邮件通知-接收消息-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 用户秒杀成功后超时未支付
     * 监听者
     */
    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"}, containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(KillSuccessUserInfo info){
        try {
            log.info("用户秒杀成功后超时未支付-监听者-接收消息:{}", info);

            if (info != null){
                ItemKillSuccess entity = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                // status=0：成功未付款
                if (entity != null && entity.getStatus().intValue() == 0){
                    itemKillSuccessMapper.expireOrder(info.getCode());
                }
            }
        }catch (Exception e){
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：", e.fillInStackTrace());
        }
    }
}












