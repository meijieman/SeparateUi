package com.baidu.provider.server;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.baidu.common.DataCenter;
import com.baidu.common.util.SLog;
import com.baidu.provider.Call;
import com.baidu.provider.ICall;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Process;
import android.os.RemoteException;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:18 PM
 */

public class ICallImpl extends ICall.Stub {

    public ICallImpl() {
        SLog.d("pid " + Process.myPid());
    }

    @Override
    public Call getCallResult(Call call) throws RemoteException {
        SLog.w("收到请求 " + call + ", pid " + Process.myPid());

        String className = call.getClassName();
        String methodName = call.getMethodName();
        Object[] params = call.getParams();
        Class<?>[] paramsTypes = call.getParamTypes();

        SLog.d("反射 className " + className + ", " + methodName + ", " + Arrays.toString(params));
        try {
            // FIXME: 2021/1/21 定义的接口如 BookService，PersonService 及其参数对象的  className 需要创建的包名路径一致
            // 以后可以通过编译时注解来解决，只需要接口中定义的方法对应上就可以
            List<Object> mImpls = DataCenter.getInstance().getImpls();
            if (mImpls.isEmpty()) {
                SLog.e("默认初始化失败");
                mImpls.add(Class.forName("com.baidu.protocol.BookServiceImpl").newInstance());
                mImpls.add(Class.forName("com.baidu.protocol.RemoteViewServiceImpl").newInstance());
            }
            SLog.d("mImpls size " + mImpls.size());

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
            SLog.v("method " + method);

            if (params != null && params[0] instanceof IBinder) {
                String canonicalName = paramsTypes[0].getCanonicalName();
                SLog.i("注册调用的方法 " + canonicalName);
                IBinder binder = (IBinder) params[0];
                IInterface iInterface = binder.queryLocalInterface(canonicalName);
                SLog.i("注册调用的方法 iInterface " + iInterface);

            }
            // 报错啦 exception java.lang.IllegalArgumentException: method com.baidu.separate.impl.BookServiceImpl.register argument 1 has type com.baidu.separate.protocol.callback.OnBookListener, got android.os.BinderProxy
            Object result = method.invoke(invoker, params);
            call.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            SLog.e("报错啦 exception " + e);
            call.setResult(e);
        }

        SLog.v("计算结果 result " + call.getResult());
        return call;
    }
}
