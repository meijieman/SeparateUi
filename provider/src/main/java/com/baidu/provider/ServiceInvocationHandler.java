package com.baidu.provider;

import com.baidu.provider.common.Slog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler implements InvocationHandler {

    private static final String TAG = "ServiceInvocationHandle";

    private final Class<?> classType;
    private final Connector connector;
    private final CallbackExchanger exchanger;

    public Class<?> getClassType() {
        return classType;
    }

    public ServiceInvocationHandler(Class<?> classType, Connector connector, CallbackExchanger exchanger) {
        Slog.v(TAG, "classType " + classType);
        this.classType = classType;
        this.connector = connector;
        this.exchanger = exchanger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Class<?> returnType = method.getReturnType();
        // 判断请求参数是否 parcelable，只能识别出浅层是否有 Parcelable，如果参数是 ArrayList<Object> 就无法判断
//        Class<?>[] classes = {int.class, short.class, long.class, float.class, double.class,
//                byte.class, char.class, boolean.class, String.class, Object[].class};
//        if (args != null) {
//            for (Object arg : args) {
//                if (!Arrays.asList(classes).contains(arg.getClass())
//                        && (!(arg instanceof Parcelable)) && (!(arg instanceof Serializable))) {
//                    Object o = retBase(returnType);
//                    XLog.e(TAG,(o + " 返回参数异常!!! " + Arrays.toString(args));
//                    return o;
//                }
//            }
//        }

        // 判断方法是否以 register 开头，是则判断参数是否是接口，是则认为是回调方法，特殊处理
        if (method.getName().startsWith("register")) {
            String key = method.getName();
            // TODO: 2021/2/17 需要校验注册回调的参数实现了 Parcelable
            //  如果是基础类型咋办？？？
            exchanger.add(args[0]);

            // 将注册监听的对象的 hasCode 传过去，让服务端创建一个回调和其一一对应，当服务端的回调被调用的时候，回调 hasCode 对应的接口
            args[0] = args[0].hashCode();
        } else if (method.getName().startsWith("unregister")) {
            exchanger.remove(args[0]);
            args[0] = args[0].hashCode();
        }
        // 封装请求信息
        Call call = new Call(classType.getName(), method.getName(), method.getParameterTypes(), args);
        Slog.v(TAG, "动态代理 " + call);
        // 发送请求
        call = connector.sendCall(call);
        if (call == null) {
            Slog.e(TAG, "call is null");
            return null;
        }
        Object returnResult = call.getResult();
        if (returnResult instanceof Exception) {
            Object o = retBase(returnType);
            Slog.e(TAG, o + "返回异常!!! " + returnResult);
            return o;
        }

        if (returnResult != null && returnType.isAssignableFrom(returnResult.getClass())) {

        }
        // 判断方法返回值是否为 void，如果为 void 且没有报错，则不关心返回结果
        if (returnType.isAssignableFrom(void.class)) {
            Slog.i(TAG, "void");
        } else {
            Slog.v(TAG, ">>>> 返回结果 " + call);
        }
        return returnResult;
    }

    // 返回基本类型的默认异常值
    private Object retBase(Class<?> returnType) {
        Class<?>[] classes = {int.class, short.class, long.class, float.class, double.class};
        for (Class<?> aClass : classes) {
            if (returnType.isAssignableFrom(aClass)) {
                return -1;
            }
        }
        if (returnType.isAssignableFrom(byte.class)) {
            return 0;
        } else if (returnType.isAssignableFrom(char.class)) {
            return 'e';
        } else if (returnType.isAssignableFrom(boolean.class)) {
            return false;
        } else {
            return null;
        }
    }
}
