package com.baidu.separate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.che.codriver.xlog.XLog;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/3/10 9:40 AM
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.i(TAG, "init main");

        setContentView(R.layout.activity_main);

        TextView info = findViewById(R.id.tv_info);
        info.setText(String.format("当前进程：%s\r\n client进程：%s",
                getPackageName(), getActivityProcessName(this, ClientActivity.class)));

        findViewById(R.id.btn_open).setOnClickListener(v -> {
            startActivity(new Intent(this, ClientActivity.class));
        });

        FrameLayout fl = findViewById(R.id.fl_main);

        mOnTransform = view -> {
            XLog.i(TAG, "view 收到 " + view + ", pid " + Process.myPid());
            fl.post(() -> {
                fl.removeAllViews();
                fl.addView(view);
            });
        };
    }

    /**
     * 获取指定 activity 在 AndroidManifest中注册的 processName
     *
     * @param clazz Activity 的 class
     * @return
     */
    private String getActivityProcessName(Context ctx, Class<?> clazz) {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                for (ActivityInfo activity : info.activities) {
                    if (clazz.getName().equals(activity.name)) {
                        return activity.processName;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static OnTransform mOnTransform;

    public interface OnTransform {
        void onReceived(View view);
    }

}
