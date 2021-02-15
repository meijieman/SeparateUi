package com.baidu.separate;

import com.baidu.common.DataCenter;
import com.baidu.common.util.Slog;
import com.baidu.separate.impl.BookServiceImpl;
import com.baidu.separate.impl.RemoteViewServiceImpl;
import com.baidu.separate.impl.WeatherServiceImpl;

import android.app.Application;
import android.os.Process;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/31 5:04 PM
 */

public class App extends Application {

    private static App sApp;

    public static App getContext() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        Slog.i("onCreate 初始化实例, pid " + Process.myPid());
        DataCenter.getInstance().add(new BookServiceImpl());
        DataCenter.getInstance().add(new WeatherServiceImpl());
        DataCenter.getInstance().add(new RemoteViewServiceImpl());
    }
}
