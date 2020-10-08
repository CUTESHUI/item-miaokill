package com.shui.server.service.impl;

import com.shui.model.entity.ItemKill;
import com.shui.model.entity.ItemKillSuccess;
import com.shui.model.mapper.ItemKillMapper;
import com.shui.model.mapper.ItemKillSuccessMapper;
import com.shui.server.enums.SysConstant;
import com.shui.server.service.KillService;
import com.shui.server.service.RabbitSenderService;
import com.shui.server.utils.RandomUtil;
import com.shui.server.utils.SnowFlake;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class KillServiceImpl implements KillService {

    private static final Logger log= LoggerFactory.getLogger(KillService.class);

    private static final String pathPrefix = "/kill/zkLock/";

    private SnowFlake snowFlake = new SnowFlake(2,3);

    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    ItemKillMapper itemKillMapper;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitSenderService rabbitSenderService;

    @Autowired
    CuratorFramework curatorFramework;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     *  商品秒杀核心业务逻辑的处理
     *  秒杀商品
     */
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        // 判断当前用户是否已经抢购过当前商品
        // <= 0 否
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0){
            // 查询待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectById(killId);
            // 判断是否可以被秒杀
            if (itemKill != null && 1 == itemKill.getCanKill() ){
                // 扣减库存，减一
                int res = itemKillMapper.updateKillItem(killId);
                // 扣减成功：生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res > 0){
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result = true;
                }
            }
        } else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     *  通用的方法
     *  记录用户秒杀成功后生成的订单
     *  进行异步邮件消息的通知
     */
    private void commonRecordKillSuccessInfo(ItemKill kill,Integer userId) throws Exception{
        // 记录抢购成功后生成的秒杀订单记录
        ItemKillSuccess entity = new ItemKillSuccess();
        String orderNo = String.valueOf(snowFlake.nextId());

        //entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数
        entity.setCode(orderNo); //雪花算法
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());
        // 更新秒杀成功的表
        if (itemKillSuccessMapper.countByKillUserId(kill.getId(), userId) <= 0){
            int res = itemKillSuccessMapper.insertSelective(entity);
            if (res > 0){
                // 进行异步邮件消息的通知 = rabbitmq+mail
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);
                // 入死信队列，用于 “失效” 超过指定的TTL时间时仍然未支付的订单
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }
    }

    /**
     *  商品秒杀核心业务逻辑的处理
     *  mysql的优化
     */
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        // 判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            // A.查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectByIdV2(killId);

            // 判断是否可以被秒杀
            if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0){
                // B.扣减库存-减一
                int res=itemKillMapper.updateKillItemV2(killId);
                // 扣减是否成功? 是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res > 0){
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     *  商品秒杀核心业务逻辑的处理
     *  redis的分布式锁
     */
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

            // 借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
            ValueOperations valueOperations=stringRedisTemplate.opsForValue();
            final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value = RandomUtil.generateOrderCode();
            Boolean cacheRes = valueOperations.setIfAbsent(key,value); //luna脚本提供“分布式锁服务”，就可以写在一起
            // redis部署节点宕机了
            if (cacheRes){
                stringRedisTemplate.expire(key,30, TimeUnit.SECONDS);

                try {
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);

                            result=true;
                        }
                    }
                }catch (Exception e){
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                }finally {
                    if (value.equals(valueOperations.get(key).toString())){
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }else{
            throw new Exception("Redis-您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     *  商品秒杀核心业务逻辑的处理
     *  redisson的分布式锁
     */
    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        final String lockKey=new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock=redissonClient.getLock(lockKey);

        try {
            Boolean cacheRes=lock.tryLock(30,10,TimeUnit.SECONDS);
            if (cacheRes){
                // 核心业务逻辑的处理
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);

                            result=true;
                        }
                    }
                }else{
                    throw new Exception("redisson-您已经抢购过该商品了!");
                }
            }
        }finally {
            lock.unlock();
            //lock.forceUnlock();
        }
        return result;
    }

    /**
     *  商品秒杀核心业务逻辑的处理
     *  基于ZooKeeper的分布式锁
     */
    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        InterProcessMutex mutex=new InterProcessMutex(curatorFramework,pathPrefix+killId+userId+"-lock");
        try {
            if (mutex.acquire(10L,TimeUnit.SECONDS)){
                // 核心业务逻辑
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }else{
                    throw new Exception("zookeeper-您已经抢购过该商品了!");
                }
            }
        }catch (Exception e){
            throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
        }finally {
            if (mutex != null){
                mutex.release();
            }
        }
        return result;
    }

}








































