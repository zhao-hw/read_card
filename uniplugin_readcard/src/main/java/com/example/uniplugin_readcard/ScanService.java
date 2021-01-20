package com.example.uniplugin_readcard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class ScanService extends AccessibilityService {

    private static OnKeyEvent onKeyEvent;
    boolean mCaps = true;
    static StringBuffer mStringBufferResult = new StringBuffer();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        analysisKeyEvent(event);
        if(onKeyEvent!=null){
            //这里通过回调的方式将事件传出去统一处理
            //返回true事件就会拦截不会继续传递
            Toast.makeText(ScanService.this.getBaseContext(),
                    "无界面监听扫描枪数据:" + (char)event.getKeyCode(), Toast.LENGTH_SHORT).show();
            return onKeyEvent.onKeyEvent(event);
        }
        return super.onKeyEvent(event);
    }
    /**
     * 设置监听
     * @param onKeyEvent
     */
    public static void setOnKeyEvent(OnKeyEvent onKeyEvent){
        ScanService.onKeyEvent=onKeyEvent;
    }
    public interface OnKeyEvent{
        boolean onKeyEvent(KeyEvent event);
    }

    //获取扫描内容
    private char getInputCode(KeyEvent event) {

        int keyCode = event.getKeyCode();

        char aChar;

        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            //字母
            aChar = (char) ('a' + keyCode - KeyEvent.KEYCODE_A);
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            //数字
            aChar = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            aChar = 0;
        } else {
            //其他符号
            aChar = (char) event.getUnicodeChar();
        }

        return aChar;

    }

    public void analysisKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();

        //字母大小写判断
        checkLetterStatus(event);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            char aChar = getInputCode(event);
//            char aChar = (char) event.getUnicodeChar();

            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
//                System.out.println(mStringBufferResult.toString());
//                Toast.makeText(ScanService.this.getBaseContext(), mStringBufferResult.toString(), Toast.LENGTH_SHORT).show();
            } else {
                //延迟post，若500ms内，有其他事件

            }

        }
    }

    //检查shift键
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //按着shift键，表示大写
                mCaps = true;
            } else {
                //松开shift键，表示小写
                mCaps = false;
            }
        }
    }
}