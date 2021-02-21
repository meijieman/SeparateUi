package com.baidu.separate.impl;

import com.baidu.common.util.Slog;
import com.baidu.separate.App;
import com.baidu.separate.MainActivity;
import com.baidu.separate.protocol.RemoteViewService;

import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.RemoteViews;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 10:29 AM
 */

public class RemoteViewServiceImpl implements RemoteViewService {

    @Override
    public void sendData(Bundle bundle) {
        RemoteViews remoteView = bundle.getParcelable("remote_view");
        Slog.i("收到数据 remoteView " + remoteView);

        View view = remoteView.apply(App.getContext(), null);
        int layoutId = remoteView.getLayoutId();
        Slog.i("收到数据 view " + view + ", layoutId " + layoutId + ", " + Thread.currentThread());
        Slog.i("收到数据 mOnTransform " + MainActivity.mOnTransform + ", pid " + Process.myPid());
        if (MainActivity.mOnTransform != null) {
            MainActivity.mOnTransform.onReceived(view);
        }
    }
}
