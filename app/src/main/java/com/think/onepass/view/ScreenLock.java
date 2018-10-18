package com.think.onepass.view;

import android.os.Handler;
import android.os.Message;

/**
 * Created by user on 18-10-12.
 */

public class ScreenLock {
    private static final int MSG_TIMEOUT = 0;
    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int DEFAULT_TIMEOUT = MINUTE * 3;//默认为3分钟
    private OnTimeOutListener mOnTimeOutListener;
    private int mScreensaverTiemout = DEFAULT_TIMEOUT;//默认为3分钟

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_TIMEOUT){
                //调用回调通知接收者
                if(mOnTimeOutListener != null){
                    mOnTimeOutListener.onTimeOut(ScreenLock.this);
                }
            }
        }
       };
    public ScreenLock(int screensaverTiemout) {
        mScreensaverTiemout = screensaverTiemout;
    }
    public ScreenLock() {

    }
    /** * 开始计时 */
    public void start() {
        Message message = mHandler.obtainMessage(MSG_TIMEOUT);
        //包装消息
        mHandler.sendMessageDelayed(message, mScreensaverTiemout);
        //延时发送消息
        }
    /** * 停止计时 */
    public void stop() {
        mHandler.removeMessages(MSG_TIMEOUT);
        //移除消息
    }
    /** * 重置时间 */
    public void resetTime() {
        stop();
        start();
    }

    public void setOnTimeOutListener(OnTimeOutListener onTimeOutListener) {
        this.mOnTimeOutListener = onTimeOutListener;
    }
    /** * 屏幕保护时间到监听 */
    public interface OnTimeOutListener {
        void onTimeOut(ScreenLock screensaver);
    }

}
