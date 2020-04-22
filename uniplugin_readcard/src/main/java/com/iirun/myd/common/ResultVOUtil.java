package com.iirun.myd.common;

import com.alibaba.fastjson.JSONObject;

public class ResultVOUtil {
    public static JSONObject success(Object object){
        JSONObject resultVO = new JSONObject();
        resultVO.put("code",1);
        resultVO.put("msg","成功");
        resultVO.put("data",object);
        return resultVO;
    }
    public static JSONObject success(){
        return success(null);
    }
    public static JSONObject error(Integer code,String msg){
        JSONObject resultVO = new JSONObject();
        resultVO.put("msg",msg);
        resultVO.put("code",code);
        return resultVO;
    }
}