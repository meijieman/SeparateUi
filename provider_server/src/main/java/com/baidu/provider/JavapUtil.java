package com.baidu.provider;

import java.lang.reflect.Method;

/**
 * 获取Java的方法签名，参考javah -jni 类路径/javap -s 类路径。
 * eg. javap -s com.lqr.test.Hello
 *
 * @author meijie05
 * @since 2021/2/22 10:44 AM
 */

public class JavapUtil {

    /**
     * 获取方法的签名 = 全类名 + 方法名 + javap -s 获取到的方法签名
     */
    public static String getSignature(Method method) {
        // 直接用 Method#toString 更香啊
        return method.getDeclaringClass().getCanonicalName() + "#" + method.getName() + getMethodDesc(method);
    }

    public static String getMethodDesc(final Method method) {
        final StringBuilder buf = new StringBuilder();
        buf.append("(");
        final Class<?>[] types = method.getParameterTypes();
        for (Class<?> type : types) {
            buf.append(getClassDesc(type));
        }
        buf.append(")");
        buf.append(getClassDesc(method.getReturnType()));
        return buf.toString();
    }

    private static String getClassDesc(final Class<?> returnType) {
        if (returnType == null) {
            return "";
        }
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }
        if (returnType.isArray()) {
            return "[" + getClassDesc(returnType.getComponentType());
        }
        return "L" + getType(returnType) + ";";
    }

    private static String getType(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return "[" + getClassDesc(parameterType.getComponentType());
        }
        if (!parameterType.isPrimitive()) {
            final String clsName = parameterType.getName();
            return clsName.replaceAll("\\.", "/");
        }
        return getPrimitiveLetter(parameterType);
    }

    private static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }
        if (Void.TYPE.equals(type)) {
            return "V";
        }
        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }
        if (Character.TYPE.equals(type)) {
            return "C";
        }
        if (Byte.TYPE.equals(type)) {
            return "B";
        }
        if (Short.TYPE.equals(type)) {
            return "S";
        }
        if (Float.TYPE.equals(type)) {
            return "F";
        }
        if (Long.TYPE.equals(type)) {
            return "J";
        }
        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }

}
