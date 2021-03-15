package com.baidu.provider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.baidu.provider.common.Slog;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 负责创建远程链接，注册回调监听
 */
class Connector {
    private static final String TAG = "Connector";

    private final CallbackExchanger mExchanger;

    public Connector(CallbackExchanger exchanger) {
        mExchanger = exchanger;
    }

    private final CountDownLatch mCountDownLatch = new CountDownLatch(1);

    private ICall mICall;
    private final AtomicBoolean mIsConnected = new AtomicBoolean(false);
    private final CallbackProxy mCallback = new CallbackProxy.Stub() {
        @Override
        public Call onChange(Bundle bundle) throws RemoteException {
            Slog.i(TAG, "onChange " + bundle);
            Call call = mExchanger.onChanged(bundle);
            Slog.i(TAG, "call " + call);
            return call;
        }
    };

    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mICall = ICall.Stub.asInterface(service);
            Slog.i(TAG, "连接成功, " + Thread.currentThread().getName());

            try {
                mICall.asBinder().linkToDeath(mDeathRecipient, 0);
                mICall.register(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIsConnected.set(true);
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Slog.w(TAG, "链接断开，触发重连");
            mIsConnected.set(false);
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Slog.w(TAG, "binder died");
            if (mICall == null) {
                return;
            }
            mICall.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mICall = null;
        }
    };

    /**
     * 创建远程连接
     *
     * @param ctx
     * @param targetPackageName 目标服务所在的包名
     */
    public void connect(Context ctx, String targetPackageName) {
        Slog.i(TAG, "连接 ");
        if (mIsConnected.get()) {
            Slog.i(TAG, "重复连接，返回 ");
            return;
        }
        // 连接远程服务
        Intent intent = new Intent("com_baidu_provider_conn_service");
        if (targetPackageName != null) {
            intent.setPackage(targetPackageName);
        } else {
            intent = explicitIntent(ctx, intent);
            if (intent == null) {
                Slog.e(TAG, "绑定服务失败，服务不存在");
                return;
            }
            // 正常输出explicitIntent #Intent;action=com_baidu_provider_conn_service;component=com.baidu.che.codriver/com.baidu.provider.server.ConnService;end
            // 如果有多个 app 集成了 provider_server.aar 调用 com.baidu.provider.Connector#explicitIntent 会绑定失败
            Slog.d(TAG, "explicitIntent " + intent.toURI());
        }

        Slog.d(TAG, "intent " + intent.toURI());

        try {
//            explicitIntent.putExtra("pid", Process.myPid());
            boolean isSuccess = ctx.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
            Slog.v(TAG, "bind " + isSuccess);
            if (isSuccess) {
                try {
                    mCountDownLatch.await(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Slog.e(TAG, "链接超时 ");
                }
            } else {
                Slog.e(TAG, "绑定服务失败，服务不存在2");
            }
        } catch (Exception e) {
            Slog.e(TAG, "绑定服务失败 e " + e);
            e.printStackTrace();
        }
    }

    public Call sendCall(Call call) {
        Slog.v(TAG, "发送请求 " + call);
        if (mICall == null) {
            Call call1 = new Call();
            call1.setResult(new RuntimeException("绑定服务失败"));
            return call1;
        }

        try {
            Call callResult = mICall.getCallResult(call);
            Slog.v(TAG, "收到结果 " + callResult);
            return callResult;
        } catch (Exception e) {
            Slog.e(TAG, "产生异常 " + e);
            Call call1 = new Call();
            call1.setResult(e);
            return call1;
        }
    }

    public static Intent explicitIntent(Context context, Intent implicitIntent) {
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentServices(implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            Slog.e(TAG, "查找服务失败 " + resolveInfoList);
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

}