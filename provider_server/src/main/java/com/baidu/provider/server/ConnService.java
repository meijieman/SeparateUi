package com.baidu.provider.server;

import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import com.baidu.common.util.Slog;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:17 PM
 */

public class ConnService extends android.app.Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Slog.i("onCreate pid " + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        ICallImpl iCall = new ICallImpl();
        Slog.i("iCall pid " + Process.myPid());
        return iCall;
    }
}
