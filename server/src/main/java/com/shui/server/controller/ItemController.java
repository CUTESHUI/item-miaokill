package com.shui.server.controller;

import com.shui.entity.ItemKill;
import com.shui.server.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
public class ItemController {

    private static final String PREFIX = "item";

    @Autowired
    private ItemService itemService;

    /**
     *  秒杀商品列表
     */
    @GetMapping({"/", "/index", PREFIX+"/index", PREFIX+"/list"})
    public String list(ModelMap modelMap) {
        try {
            // 获取秒杀商品列表
            List<ItemKill> list = itemService.getKillItems();
            modelMap.put("list",list);

        } catch (Exception e) {
            log.error("获取秒杀商品列表-发生异常：", e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "list";
    }

    /**
     *  秒杀商品详情
     */
    @GetMapping(PREFIX+"/detail/{id}")
    public String detail(@PathVariable("id") Integer id, ModelMap modelMap) {
        if (id == null || id <= 0){
            return "redirect:/base/error";
        }
        try {
            // 获取秒杀商品详情
            ItemKill detail = itemService.getKillDetail(id);
            modelMap.put("detail",detail);

        } catch (Exception e) {
            log.error("获取秒杀商品详情-发生异常：", e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "info";
    }

}
