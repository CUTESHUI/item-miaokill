package com.shui.server.service;

import com.shui.model.dto.KillSuccessUserInfo;
import com.shui.model.mapper.ItemKillSuccessMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *  RabbitMQ发送邮件服务
 */
@Service
public class RabbitSenderService {

    public static final Logger log= LoggerFactory.getLogger(RabbitSenderService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Environment env;

    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;


    /**
     *  秒杀成功异步发送邮件通知消息
     */
    public void sendKillSuccessEmailMsg(String orderNo){
        log.info("秒杀成功异步发送邮件通知消息-准备发送消息：{}",orderNo);

        try {
            if (StringUtils.isNotBlank(orderNo)){
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
                if (info!=null){
                    // rabbitmq发送消息的逻辑
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));

                    // 将info充当消息发送至队列
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties=message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,KillSuccessUserInfo.class);
                            return message;
                        }
                    });
                }
            }
        }catch (Exception e){
            log.error("秒杀成功异步发送邮件通知消息-发生异常，消息为：{}",orderNo,e.fillInStackTrace());
        }
    }


    /**
     *  秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单
     */
    public void sendKillSuccessOrderExpireMsg(final String orderCode){
        try {
            if (StringUtils.isNotBlank(orderCode)){
                KillSuccessUserInfo info=itemKillSuccessMapper.selectByCode(orderCode);
                if (info!=null){
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties mp=message.getMessageProperties();
                            mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            mp.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,KillSuccessUserInfo.class);

                            //TODO：动态设置TTL(为了测试方便，暂且设置10s)
                            mp.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));
                            return message;
                        }
                    });
                }
            }
        }catch (Exception e){
            log.error("秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单-发生异常，消息为：{}",orderCode,e.fillInStackTrace());
        }
    }

}




























