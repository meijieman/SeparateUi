package com.baidu.provider.server;

import android.os.Bundle;
import android.os.Parcelable;

import com.baidu.che.codriver.xlog.XLog;
import com.baidu.provider.common.util.Slog;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 回调接口对应的对象的代理类 （本地进程-> 远程进程）
 *
 * @author meijie05
 * @since 2021/3/11 11:13 AM
 */

class CallbackHandler implements InvocationHandler {

    private static final String TAG = "CallbackHandler";
    private final ICallImpl mICall;
    private final int mObjHash;

    public CallbackHandler(ICallImpl iCall, int objHash) {
        mICall = iCall;
        mObjHash = objHash;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.getName().startsWith("on")) {  // TODO: 2021/2/21 可以通过是否是接口中定义的方法来判断
            // 无法拿到动态代理对象的 toString，hashCode 方法，可以将 InvocationHandler 和 代理对象绑定，然后返回 InvocationHandler 的对应方法
            return method.invoke(this, args);
        }
        XLog.i(TAG, "回调代理, method " + method + ", " + Arrays.toString(args));
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
        XLog.i(TAG, "回调 " + bundle);
        mICall.notifyCallback(bundle);

        return null;
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
            Slog.e("other type " + type);
        }
    }
}
