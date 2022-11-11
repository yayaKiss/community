package com.newCoder.community.util;


import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-10 20:55
 * @Desc
 */
public class JsonResult extends HashMap<String,Object> {

    public JsonResult(){
        put("code",200);
        put("msg","success");
    }

    public JsonResult put(String key, Object value){
        super.put(key,value);
        return this;
    }
    public Integer getCode(){
        return (Integer) this.get("code");
    }

    public static JsonResult ok(){
        return new JsonResult();
    }
    public static JsonResult ok(String msg){
        JsonResult res = new JsonResult();
        res.put("msg",msg);
        return res;
    }
    public static JsonResult ok(Map<String,Object> map){
        JsonResult res = new JsonResult();
        res.putAll(map);
        return res;
    }
    public static JsonResult error(){
        return error(500,"未知异常，请联系管理员");
    }
    //状态码固定，是什么消息
    public static JsonResult error(String msg){
        return error(500,msg);
    }
    //自定义错误码和信息
    public static JsonResult error(int code,String msg){
        JsonResult res = new JsonResult();
        res.put("code",code);
        res.put("msg",msg);
        return res;
    }


}
