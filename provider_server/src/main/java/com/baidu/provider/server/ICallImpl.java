package com.baidu.provider.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.common.DataCenter;
import com.baidu.common.util.Slog;
import com.baidu.provider.Call;
import com.baidu.provider.CallbackProxy;
import com.baidu.provider.ICall;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:18 PM
 */

public class ICallImpl extends ICall.Stub {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public ICallImpl() {
        Slog.d("pid " + Process.myPid());
    }

    private final Map<Integer, Object> mMapping = new HashMap<>(); // key 远程对象嗯 hashCode，value 本地对象

    @Override
    public Call getCallResult(Call call) throws RemoteException {
        Slog.w("收到请求 " + call + ", pid " + Process.myPid());

        String className = call.getClassName();
        String methodName = call.getMethodName();
        Object[] params = call.getParams();
        Class<?>[] paramsTypes = call.getParamTypes();

        Slog.d("反射 className " + className + ", " + methodName + ", " + Arrays.toString(params));
        try {
            // FIXME: 2021/1/21 定义的接口如 BookService，PersonService 及其参数对象的  className 需要创建的包名路径一致
            // 以后可以通过编译时注解来解决，只需要接口中定义的方法对应上就可以
            List<Object> mImpls = DataCenter.getInstance().getImpls();
            if (mImpls.isEmpty()) {
                Slog.e("默认初始化失败");
                mImpls.add(Class.forName("com.baidu.protocol.BookServiceImpl").newInstance());
                mImpls.add(Class.forName("com.baidu.protocol.RemoteViewServiceImpl").newInstance());
            }
            Slog.d("mImpls size " + mImpls.size());

            Class<?> classType = Class.forName(className);
            // 获取 classType 的实现类
            Object invoker = null;
            for (Object impl : mImpls) {
                if (impl != null && classType.isInstance(impl)) {
                    invoker = impl;
                    break;
                }
            }

            if (invoker == null) {
                throw new RuntimeException("没有定义接口类或实现类");
            }
            // 获取所要调用的方法
            Method method = classType.getMethod(methodName, paramsTypes);
            Slog.v("method " + method);

//            if (params != null && params[0] instanceof IBinder) {
//                String canonicalName = paramsTypes[0].getCanonicalName();
//                Slog.i("注册调用的方法 " + canonicalName);
//                IBinder binder = (IBinder) params[0];
//                IInterface iInterface = binder.queryLocalInterface(canonicalName);
//                Slog.i("注册调用的方法 iInterface " + iInterface);
//
//            }
            if (method.getName().startsWith("register")) {
                // register 的参数必须为对应 Object 的 hashcode
                int objHash = (int) params[0];
                if (mMapping.get(objHash) != null) {
                    Slog.d("不能重复注册相同的对象");
                    call.setResult(new RuntimeException("不能重复注册相同的对象"));
                    return call;
                }

                // 创建实现类注册到
                Class<?> paramsType = paramsTypes[0];
                Object o;
                MyHandler2 h2 = new MyHandler2(objHash);
                Object realProxy = Proxy.newProxyInstance(invoker.getClass().getClassLoader(), new Class[]{paramsType},
                        h2);
                MyHandler h = new MyHandler(realProxy);
                if (paramsType.isInterface()) {
                    // 如果是接口，使用动态代理创建其实现类
                    o = Proxy.newProxyInstance(invoker.getClass().getClassLoader(), new Class[]{paramsType}, h);
                } else {
                    o = paramsType.newInstance();
                }
                mMapping.put(objHash, o);
                params[0] = o;
            } else if (method.getName().startsWith("unregister")) {
                Integer objHash = null;
                for (int key : mMapping.keySet()) {
                    if (key == (int) params[0]) {
                        objHash = key;
                        break;
                    }
                }
                if (objHash != null) {
                    params[0] = mMapping.get(objHash);
                    mMapping.remove(objHash);
                } else {
                    call.setResult(new RuntimeException("未找到对应的 objHash， unregister 失败 " + params[0]));
                    Slog.d("未找到对应的 objHash， unregister 失败 " + params[0]);
                    return call;
                }
            }

            // 报错啦 exception java.lang.IllegalArgumentException: method com.baidu.separate.impl.BookServiceImpl.register argument 1 has type com.baidu.separate.protocol.callback.OnBookListener, got android.os.BinderProxy
            // 报错啦 exception java.lang.NullPointerException: Expected to unbox a 'int' primitive type but was returned null
            Object result = method.invoke(invoker, params);
            if (method.getName().startsWith("register") || method.getName().startsWith("unregister")) {
                // 将 register 的 params 置为0，因为未实现 parcelable 的 object 是不能够跨进程传输的
                params[0] = 0;

            }
            call.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e("报错啦 exception " + e);
            call.setResult(e);
        }

        Slog.v("发送结果 " + call);
        Slog.v("计算结果 result " + call.getResult());
        return call;
    }

    private final RemoteCallbackList<CallbackProxy> mCallbackList = new RemoteCallbackList<>();

    @Override
    public void register(CallbackProxy proxy) throws RemoteException {
        mCallbackList.register(proxy);
    }

    @Override
    public void unregister(CallbackProxy proxy) throws RemoteException {
        mCallbackList.unregister(proxy);
    }

    public void notifyCallback(Bundle bundle) {
        Slog.v("更新 " + bundle);
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
                // java.lang.IllegalStateException: beginBroadcast() called while already in a broadcast
                try {
                    int count = mCallbackList.beginBroadcast();
                    if (count == 0) {
                        return;
                    }
                    for (int i = 0; i < count; i++) {
                        CallbackProxy item = mCallbackList.getBroadcastItem(i);
                        try {
                            Slog.d("发送更新 " + bundle);
                            // android.os.BadParcelableException: ClassNotFoundException when unmarshalling: com.baidu.separate.protocol.bean.Result
                            item.onChange(bundle);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Slog.e("报错啦 " + e);
                } finally {
                    mCallbackList.finishBroadcast();
                }
//            }
//        });

    }

    // 回调方法的代理对象
   /* private class MyHandler implements InvocationHandler {
        private Integer objHash;

        public MyHandler(Integer objHash) {
            this.objHash = objHash;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Slog.i("回调被调用 " + proxy + ", method " + method + ", " + Arrays.toString(args));

            if (!method.getName().startsWith("on")) {
//                Object o = mMapping.get(objHash);
//                Object invoke = method.invoke(o, args);
//                Slog.i("回调返回结果 " + invoke);
//                return invoke;
                return null;
            }

            Parcelable arg = null;
            if (args != null && args.length > 0) {
                arg = (Parcelable) args[0];
            }
            Bundle bundle = gen(objHash, arg);
            Slog.i("回调 " + bundle);
            notifyCallback(bundle);
            return null;
        }
    }*/

    private static class MyHandler implements InvocationHandler {
        private final Object proxy;

        public MyHandler(Object proxy) {
            this.proxy = proxy;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 为什么会调用到  public java.lang.String java.lang.Object.toString()
            // 因为 com.baidu.separate.impl.BookServiceImpl.register 方法打印了 listener

            Slog.i("回调被调用 , method " + method + ", " + Arrays.toString(args));
            return method.invoke(this.proxy, args);
        }
    }

    class MyHandler2 implements InvocationHandler {
        private final Integer objHash;

        public MyHandler2(Integer objHash) {
            this.objHash = objHash;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!method.getName().startsWith("on")) {  // TODO: 2021/2/21 可以通过是否是接口中定义的方法来判断
                // 无法拿到动态代理对象的 toString，hashCode 方法，可以将 InvocationHandler 和 代理对象绑定，然后返回 InvocationHandler 的对应方法
                return method.invoke(this, args);
            }
            Parcelable arg = null;
            if (args != null && args.length > 0) {
                arg = (Parcelable) args[0];
            }
            Bundle bundle = gen(objHash, method.getName(), arg);
            bundle.setClassLoader(getClass().getClassLoader());
            Slog.i("回调 " + bundle);
            notifyCallback(bundle);

            return null;
        }
    }

    private Bundle gen(int objHash, String method, Parcelable arg) {
        Bundle bundle = new Bundle();
        bundle.putString("method", method);
        bundle.putInt("objHash", objHash);
        if (arg != null) {
            bundle.putParcelable("arg", arg);
        }

        return bundle;
    }

}
