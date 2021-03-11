package com.baidu.provider.common;

import android.os.Process;

import com.baidu.che.codriver.xlog.XLog;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 11:44 PM
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

    private final List<Object> mImpls = new ArrayList<>();

    public List<Object> getImpls() {
        XLog.d(TAG, "getImpls, pid " + Process.myPid());
        return mImpls;
    }

    public void add(Object impl) {
        mImpls.add(impl);
    }


}
