package com.baidu.provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.baidu.common.util.Slog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

/**
 * 交换本地callback 和远程 callback（本地 callback 不能跨进程，远程的 callback 可以）
 *
 * @author meijie05
 * @since 2021/2/17 4:26 PM
 */

public class CallbackExchanger {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Object> mMap = new HashMap<>(); // 对象的 hashcode，value 该对象

    public void add(Object obj) {
        int key = obj.hashCode();
        if (mMap.get(key) == null) {
            mMap.put(key, obj);
            Slog.i("add " + key + ", " + obj);
        }
    }

    public void remove(Object obj) {
        int key = obj.hashCode();
        mMap.remove(key);
        Slog.i("remove " + key);
    }

    public void onChanged(Bundle bundle) {
        // 解决 android.os.BadParcelableException: ClassNotFoundException when unmarshalling: com.baidu.separate.protocol.bean.Result
        bundle.setClassLoader(getClass().getClassLoader());
        Slog.i("onChange " + bundle);
        String method = bundle.getString("method");
        int objHash = bundle.getInt("objHash");
        Parcelable arg = bundle.getParcelable("arg");
        Slog.i("objHash " + objHash);
        Slog.i("method " + method);
        Slog.i("arg " + arg);

        Object obj = mMap.get(objHash);
        if (obj == null) {
            Slog.i("not found method instance. " + method);
            return;
        }
        for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
            if (method.equals(declaredMethod.getName())) {
                // 切换到ui线程
                mHandler.post(() -> {
                    try {
                        declaredMethod.invoke(obj, arg);
                    } catch (Exception e) {
                        Slog.e("回调转化发生异常 " + e);
                    }
                });
                break;
            }
        }
    }
}
