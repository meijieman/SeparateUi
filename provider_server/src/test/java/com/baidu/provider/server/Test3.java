package com.baidu.provider.server;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java利用反射为接口实例化对象并调用
 * https://blog.csdn.net/qq_37956311/article/details/85328915
 *
 * @author meijie05
 * @since 2021/2/17 2:08 PM
 */

public class Test3 {
    @Test
    public void main() throws Exception {

//        Class<?> myLibraryClazz = Class.forName("xxxxx.MyLibrary");//类
//        Class<?> myCallbackClazz = Class.forName("xxxxx.MyLibrary$MyCallback");//接口
        Class<?> myLibraryClazz = MyLibrary.class;
        Class<?> myCallbackClazz = MyLibrary.MyCallback.class;

        MyHandler myHandler = new MyHandler();//类
        MyLibrary.MyCallback myCallback = (MyLibrary.MyCallback) Proxy.newProxyInstance(
                Test3.class.getClassLoader(),//类加载器
                new Class[]{myCallbackClazz},//接口数组
                myHandler//为接口实现的对应具体方法
        );

//        Method method = myLibraryClazz.getDeclaredMethod("mainMethod", MyLibrary.MyCallback.class);//（类名，参数类型）
//        method.invoke(myLibraryClazz.newInstance(), myCallback);//调用方法，（实例化对象，内部接口实现对象）

        MyLibrary my = new MyLibrary();
        my.mainMethod(myCallback);

    }

    static class MyHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("doing MyHandler invoke... " + hashCode());
            return null;
        }
    }


    public static class MyLibrary {

        public interface MyCallback {
            void doMyCallback();
        }

        public void mainMethod(MyCallback myCallback) {
            System.out.println("doing MyLibrary mainMethod...");
            myCallback.doMyCallback();
            System.out.println("==============");
            System.out.println(myCallback.toString()); // 这个酒很搞人了，返回的为 null；如何才能返回动态对象的 toString 方法呢；
        }
    }
}


