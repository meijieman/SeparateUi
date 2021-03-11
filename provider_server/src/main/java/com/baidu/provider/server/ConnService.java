package com.baidu.provider.server;

import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import com.baidu.che.codriver.xlog.XLog;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:17 PM
 */

public class ConnService extends android.app.Service {

    private static final String TAG = "ConnService";

    @Override
    public void onCreate() {
        super.onCreate();
        XLog.i(TAG, "onCreate pid " + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        ICallImpl iCall = new ICallImpl();
        XLog.i(TAG, "iCall pid " + Process.myPid());
        return iCall;
    }
}
