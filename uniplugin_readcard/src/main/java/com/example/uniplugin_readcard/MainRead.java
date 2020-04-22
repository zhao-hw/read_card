package com.example.uniplugin_readcard;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.iirun.myd.common.ResultVOUtil;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.util.Arrays;

import cn.wmr.CWMR;

public class MainRead extends WXSDKEngine.DestroyableModule {
    public CWMR wmr = new CWMR();
    int rt = 0;
    int port = 0;
    String str = "FFFFFFFFFFFF";
    int block = 5;
    byte[] bdata=new byte[1024];
    byte[] hdata=new byte[1024];
    boolean openUSBHID = false;
    boolean readCardType = false;
    boolean loadPassword = false;


    @JSMethod(uiThread = true)
    public void test(JSCallback jsCallback) {
        if (openUSBHID && readCardType && loadPassword) {
            String s = on_button_readblock_clicked();
            if (s != null && s != "") {
                wmr.ws_beep(port);
                jsCallback.invoke(ResultVOUtil.success(s));
            } else {
                beforeWork();
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (!openUSBHID) {
                sb.append("没有打开USB连接，断开无关usb设备后重试\n");
            }
            if (!readCardType) {
                sb.append("获取卡类型失败\n");
            }
            if (!loadPassword) {
                sb.append("加载密码失败\n");
            }
            jsCallback.invoke(ResultVOUtil.error(-1, sb.toString()));
        }
    }

    @JSMethod(uiThread = true)
    public void on_button_port_clicked(JSCallback jsCallback) {
        JSONObject result = new JSONObject();
        Context context = mWXSDKInstance.getContext();
        wmr.ws_set_context(context);
        if (!openUSBHID) {
            rt = wmr.ws_openPort(port);
            if (rt >= 0) {
                openUSBHID = true;
                boolean b = beforeWork();
                if (b) {
                    String s = on_button_readblock_clicked();
                    result = ResultVOUtil.success();
                }
            } else {
                openUSBHID = false;
                result = ResultVOUtil.error(-1, String.format("USB-HID端口 %d 打开失败.(返回值：%d),请重试", port, rt));
            }
        }
//        else {
//            Toast.makeText(context, "打开成功", Toast.LENGTH_SHORT).show();
//            boolean b = beforeWork();
//            if (b) {
//                String s = on_button_readblock_clicked();
//                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
//                result = ResultVOUtil.error(1, s);
//            } else {
//                result = ResultVOUtil.error(-1, "加载密码或者卡号失败");
//            }
//        }
        jsCallback.invoke(result);
    }

    public boolean beforeWork() {
        Context context = mWXSDKInstance.getContext();
        byte[] hkey = str.getBytes();
        byte[] bkey = new byte[100];
        wmr.ws_strHexToChar(hkey, str.length(), bkey);
        rt = wmr.ws_loadKey(0, bkey, 1);
        if (rt > 0) {
            loadPassword = true;
        } else {
            loadPassword = false;
        }

        String cardno_iso14443a = get_cardno_iso14443a();
        if (cardno_iso14443a != null) {
            readCardType = true;
        } else {
            readCardType = false;
        }
        return true;
    }

    public String get_cardno_iso14443a() {
        // String
        String[] cardno_string={""};
        rt=wmr.ws_getCardNo_String(port, cardno_string);
        if (rt>=0){
            System.out.println(cardno_string[0]);
            return cardno_string[0];
        }

        // double
        double[] cardno_double={0};
        rt=wmr.ws_getCardNo_Double(port, cardno_double);

        // DWORD
        long[] cardno_long={0};
        rt=wmr.ws_getCardNo_DWORD(port, cardno_long);

        // Hex
        String[] cardno_hex={""};
        rt=wmr.ws_getCardNo_Hex(port, cardno_hex);

        // char
        int[] len=new int[1];
        byte[] cardno_char=new byte[100];

        rt=wmr.ws_getCardNo_Char(port, cardno_char);
        return "";

    }

    //读块数据
    public String on_button_readblock_clicked(){

        int i;
        Arrays.fill(bdata, (byte)0);
        Arrays.fill(hdata, (byte)0);

        StringBuilder stringBuilder = new StringBuilder("");

        // 读块数据
        rt=wmr.ws_readBlock(port, block,bdata);
        if (rt>=0){
            // 转成16进制字符串方便显示
//            wmr.ws_charToStrHex(bdata, 16, hdata);
            stringBuilder.append(toUpperCase(bdata,32), 0, 32);
            stringBuilder.append("\n");
            return stringBuilder.toString();
        } else {
            return "";
        }

    }

    // 字符转成大写
    public char[] toUpperCase(byte[] data,int len){
        char[] hdata=new char[len];
        String s=new String();

        for (int i=0;i<len;i++)
            hdata[i]=(char)data[i];

        s=s.valueOf(hdata);
        s=s.toUpperCase();

        return s.toCharArray();
    }

    @Override
    public void destroy() {

    }
}
