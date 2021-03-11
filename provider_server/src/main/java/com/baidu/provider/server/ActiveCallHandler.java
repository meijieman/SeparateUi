package com.baidu.provider.server;

import com.baidu.common.util.Slog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 主动调用（远程进程 -> 本地进程调用的代理类）
 *
 * @author meijie05
 * @since 2021/3/11 11:08 AM
 */

class ActiveCallHandler implements InvocationHandler {

    private final Object mProxy;

    public ActiveCallHandler(Object proxy) {
        mProxy = proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 为什么会调用到  public java.lang.String java.lang.Object.toString()
        // 因为 com.baidu.separate.impl.BookServiceImpl.register 方法打印了 listener

        Slog.i("回调被调用, method " + method + ", " + Arrays.toString(args));
        try {
            return method.invoke(mProxy, args);
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e("回调异常 " + e);
        }
        return null;
    }
}
