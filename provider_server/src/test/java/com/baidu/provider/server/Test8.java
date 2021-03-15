package com.baidu.provider.server;

import org.junit.Test;

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

