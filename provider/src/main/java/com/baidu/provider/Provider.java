package com.baidu.provider;

import android.content.Context;

import com.baidu.provider.common.DataCenter;

import java.lang.reflect.Proxy;

/**
 * 客户端数据提供者
 * <p>
 * 设计思路：
 * 可以提供的功能和实现类通过接口隔离分割开，在初始化的时候注入实现类和接口的一一对应关系，然后调用功能的时候，不需要关心实现类是在什么进程，
 * 通过 sdk 提供的统一方法完成调用方法。
 *
 * <p>
 * 1. 如果是跨进程调用，则利用 Service 和 ICall 接口，动态接口实现跨进程调用方法；
 * 局限性：受限于进程间通讯，对于自定义的对象，需要实现 Parcelable 接口; 同样，注册/反注册回调方法需要使用进程间的回调（可以使用 sdk 中定义的统一回调方法实现）
 * <p>
 * 2. 如果是同进程调用，则调用 {@link Provider#get(Class)} 时通过 DataCenter 注入的方法返回其对应的实现类，供其使用
 */
public class Provider {

    private final Connector connector;
    private final CallbackExchanger mExchanger;
    private boolean mIpc;

    private Provider() {
        mExchanger = new CallbackExchanger();
        connector = new Connector(mExchanger);
    }

    public static Provider getInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static final Provider sInstance = new Provider();
    }

    /**
     * @param ipc 是否需要跨进程
     */
    public void init(Context ctx, final boolean ipc) {
        mIpc = ipc;
        if (ipc) {
            // FIXME: 2021/1/21 子线程运行，或需要添加链接成功回调
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // ui 分离
                    connector.connect(ctx, ipc);
                }
            }).start();
        }
    }

    /**
     * @param clazz 提供数据的接口
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz) {
        if (!mIpc) {
            T impl = DataCenter.getInstance().get(clazz);
            if (impl != null) {
                return impl;
            }

            throw new RuntimeException("未使用 DataCenter.getInstance#add 添加对应实现类");
        } else {
            ServiceInvocationHandler handler = new ServiceInvocationHandler(clazz, connector, mExchanger);
            Class<?> classType = handler.getClassType();
            Object proxy = Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType}, handler);
            return (T) proxy;
        }
    }

}