package com.baidu.common;

import java.util.ArrayList;
import java.util.List;

import com.baidu.common.util.SLog;

import android.os.Process;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 11:44 PM
 */

public class DataCenter {

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
        SLog.d("getImpls, pid " + Process.myPid());
        return mImpls;
    }

    public void add(Object impl) {
        mImpls.add(impl);
    }


}
