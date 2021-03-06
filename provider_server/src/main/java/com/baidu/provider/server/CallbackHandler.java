package com.baidu.provider.server;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.baidu.provider.Call;
import com.baidu.provider.CallbackProxy;
import com.baidu.provider.common.Slog;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 回调接口对应的对象的代理类 （本地进程-> 远程进程）
 *
 * @author meijie05
 * @since 2021/3/11 11:13 AM
 */

class CallbackHandler implements InvocationHandler {

    private static final String TAG = "CallbackHandler";

    private final int mObjHash;
    private final Set<Method> mMethods = new HashSet<>();
    private final RemoteCallbackList<CallbackProxy> mCallbackList;

    public CallbackHandler(RemoteCallbackList<CallbackProxy> callbackList, int objHash, Class<?> paramsType) {
        mCallbackList = callbackList;
        mObjHash = objHash;
        mMethods.addAll(Arrays.asList(paramsType.getDeclaredMethods()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!mMethods.contains(method)) {
            // 不是接口中定义的方法，直接返回
            // 无法拿到动态代理对象的 toString，hashCode 方法，可以将 InvocationHandler 和 代理对象绑定，然后返回 InvocationHandler 的对应方法
            return method.invoke(this, args);
        }
        Slog.i(TAG, "回调代理, method " + method + ", " + Arrays.toString(args));
        Class<?>[] types = method.getParameterTypes();
        if (args.length != types.length) {
            throw new RuntimeException("参数列表错误");
        }

        Bundle bundle = new Bundle();
        bundle.putString("method", method.getName());
        bundle.putInt("objHash", mObjHash);
        for (int i = 0; i < types.length; i++) {
            putParamsData(bundle, types[i], args[i]);
        }

        bundle.setClassLoader(getClass().getClassLoader());
        Slog.i(TAG, "回调 " + bundle);
        Call call = notifyCallback(bundle);
        if (call == null) {
            return null;
        }
        Object returnResult = call.getResult();
        // FIXME: 2021/3/12 判断是否返回 Exception
        if (returnResult instanceof Exception) {
            Slog.e(TAG, "回调返回异常!!! " + returnResult);
        }

        return returnResult;
    }

    private Call notifyCallback(Bundle bundle) {
        Slog.v(TAG, "更新 " + bundle);
        if (mCallbackList == null) {
            return null;
        }
        Call call = null;
        try {
            int count = mCallbackList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                CallbackProxy item = mCallbackList.getBroadcastItem(i);
                try {
                    Slog.d(TAG, "发送更新 " + bundle);
                    // android.os.BadParcelableException: ClassNotFoundException when unmarshalling: com.baidu.separate.protocol.bean.Result
                    Call temp = item.onChange(bundle);
                    if (call == null) {
                        call = temp;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    if (call == null) {
                        call = new Call();
                        call.setResult(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e(TAG, "报错啦 " + e);
            if (call == null) {
                call = new Call();
                call.setResult(e);
            }
        } finally {
            mCallbackList.finishBroadcast();
        }
        return call;
    }

    private void putParamsData(Bundle bundle, Class<?> type, Object arg) {
        if (Parcelable.class.isAssignableFrom(type)) {
            bundle.putParcelable(type.getName(), (Parcelable) arg);
        } else if (Serializable.class.isAssignableFrom(type)) {
            bundle.putSerializable(type.getName(), (Serializable) arg);
        } else if (String.class.isAssignableFrom(type)) {
            bundle.putString(type.getName(), (String) arg);
        } else if (int.class.isAssignableFrom(type)) {
            bundle.putInt(type.getName(), (int) arg);
        } else if (int[].class.isAssignableFrom(type)) {
            bundle.putIntArray(type.getName(), (int[]) arg);
        } else if (short.class.isAssignableFrom(type)) {
            bundle.putShort(type.getName(), (short) arg);
        } else if (long.class.isAssignableFrom(type)) {
            bundle.putLong(type.getName(), (long) arg);
        } else if (float.class.isAssignableFrom(type)) {
            bundle.putFloat(type.getName(), (float) arg);
        } else if (double.class.isAssignableFrom(type)) {
            bundle.putDouble(type.getName(), (double) arg);
        } else if (byte.class.isAssignableFrom(type)) {
            bundle.putByte(type.getName(), (byte) arg);
        } else if (char.class.isAssignableFrom(type)) {
            bundle.putChar(type.getName(), (char) arg);
        } else if (boolean.class.isAssignableFrom(type)) {
            bundle.putBoolean(type.getName(), (boolean) arg);
        } else {
            Slog.e(TAG, "other type " + type);
        }
    }
}
