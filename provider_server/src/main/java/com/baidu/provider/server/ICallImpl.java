package com.baidu.provider.server;

import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.baidu.provider.Call;
import com.baidu.provider.CallbackProxy;
import com.baidu.provider.ICall;
import com.baidu.provider.common.DataCenter;
import com.baidu.provider.common.Slog;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:18 PM
 */

class ICallImpl extends ICall.Stub {

    private static final String TAG = "ICallImpl";

    public ICallImpl() {
        Slog.d(TAG, "pid " + Process.myPid());
    }

    private final Map<Integer, Object> mMapping = new HashMap<>(); // key 远程对象嗯 hashCode，value 本地对象

    @Override
    public Call getCallResult(Call call) throws RemoteException {
        Slog.w(TAG, "收到请求 " + call + ", pid " + Process.myPid());

        String className = call.getClassName();
        String methodName = call.getMethodName();
        Object[] params = call.getParams();
        Class<?>[] paramsTypes = call.getParamTypes();

        try {
            // FIXME: 2021/1/21 定义的接口如 BookService，PersonService 及其参数对象的  className 需要创建的包名路径一致
            // 以后可以通过编译时注解来解决，只需要接口中定义的方法对应上就可以

            Class<?> classType = Class.forName(className);
            // 获取 classType 的实现类
            Object invoker = DataCenter.getInstance().get(classType);
            if (invoker == null) {
                throw new RuntimeException("未使用 DataCenter.getInstance#add 添加对应实现类（Across Process）");
            }
            // 获取所要调用的方法
            Method method = classType.getMethod(methodName, paramsTypes);
            Slog.v(TAG, "method " + method);

//            if (params != null && params[0] instanceof IBinder) {
//                String canonicalName = paramsTypes[0].getCanonicalName();
//                XLog.i(TAG, "注册调用的方法 " + canonicalName);
//                IBinder binder = (IBinder) params[0];
//                IInterface iInterface = binder.queryLocalInterface(canonicalName);
//                XLog.i(TAG, "注册调用的方法 iInterface " + iInterface);
//
//            }
            if (method.getName().startsWith("register")) {
                // register 的参数必须为对应 Object 的 hashcode
                int objHash = (int) params[0];
                if (mMapping.get(objHash) != null) {
                    Slog.d(TAG, "不能重复注册相同的对象");
                    call.setResult(new RuntimeException("不能重复注册相同的对象"));
                    return call;
                }

                // 创建回调的实现类
                Class<?> paramsType = paramsTypes[0];
                CallbackHandler callback = new CallbackHandler(mCallbackList, objHash, paramsType);
                ClassLoader classLoader = invoker.getClass().getClassLoader();
                Object callbackProxy = Proxy.newProxyInstance(classLoader, new Class[]{paramsType}, callback);

                ActiveCallHandler activeCall = new ActiveCallHandler(callbackProxy);
                Object serviceProxy;
                if (paramsType.isInterface()) {
                    // 如果是接口，使用动态代理创建其实现类
                    serviceProxy = Proxy.newProxyInstance(classLoader, new Class[]{paramsType}, activeCall);
                } else {
                    // FIXME: 2021/2/22 暂不支持
                    serviceProxy = paramsType.newInstance();
                }
                mMapping.put(objHash, serviceProxy);
                params[0] = serviceProxy;
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
                    Slog.d(TAG, "未找到对应的 objHash， unregister 失败 " + params[0]);
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
            Slog.e(TAG, "报错啦 exception " + e);
            call.setResult(e);
        }

        Slog.v(TAG, "发送结果 " + call);
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

}
