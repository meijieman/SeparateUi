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

    /**
     * 获取单例
     */
    public static Provider getInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static final Provider sInstance = new Provider();
    }

    /**
     * 初始化框架
     *
     * @param ipc 是否需要跨进程
     */
    public void init(Context ctx, final boolean ipc) {
        init(ctx, null, ipc);
    }

    /**
     * 初始化框架
     *
     * @param targetPackageName 目标服务所在的包名，跨进程的时候配置使用，如果配置为 null，跨进程的时候回去主动查找
     * @param ipc               是否需要跨进程
     */
    public void init(Context ctx, final String targetPackageName, final boolean ipc) {
        mIpc = ipc;
        if (ipc) {
            // FIXME: 2021/1/21 子线程运行，或需要添加链接成功回调
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 链接远程服务
                    connector.connect(ctx, targetPackageName);
                }
            }).start();
        }
    }

    /**
     * 获取具备某种功能的实现类（同进程返回的为初始化时传入的实现类，跨进程返回的为动态代理对象）
     *
     * @param clazz 有对应功能的实现类的接口类
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new RuntimeException("param must be a interface. " + clazz);
        }
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