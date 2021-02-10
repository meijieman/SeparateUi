package com.baidu.separate.impl;

import com.baidu.common.util.SLog;
import com.baidu.separate.protocol.RemoteViewService;
import com.baidu.separate.App;
import com.baidu.separate.MainActivity;

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
        RemoteViews remoteView = bundle.getParcelable("xxx");
        SLog.i("收到数据 remoteView " + remoteView);

        View view = remoteView.apply(App.getContext(), null);
        int layoutId = remoteView.getLayoutId();
        SLog.i("收到数据 view " + view + ", layoutId " + layoutId + ", " + Thread.currentThread());
        SLog.i("收到数据 mOnTransform " + MainActivity.mOnTransform + ", pid " + Process.myPid());
        if (MainActivity.mOnTransform != null) {
            MainActivity.mOnTransform.onReceived(view);
        }
    }
}
