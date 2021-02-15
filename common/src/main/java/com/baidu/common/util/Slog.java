package com.baidu.common.util;

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

    public static void v(String msg) {
        Log.v(TAG, getTrace() + msg);
    }

    public static void d(String msg) {
        Log.d(TAG, getTrace() + msg);
    }

    public static void i(String msg) {
        Log.i(TAG, getTrace() + msg);
    }

    public static void w(String msg) {
        Log.w(TAG, getTrace() + msg);
    }

    public static void e(String msg) {
        Log.e(TAG, getTrace() + msg);
    }

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
}
