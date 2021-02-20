package com.baidu.provider.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1() throws InstantiationException, IllegalAccessException, InvocationTargetException {

        Class<?> clazz = T.class;
        Method[] methods = clazz.getDeclaredMethods();

        // 反射类
        Object obj = clazz.newInstance();

        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            System.out.println("types " + Arrays.toString(types) + ", " + method);
            if (types.length != 0 && types[0].isInterface()) {
                System.out.println(" types[0]xxx " + types[0]);
                Object o = Proxy.newProxyInstance(ExampleUnitTest.class.getClassLoader(), new Class[]{types[0]},
                        new MyHandler());
                method.invoke(obj, o);
            }
        }


    }

    static class MyHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("打印数据invoke");
            return null;
        }
    }


    static class T {

        void reg(Callback callback) {
            // doing st

            if (callback != null) {
                callback.onChange("xxxx");
            }
        }
    }

    interface Callback {
        void onChange(String arg);
    }

}