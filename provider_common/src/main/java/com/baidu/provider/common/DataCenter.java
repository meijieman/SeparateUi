package com.baidu.provider.common;

import com.baidu.che.codriver.xlog.XLog;

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
        mMap.put(impl.getClass(), impl);
    }

    public <T> T get(Class<T> clazz) {
        Object obj = mMap.get(clazz);
        if (obj != null) {
            XLog.i(TAG, "get instance");
            return (T) obj;
        } else {
            // 2021/2/6  如果传入的是父接口，向下查找其子接口，在 mMap 中查找是否有实现类
            Set<Class<?>> classes = mMap.keySet();
            for (Class<?> aClass : classes) {
                if (clazz.isAssignableFrom(aClass)) {
                    XLog.i(TAG, "get instance across process");
                    return (T) mMap.get(aClass);
                }
            }
            XLog.i(TAG, "get instance 3");
            return null;
        }
    }
}
