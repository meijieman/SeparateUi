package com.baidu.provider.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 管理 同/跨进程 调用接口的实现类
 *
 * @author meijie05
 * @since 2021/2/6 4:13 PM
 */

public class DataCenter {

    private static final String TAG = "DataCenter";

    private static class Holder {
        private static final DataCenter sInstance = new DataCenter();
    }

    /**
     * 单例入口
     *
     * @return
     */
    public static DataCenter getInstance() {
        return Holder.sInstance;
    }

    private DataCenter() {

    }

    private final Map<Class<?>, Object> mMap = new HashMap<>();

    /**
     * 添加对应接口功能的实现类
     *
     * @param impl 有对应功能的接口实现类
     */
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

    /**
     * 获取具备对应功能的实现类
     *
     * @param clazz 有对应功能的实现类的接口类
     * @param <T>
     * @return
     */
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
                    T t = (T) mMap.get(aClass);
                    mMap.put(aClass, t); // 添加缓存
                    return t;
                }
            }
            Slog.w(TAG, clazz.getSimpleName() + " has no instance.");
            return null;
        }
    }
}
