package com.baidu.provider.server;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import com.baidu.che.codriver.xlog.XLog;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/1/23 5:17 PM
 */

public class ConnService2 extends android.app.Service {

    private static final String TAG = "ConnService2";

    @Override
    public void onCreate() {
        super.onCreate();
        XLog.i(TAG, "onCreate " + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        String callingApp = getPackageManager().getNameForUid(Binder.getCallingUid());
        XLog.i(TAG, "packageName " + intent.getComponent() + ", pid " + Binder.getCallingPid() + ", " + callingApp);
        ICallImpl iCall = new ICallImpl();
        XLog.i(TAG, "iCall pid " + Process.myPid());
        return iCall;
    }
}
