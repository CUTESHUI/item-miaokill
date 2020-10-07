package com.shui.server.config;/**
 * Created by Administrator on 2019/6/29.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 定时任务多线程处理的通用化配置
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/29 21:45
 **/
@Configuration
public class SchedulerConfig implements SchedulingConfigurer{

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }
}