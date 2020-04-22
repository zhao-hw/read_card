package com.example.uniplugin_readcard;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;

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
    public void test() {
        Context context = mWXSDKInstance.getContext();
        Toast.makeText(context, "===>start!", Toast.LENGTH_SHORT).show();
    }

    @JSMethod(uiThread = true)
    public void on_button_port_clicked() {
        Context context = mWXSDKInstance.getContext();
        Toast.makeText(context, "===>start!", Toast.LENGTH_SHORT).show();
        wmr.ws_set_context(context);
        rt = wmr.ws_openPort(port);
        if (rt >= 0) {
            openUSBHID = true;
            Toast.makeText(context, "打开成功", Toast.LENGTH_SHORT).show();
            beforeWork();
        } else {
            openUSBHID = false;
            Toast.makeText(context, String.format("USB-HID端口 %d 打开失败.(返回值：%d),请重试", port, rt), Toast.LENGTH_SHORT).show();
        }
//        wmr.ws_set_context(context);
//        wmr.ws_openPort(port);
//        byte[] hkey = str.getBytes();
//        byte[] bkey = new byte[100];
//        wmr.ws_strHexToChar(hkey, str.length(), bkey);
//        rt = wmr.ws_loadKey(0, bkey, 1);
//        get_cardno_iso14443a();
//        on_button_readblock_clicked();
    }

    public boolean beforeWork() {
        Context context = mWXSDKInstance.getContext();
        Toast.makeText(context, "正在打开", Toast.LENGTH_SHORT).show();
        byte[] hkey = str.getBytes();
        byte[] bkey = new byte[100];



        while (!loadPassword || !readCardType) {
            wmr.ws_strHexToChar(hkey, str.length(), bkey);
            rt = wmr.ws_loadKey(0, bkey, 1);
            if (rt > 0) {
                loadPassword = true;
                Toast.makeText(context, "装载密码成功", Toast.LENGTH_SHORT).show();
            } else {
                loadPassword = false;
                Toast.makeText(context, "装载密码失败", Toast.LENGTH_SHORT).show();
            }

            String cardno_iso14443a = get_cardno_iso14443a();
            if (cardno_iso14443a != null) {
                readCardType = true;
                Toast.makeText(context, cardno_iso14443a, Toast.LENGTH_SHORT).show();
            } else {
                readCardType = false;
            }
        }
        rt = wmr.ws_beep(port);
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
    public void on_button_readblock_clicked(){

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

        }
        else{
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
