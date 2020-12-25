package com.shui.enums;

/**
 *  系统级别的常量
 */
public class SysConstant {

    public enum OrderStatus{

        // 无效
        Invalid(-1,"无效"),
        // 成功未付款
        SuccessNotPayed(0,"成功-未付款"),
        // 已付款
        HasPayed(1,"已付款"),
        // 已取消
        Cancel(2,"已取消"),

        ;

        private Integer code;
        private String msg;

        OrderStatus(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}