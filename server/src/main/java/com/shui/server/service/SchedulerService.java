package com.shui.server.service;

import com.shui.entity.ItemKillSuccess;
import com.shui.server.mapper.ItemKillSuccessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  定时任务服务
 *  缺点：这样会频繁扫描数据库，数据量大时压力山大
 */
@Slf4j
@Service
public class SchedulerService {

    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    Environment env;

    /**
     *  定时获取status=0的订单并判断是否超过TTL，然后进行失效
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void schedulerExpireOrders(){

        try {
            // 批量获取秒杀成功未付款的订单信息
            List<ItemKillSuccess> list = itemKillSuccessMapper.selectExpireOrders();
            if (list != null && !list.isEmpty()) {
                // 批量失效秒杀成功未付款的订单信息
                list.stream().forEach(itemKillSuccess -> {
                    if (itemKillSuccess != null && itemKillSuccess.getDiffTime() > env.getProperty("scheduler.expire.orders.time", Integer.class)){
                        itemKillSuccessMapper.expireOrder(itemKillSuccess.getCode());
                    }
                });
            }
        } catch (Exception e){
            log.error("定时获取status=0的订单并判断是否超过TTL，然后进行失效-发生异常：",e.fillInStackTrace());
        }
    }

//    @Scheduled(cron = "0/11 * * * * ?")
//    public void schedulerExpireOrdersV2(){
//        log.info("v2的定时任务----");
//    }


//    @Scheduled(cron = "0/10 * * * * ?")
//    public void schedulerExpireOrdersV3(){
//        log.info("v3的定时任务----");
//    }

}




































