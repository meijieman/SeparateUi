package com.baidu.provider.server;

import com.baidu.provider.JavapUtil;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/21 1:48 AM
 */

public class Test5 {

    @Test
    public void main() throws Exception {

        Class<?> myCallbackClazz = Test5.MyLibrary.MyCallback.class;

        // 不编写实现类，直接在运行期创建某个interface的实例
        Test5.MyHandler myHandler = new Test5.MyHandler();//类
        Test5.MyLibrary.MyCallback myCallback = (Test5.MyLibrary.MyCallback) Proxy.newProxyInstance(
                Test5.class.getClassLoader(),//类加载器
                new Class[]{myCallbackClazz},//接口数组
                myHandler//为接口实现的对应具体方法
        );

        Test5.MyLibrary my = new Test5.MyLibrary();
        my.mainMethod(myCallback);
    }

    static class MyHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("doing MyHandler invoke... " + method + ", " + hashCode());
            if ("toString".equals(method.getName())) {
                return "拿不到toString 方法啊咋办（可以将 MyHandler 和代理对象绑定，然后返回 MyHandler 的toString 方法 ";

            }
            System.out.println("doing MyHandler invoke222... ");
            return null;
        }
    }

    // FIXME: 2021/2/21 为什么动态代理对象的返回值为 null？？？ 为什么 invoke 方法中不能打印 proxy

    public static class MyLibrary {

        public interface MyCallback {
            void doMyCallback();
        }

        public void mainMethod(Test5.MyLibrary.MyCallback myCallback) {
            System.out.println("doing MyLibrary mainMethod...");
            myCallback.doMyCallback();
            System.out.println("==============");
//            System.out.println(myCallback.toString()); // 这个就很搞人了，返回的为 null；如何才能返回动态对象的 toString 方法呢；
//            Class<? extends MyCallback> aClass = myCallback.getClass();
//            System.out.println("class " + aClass);
        }
    }

    @Test
    public void test1() {
        Method[] methods = MyLibrary.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("方法 " + method);
            System.out.println(JavapUtil.getSignature(method));
        }

        System.out.println("==============");

        Method[] methods2 = getClass().getDeclaredMethods();
        for (Method method : methods2) {
            System.out.println("方法 " + method);
            System.out.println(JavapUtil.getSignature(method));
        }
    }

    @Test
    public void test2() {

    }

}
