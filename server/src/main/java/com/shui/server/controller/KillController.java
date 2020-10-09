package com.shui.server.controller;

import com.shui.kill.api.enums.StatusCode;
import com.shui.kill.api.response.BaseResponse;
import com.shui.model.dto.KillSuccessUserInfo;
import com.shui.model.mapper.ItemKillSuccessMapper;
import com.shui.server.dto.KillDto;
import com.shui.server.service.KillService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@Controller
public class KillController {

    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    private static final String PREFIX = "kill";

    @Autowired
    KillService killService;

    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     *  商品秒杀核心业务逻辑
     */
    @RequestMapping(value = PREFIX+"/execute",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        // 非法参数
        if (result.hasErrors() || dto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Object uId = session.getAttribute("uid");
        if (uId == null){
            return new BaseResponse(StatusCode.UserNotLogin);
        }
        Integer userId = (Integer)uId ;

        try {
            // 秒杀商品，默认false
            Boolean res = killService.killItemV5(dto.getKillId(), userId);
            // 如果成功true，不会进循环
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"商品已抢购完毕！！或者不在抢购时间段！!");
            }
        }catch (Exception e){ // 您已经抢购过该商品了!
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     *  查看订单详情
     */
    @GetMapping(PREFIX+"/record/detail/{orderNo}")
    public String killRecordDetail(@PathVariable String orderNo, ModelMap modelMap){
        if (StringUtils.isBlank(orderNo)){
            return "error";
        }
        KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
        if (info == null){
            return "error";
        }
        modelMap.put("info",info);
        return "killRecord";
    }


    // 抢购成功跳转页面
    @GetMapping(PREFIX+"/execute/success")
    public String executeSuccess(){
        return "executeSuccess";
    }

    // 抢购失败跳转页面
    @GetMapping(PREFIX+"/execute/fail")
    public String executeFail(){
        return "executeFail";
    }


    /**
     *  商品秒杀核心业务逻辑
     *  用于压力测试
     */
    @RequestMapping(value = PREFIX+"/execute/lock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse executeLock(@RequestBody @Validated KillDto dto, BindingResult result){
        if (result.hasErrors() || dto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            // 不加分布式锁的前提
//            Boolean res=killService.killItemV2(dto.getKillId(),dto.getUserId());
//            if (!res){
//                return new BaseResponse(StatusCode.Fail.getCode(),"不加分布式锁-商品已抢购完毕！！或者不在抢购时间段！!");
//            }

            // 基于Redis的分布式锁进行控制
//            Boolean res=killService.killItemV3(dto.getKillId(),dto.getUserId());
//            if (!res){
//                return new BaseResponse(StatusCode.Fail.getCode(),"基于Redis的分布式锁进行控制-商品已抢购完毕！！或者不在抢购时间段！!");
//            }

            // 基于Redisson的分布式锁进行控制
            /*Boolean res=killService.killItemV4(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"基于Redisson的分布式锁进行控制-商品已抢购完毕！！或者不在抢购时间段！!");
            }*/

            // 基于ZooKeeper的分布式锁进行控制
            Boolean res = killService.killItemV5(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"基于ZooKeeper的分布式锁进行控制-商品已抢购完毕！！或者不在抢购时间段！!");
            }

        }catch (Exception e){
            response = new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


}