package com.shui.model.mapper;

import com.shui.model.entity.ItemKill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemKillMapper {

    // 获取所有待秒杀商品列表
    List<ItemKill> selectAll();

    // 获取待秒杀商品详情
    ItemKill selectById(@Param("id") Integer id);

    // 秒杀商品，剩余数量减1
    int updateKillItem(@Param("killId") Integer killId);

    //
    ItemKill selectByIdV2(@Param("id") Integer id);

    //
    int updateKillItemV2(@Param("killId") Integer killId);
}