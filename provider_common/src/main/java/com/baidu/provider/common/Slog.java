package com.baidu.provider.common;

import android.os.Process;
import android.util.Log;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/7 12:29 PM
 */

public class Slog {
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

    public static void v(String s, Object msg) {
        Log.v(TAG, getTrace() + msg);
    }

    public static void d(String s, Object msg) {
        Log.d(TAG, getTrace() + msg);
    }

    public static void i(String s, Object msg) {
        Log.i(TAG, getTrace() + msg);
    }

    public static void w(String s, Object msg) {
        Log.w(TAG, getTrace() + msg);
    }

    public static void e(String s, Object msg) {
        Log.e(TAG, getTrace() + msg);
    }

    public static void e(String s, Object msg, Throwable throwable) {
        Log.e(TAG, getTrace() + msg + throwable);
    }
}
