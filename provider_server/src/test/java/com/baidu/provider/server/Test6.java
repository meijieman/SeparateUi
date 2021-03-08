package com.baidu.provider.server;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/22 1:04 PM
 */

public class Test6 {

    @Test
    public void main() throws Exception {
        Class<?> myCallbackClazz = Test6.MyLibrary.MyCallback.class;
        MyHandler2 handler = new MyHandler2();
        Test6.MyLibrary.MyCallback myCallback = (Test6.MyLibrary.MyCallback) Proxy.newProxyInstance(
                Test6.class.getClassLoader(),//类加载器
                new Class[]{myCallbackClazz},//接口数组
                handler//为接口实现的对应具体方法
        );

        Test6.MyLibrary my = new Test6.MyLibrary();
        my.mainMethod(myCallback);

    }


    static class MyHandler2 implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("method" + method);
            System.out.println("doing MyHandler invoke... " + method + ", " + hashCode());

            return proxy;
        }
    }

    public static class MyLibrary {

        public interface MyCallback {
            void doMyCallback();
        }

        public void mainMethod(Test6.MyLibrary.MyCallback myCallback) {
            System.out.println("doing MyLibrary mainMethod...");
            myCallback.doMyCallback();
            System.out.println("==============");
        }
    }
}
