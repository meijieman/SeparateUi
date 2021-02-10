package com.baidu.provider;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.baidu.common.util.SLog;

import android.os.Parcelable;

public class ServiceInvocationHandler implements InvocationHandler {

    private final Class<?> classType;
    private final Connector connector;

    public Class<?> getClassType() {
        return classType;
    }

    public ServiceInvocationHandler(Class<?> classType, Connector connector) {
        SLog.v("classType " + classType);
        this.classType = classType;
        this.connector = connector;
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
//                    SLog.e(o + " 返回参数异常!!! " + Arrays.toString(args));
//                    return o;
//                }
//            }
//        }

        // 封装请求信息
        Call call = new Call(classType.getName(), method.getName(), method.getParameterTypes(), args);
        SLog.v("动态代理 " + call);
        // 发送请求
        call = connector.sendCall(call);
        Object returnResult = call.getResult();
        if (returnResult instanceof Exception) {
            Object o = retBase(returnType);
            SLog.e(o + "返回异常!!! " + returnResult);
            return o;
        }

        if (returnResult != null && returnType.isAssignableFrom(returnResult.getClass())) {

        }
        // 判断方法返回值是否为 void，如果为 void 且没有报错，则不关心返回结果
        if (returnType.isAssignableFrom(void.class)) {
            SLog.i("void");
        } else {
            SLog.v(">>>> 返回结果 " + call);
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
