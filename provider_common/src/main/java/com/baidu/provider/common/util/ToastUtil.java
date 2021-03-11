package com.baidu.provider.common.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
    private Context mContext;

    private static class Singleton {
        private static ToastUtil sInstance = new ToastUtil();
    }

    public static ToastUtil getInstance() {
        return Singleton.sInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    private Toast init(CharSequence message, int duration, int gravity) {
        Toast toast = Toast.makeText(mContext, message, duration);
//        toast.setGravity(gravity, 0, 0);
        return toast;
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public void showShort(CharSequence message) {
        init(message, Toast.LENGTH_SHORT, Gravity.CENTER).show();
    }


}