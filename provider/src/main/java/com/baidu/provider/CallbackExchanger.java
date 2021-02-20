package com.baidu.provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.common.util.Slog;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * 交换本地callback 和远程 callback（本地 callback 不能跨进程，远程的 callback 可以）
 *
 * @author meijie05
 * @since 2021/2/17 4:26 PM
 */

public class CallbackExchanger {

    private final Map<String, List<Object>> mMap = new HashMap<>(); // 方法，和调用这个方法的实例

    public void add(String key, Object obj) {
        List<Object> objs = mMap.get(key);
        if (objs == null) {
            objs = new ArrayList<>();
        }
        objs.add(obj);
        mMap.put(key, objs);
    }

    public void remove(String key, Object obj) {
        List<Object> objs = mMap.get(key);
        if (objs != null) {
            objs.remove(obj);
            mMap.put(key, objs);
        }
    }

    public void onChange(Bundle bundle) {
        String method = bundle.getString("method");
        int objHash = bundle.getInt("objHash");
        Parcelable arg = bundle.getParcelable("arg");
        Slog.i("objHash " + objHash);
        Slog.i("method " + method);
//        Slog.i("arg " + arg);

//        List<Object> objs = mMap.get(method);
//        for (Object obj : objs) {
//            if (objHash == obj.hashCode()) {
//                for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
//                    if (method.equals(declaredMethod.getName())) {
//                        try {
//                            declaredMethod.invoke(obj, arg);
//                        } catch (Exception e) {
//                            Slog.e("回调转化发生异常 " + e);
//                        }
//                        break;
//                    }
//                }
//                break;
//            }
//        }
    }
}
