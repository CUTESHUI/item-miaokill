package com.shui.model.mapper;

import com.shui.model.dto.KillSuccessUserInfo;
import com.shui.model.entity.ItemKillSuccess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemKillSuccessMapper {

    int deleteByPrimaryKey(String code);

    int insert(ItemKillSuccess record);

    int insertSelective(ItemKillSuccess record);

    ItemKillSuccess selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(ItemKillSuccess record);

    int updateByPrimaryKey(ItemKillSuccess record);

    // 根据秒杀活动跟用户Id，查询用户的抢购数量
    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    KillSuccessUserInfo selectByCode(@Param("code") String code);

    int expireOrder(@Param("code") String code);

    List<ItemKillSuccess> selectExpireOrders();
}