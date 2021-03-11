package com.baidu.separate;

import android.app.Application;

import com.baidu.che.codriver.xlog.XLog;
import com.baidu.provider.common.DataCenter;
import com.baidu.separate.impl.BookServiceImpl;
import com.baidu.separate.impl.RemoteViewServiceImpl;
import com.baidu.separate.impl.WeatherServiceImpl;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/31 5:04 PM
 */

public class App extends Application {

    private static final String TAG = "App";
    private static App sApp;

    public static App getContext() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        XLog.init(this);

        String currentProcess = ProcessUtil.getCurrentProcessName(this);
        XLog.i(TAG, "onCreate 初始化实例, " + currentProcess);
        if (getPackageName().equals(currentProcess)) {
            DataCenter.getInstance().add(new BookServiceImpl());
            DataCenter.getInstance().add(new WeatherServiceImpl());
            DataCenter.getInstance().add(new RemoteViewServiceImpl());
        }
    }
}
