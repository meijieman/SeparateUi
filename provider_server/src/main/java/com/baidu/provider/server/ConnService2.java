package com.baidu.provider.server;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import com.baidu.provider.common.Slog;

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
        Slog.i(TAG, "onCreate " + Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        String callingApp = getPackageManager().getNameForUid(Binder.getCallingUid());
        Slog.i(TAG, "packageName " + intent.getComponent() + ", pid " + Binder.getCallingPid() + ", " + callingApp);
        ICallImpl iCall = new ICallImpl();
        Slog.i(TAG, "iCall pid " + Process.myPid());
        return iCall;
    }
}
