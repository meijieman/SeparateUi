package com.baidu.provider.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/22 2:49 PM
 */

public class Test7 {
    /**
     * 动态代理 https://www.liaoxuefeng.com/wiki/1252599548343744/1264804593397984
     * 不编写实现类，直接在运行期创建某个interface的实例
     */

    @Test
    public void main() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("proxy class " + proxy.getClass().getName() + ", " + Arrays.toString(proxy.getClass().getInterfaces()));
                System.out.println(method);
                if (method.getName().equals("morning")) {
                    System.out.println("Good morning, " + args[0]);
                }

                return proxy;
            }
        };
        Hello hello = (Hello) Proxy.newProxyInstance(
                Hello.class.getClassLoader(), // 传入ClassLoader
                new Class[]{Hello.class}, // 传入要实现的接口
                handler); // 传入处理调用方法的InvocationHandler
        hello.morning("Bob");

        // 并没有复写 toString 方法和 hashCode 方法
//        System.out.println(hello.hashCode());

        hello = new HelloDynamicProxy(handler);
        hello.morning("major");
        System.out.println(hello.hashCode());
    }

    public interface Hello {
        void morning(String name);
    }

    public static class HelloDynamicProxy implements Hello {
        InvocationHandler handler;

        public HelloDynamicProxy(InvocationHandler handler) {
            this.handler = handler;
        }

        public void morning(String name) {
            try {
                handler.invoke(
                        this,
                        Hello.class.getMethod("morning", String.class),
                        new Object[]{name});
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
