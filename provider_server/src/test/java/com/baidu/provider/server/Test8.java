package com.baidu.provider.server;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/3/9 3:35 PM
 */

public class Test8 {

    @Test
    public void test1() {
        B b = new B("abc");

    }

    @Test
    public void test2() {

        System.out.println("Parent.isAssignableFrom(Child) " + Parent.class.isAssignableFrom(Child.class));
        System.out.println("Child.isAssignableFrom(Parent) " + Child.class.isAssignableFrom(Child.class));
        System.out.println("Child.isAssignableFrom(Parent) " + Parent.class.isAssignableFrom(Parent.class));
        System.out.println("Child.isAssignableFrom(Parent) " + Child.class.isAssignableFrom(Parent.class));
    }

    interface Parent {

    }

    interface Child extends Parent {

        void make();
    }

    static class Entity implements Child {
        int id;

        byte[] data = new byte[1024 * 1024 * 2];

        @Override
        public void make() {
            id++;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "id=" + id +
                    '}';
        }
    }

    @Test
    public void test3() {
        Map<Class<?>, Object> map = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        long free = runtime.freeMemory();
        Entity entity = new Entity();
        map.put(String.class, entity);
        map.put(Integer.class, entity);
        map.put(Boolean.class, entity);
        System.out.println("used " + (free - runtime.freeMemory()));

        System.out.println(map);
        entity.make();
        entity.make();

        System.out.println(map);

    }

    @Test
    public void test4() {
        // 关于Java的Class类概念的一些疑问? - weishu的回答 - 知乎
        //https://www.zhihu.com/question/67270393/answer/251059300
        Entity entity = new Entity();
        Class<?> clazz = entity.getClass();

        System.out.println("是否  " + clazz.isInterface());
        System.out.println("是否  " + clazz.isLocalClass()); // 方法内的类
        System.out.println("是否  " + clazz.isAnnotation()); // 注解
        System.out.println("是否  " + clazz.isAnonymousClass());
        System.out.println("是否  " + clazz.isMemberClass()); // 成员类
        System.out.println("是否  " + clazz.isPrimitive());
        System.out.println("是否  " + clazz.isSynthetic());


        Set<Class<?>> interfaces = getInterfaces(clazz, null);
        System.out.println(interfaces);


        Set<Class<?>> interfaces1 = getInterfaces(new ArrayList<String>().getClass(), null);
        System.out.println(interfaces1);

    }

    private static Set<Class<?>> getInterfaces(Class<?> clazz, Set<Class<?>> result) {
        if (result == null) {
            result = new HashSet<>();
        }
        if (clazz == null) {
            return result;
        }
        if (clazz.getInterfaces().length != 0) {
            for (Class<?> anInterface : clazz.getInterfaces()) {
                result.add(anInterface);
                getInterfaces(anInterface, result);
            }
//            if (clazz.getSuperclass() != Object.class) {
//                getInterfaces(clazz.getSuperclass(), result);
//            }
        }

        return result;
    }

    /**
     * 获取对象的所有接口，不包括对象的父类的所有接口
     *
     * @param clazz
     * @return
     */
    public static Set<Class<?>> getInterfaces(Class<?> clazz) {
        return getInterfaces(clazz, null);
    }


    @Test
    public void test5() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("entity " + (1024 * 8));
        long free = runtime.freeMemory();
        Entity2 entity2 = new Entity2();
        new Child2() {
        };
        System.out.println("used " + (free - runtime.freeMemory()));

    }


}

abstract class A {

    A() {
        System.out.println("a constructor");
    }

    A(boolean b) {
        System.out.println("a 1 constructor");

    }

}

class B extends A {

    B(String str) {
        System.out.println("b constructor");

    }
}

