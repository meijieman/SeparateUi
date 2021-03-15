package com.baidu.provider.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 注册跨进程调用接口的实现类
 *
 * @author meijie05
 * @since 2021/2/6 4:13 PM
 */

public class DataCenter {

    private static final String TAG = "DataCenter";

    private static class Holder {
        private static final DataCenter sInstance = new DataCenter();
    }

    public static DataCenter getInstance() {
        return Holder.sInstance;
    }

    private DataCenter() {

    }

    private final Map<Class<?>, Object> mMap = new HashMap<>();

    public void add(Object impl) {
        if (impl.getClass().isInterface() || impl == Class.class) {
            Slog.e(TAG, "传入的是接口");
        } else {
            Class<?>[] interfaces = impl.getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                mMap.put(anInterface, impl);
            }
        }
    }

    public <T> T get(Class<T> clazz) {
        Object obj = mMap.get(clazz);
        if (obj != null) {
            Slog.i(TAG, "get instance");
            return (T) obj;
        } else {
            // 2021/2/6  如果传入的是父接口，向下查找其子接口，在 mMap 中查找是否有实现类
            Set<Class<?>> classes = mMap.keySet();
            for (Class<?> aClass : classes) {
                if (clazz.isAssignableFrom(aClass)) {
                    Slog.i(TAG, "get instance in process");
                    return (T) mMap.get(aClass);
                }
            }
            Slog.i(TAG, "get instance 3");
            return null;
        }
    }
}
