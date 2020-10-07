package com.shui.server.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 *  ZooKeeper组件自定义配置
 */
@Configuration
public class ZooKeeperConfig {

    @Autowired
    private Environment env;


    /**
     *  自定义注入ZooKeeper客户端操作实例
     */
    @Bean
    public CuratorFramework curatorFramework(){
        CuratorFramework curatorFramework=CuratorFrameworkFactory.builder()
                .connectString(env.getProperty("zk.host"))
                .namespace(env.getProperty("zk.namespace"))
                //重试策略
                .retryPolicy(new RetryNTimes(5,1000))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }


}
































