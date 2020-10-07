package com.shui.server.controller;

import com.shui.kill.api.enums.StatusCode;
import com.shui.kill.api.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("base")
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @GetMapping("/welcome")
    public String welcome(String msg, ModelMap modelMap) {
        if (StringUtils.isNotBlank(msg)) {
            msg = "这是welcome,hello";
        }
        modelMap.put("name", msg);
        return "welcome";
    }

    @GetMapping("/data")
    @ResponseBody
    public String data(String msg){
        if (StringUtils.isBlank(msg)){
            msg="这是welcome!";
        }
        return msg;
    }

    @GetMapping("/response")
    @ResponseBody
    public BaseResponse response(String msg){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        if (StringUtils.isBlank(msg)){
            msg="这是 response test!";
        }
        response.setData(msg);
        return response;
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
