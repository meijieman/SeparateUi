package com.baidu.provider.server;

import com.baidu.common.util.SLog;

import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:17 PM
 */

public class ConnService2 extends android.app.Service {

    @Override
    public void onCreate() {
        super.onCreate();
        SLog.i("onCreate " + Process.myPid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ICallImpl iCall = new ICallImpl();
        SLog.i("iCall pid " + Process.myPid());
        return iCall;
    }
}
