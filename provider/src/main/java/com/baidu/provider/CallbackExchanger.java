package com.baidu.provider;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import com.baidu.provider.common.Slog;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 交换本地callback 和远程 callback（本地 callback 不能跨进程，远程的 callback 可以）
 *
 * @author meijie05
 * @since 2021/2/17 4:26 PM
 */

class CallbackExchanger {

    private static final String TAG = "CallbackExchanger";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Object> mMap = new HashMap<>(); // 对象的 hashcode，value 该对象

    public void add(Object obj) {
        int key = obj.hashCode();
        if (mMap.get(key) == null) {
            mMap.put(key, obj);
            Slog.i(TAG, "add " + key + ", " + obj);
        }
    }

    public void remove(Object obj) {
        int key = obj.hashCode();
        mMap.remove(key);
        Slog.i(TAG, "remove " + key);
    }

    public Call onChanged(Bundle bundle) {
        // 解决 android.os.BadParcelableException: ClassNotFoundException when unmarshalling: com.baidu.separate.protocol.bean.Result
        bundle.setClassLoader(getClass().getClassLoader());
        String method = bundle.getString("method");
        int objHash = bundle.getInt("objHash");
//        Parcelable arg = bundle.getParcelable("arg");
        Slog.i(TAG, "objHash " + objHash + ", method " + method + ", bundle " + bundle);

        Call call = new Call();
        Object obj = mMap.get(objHash);
        if (obj == null) {
            Slog.d(TAG, "not found method instance. " + method);
            call.setResult(new RuntimeException("not found method instance. " + method));
            return call;
        }
        for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
            if (method.equals(declaredMethod.getName())) {
                Object[] args = getParamsData(bundle, declaredMethod.getParameterTypes());

                if (declaredMethod.getReturnType().isAssignableFrom(void.class)) {
                    // 切换到ui线程
                    mHandler.post(() -> {
                        try {
                            declaredMethod.invoke(obj, args);
                        } catch (Exception e) {
                            Slog.e(TAG, "回调调用发生异常 " + e);
                        }
                    });
                    call.setResult(null);
                } else {
                    // 同步方法不切换线程，默认在 Binder 线程
                    try {
                        Object invoke = declaredMethod.invoke(obj, args);
                        call.setResult(invoke);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        return call;
    }

    private Object[] getParamsData(Bundle bundle, Class<?>[] types) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if (Parcelable.class.isAssignableFrom(type)) {
                args[i] = bundle.getParcelable(type.getName());
            } else if (Serializable.class.isAssignableFrom(type)) {
                args[i] = bundle.getSerializable(type.getName());
            } else if (String.class.isAssignableFrom(type)) {
                args[i] = bundle.getString(type.getName());
            } else if (int.class.isAssignableFrom(type)) {
                args[i] = bundle.getInt(type.getName());
            } else if (int[].class.isAssignableFrom(type)) {
                args[i] = bundle.getIntArray(type.getName());
            } else if (short.class.isAssignableFrom(type)) {
                args[i] = bundle.getShort(type.getName());
            } else if (long.class.isAssignableFrom(type)) {
                args[i] = bundle.getLong(type.getName());
            } else if (float.class.isAssignableFrom(type)) {
                args[i] = bundle.getFloat(type.getName());
            } else if (double.class.isAssignableFrom(type)) {
                args[i] = bundle.getDouble(type.getName());
            } else if (byte.class.isAssignableFrom(type)) {
                args[i] = bundle.getByte(type.getName());
            } else if (char.class.isAssignableFrom(type)) {
                args[i] = bundle.getChar(type.getName());
            } else if (boolean.class.isAssignableFrom(type)) {
                args[i] = bundle.getBoolean(type.getName());
            } else {
                Slog.e(TAG, "other type " + type);
            }
        }

        return args;
    }
}
