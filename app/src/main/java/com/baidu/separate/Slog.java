package com.baidu.separate;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.baidu.che.codriver.xlog.ILog;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/7 12:29 PM
 */

public class Slog implements ILog {
    private static final String TAG = "==##";

    private static String getTrace() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        StringBuilder sb = new StringBuilder();
        if (ste.length >= 2) {
            StackTraceElement element = ste[2];
            sb.append(Process.myPid())
                    .append("-")
                    .append(Thread.currentThread().getName())
                    .append(" ")
                    .append(element.getMethodName())
                    .append("(")
                    .append(element.getFileName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append(") ");
        }
        return sb.toString();
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void setLogLevel(int i) {

    }

    @Override
    public void setLogFileLevel(int i) {

    }

    @Override
    public void v(String s, Object msg) {
        Log.v(TAG, getTrace() + msg);
    }

    @Override
    public void d(String s, Object msg) {
        Log.d(TAG, getTrace() + msg);
    }

    @Override
    public void i(String s, Object msg) {
        Log.i(TAG, getTrace() + msg);
    }

    @Override
    public void w(String s, Object msg) {
        Log.w(TAG, getTrace() + msg);
    }

    @Override
    public void e(String s, Object msg) {
        Log.e(TAG, getTrace() + msg);
    }

    @Override
    public void e(String s, Object msg, Throwable throwable) {
        Log.e(TAG, getTrace() + msg + throwable);
    }
}
