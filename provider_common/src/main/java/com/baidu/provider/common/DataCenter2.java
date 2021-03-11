package com.baidu.common;

import com.baidu.common.util.Slog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DataCenter 优化版
 *
 * @author meijie05
 * @since 2021/2/6 4:13 PM
 */

public class DataCenter2 {

    private static class Holder {
        private static final DataCenter2 sInstance = new DataCenter2();
    }

    public static DataCenter2 getInstance() {
        return DataCenter2.Holder.sInstance;
    }

    private DataCenter2() {

    }

    private final Map<Class<?>, Object> mMap = new HashMap<>();

    public void add(Object impl) {
        mMap.put(impl.getClass(), impl);
    }

    public <T> T get(Class<T> clazz) {
        Object obj = mMap.get(clazz);
        if (obj != null) {
            Slog.i("getImpls");
            return (T) obj;
        } else {
            // 2021/2/6  如果传入的是父接口，向下查找其子接口，在 mMap 中查找是否有实现类
            Set<Class<?>> classes = mMap.keySet();
            for (Class<?> aClass : classes) {
                if (clazz.isAssignableFrom(aClass)) {
                    Slog.i("getImpls 2");
                    return (T) mMap.get(aClass);
                }
            }
            Slog.i("getImpls 3");
            return null;
        }
    }
}
