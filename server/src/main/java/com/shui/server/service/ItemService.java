package com.shui.server.service;

import com.shui.entity.ItemKill;

import java.util.List;

public interface ItemService {
    /**
     * 待秒杀商品列表
     * 剩余数量 > 0，处于秒杀时段
     */
    List<ItemKill> getKillItems() throws Exception;

    ItemKill getKillDetail(Integer id) throws Exception;
}
