package com.baidu.demo.client;

import com.baidu.common.util.Slog;
import com.baidu.common.util.ToastUtil;
import com.baidu.provider.Provider;
import com.baidu.separate.protocol.BookService;
import com.baidu.separate.protocol.RemoteViewService;
import com.baidu.separate.protocol.WeatherService;
import com.baidu.separate.protocol.bean.Book;
import com.baidu.separate.protocol.bean.Result;
import com.baidu.separate.protocol.callback.OnBookListener;
import com.baidu.separate.protocol.callback.OnCommonCallback;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdPartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_CLICK = "action_click";

    private final OnBookListener mListener = new OnBookListener() {
        @Override
        public void onChanged(Result result) {
            Slog.i("result " + result);
            mText.setText("收到更新 " + result);
        }
    };
    private final OnCommonCallback mCallback = new OnCommonCallback.Stub() {
        @Override
        public void onChanged(Bundle data) throws RemoteException {
            Slog.i("callback " + data);
            mText.setText("callback " + data);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 界面显示在其他进程，单其逻辑还运行在本进程
            Slog.i("点击按钮===");
            ToastUtil.getInstance().showShort("点击按钮");
            mRemoteView.setTextViewText(R.id.tv_text, "" + System.currentTimeMillis() % 100);
            Bundle b = new Bundle();
            b.putParcelable("remote_view", mRemoteView);

            RemoteViewService remoteViewService = Provider.getInstance().get(RemoteViewService.class);
            remoteViewService.sendData(b);
        }
    };

    private TextView mText;
    private RemoteViews mRemoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_part);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_remove).setOnClickListener(this);
        findViewById(R.id.btn_count).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_unreg).setOnClickListener(this);
        findViewById(R.id.btn_reg_comm).setOnClickListener(this);
        findViewById(R.id.btn_unreg_comm).setOnClickListener(this);

        findViewById(R.id.btn_remote_view).setOnClickListener(this);

        mText = findViewById(R.id.tv_text);
        ImageView img = findViewById(R.id.iv_img);

        ToastUtil.getInstance().init(this);
        Slog.i("onCreate");
        // 初始化
        Provider.getInstance().init(getApplicationContext(), true);
        Slog.i("init end");

        IntentFilter filter = new IntentFilter(ACTION_CLICK);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        BookService bookService = Provider.getInstance().get(BookService.class);
        if (id == R.id.btn_add) {
            long start = System.currentTimeMillis();

            Book book = new Book();
            book.setNo(0);
            book.setName("第一行代码" + start);
            book.setAvailable(true);

            boolean result = bookService.addBook(book);
            Slog.w("结果: " + result + ", used: " + (System.currentTimeMillis() - start));
            mText.setText("添加 " + result);
        } else if (id == R.id.btn_remove) {
            bookService.removeBook(0);

        } else if (id == R.id.btn_count) {
            int count = bookService.getCount();
            Slog.w("count " + count);
            mText.setText("总数 " + count);
        } else if (id == R.id.btn_register) {
            // FIXME: 2021/1/27 不支持回调中设置为 Serializable，为什么支持 ArrayList 呢？
            //  java.lang.RuntimeException: Parcelable encountered ClassNotFoundException reading a Serializable object (name = com.baidu.demo.client.-$$Lambda$ThirdPartActivity$FCOslk6SrLTXtREJt5DrDBTydb4)
            long start = System.currentTimeMillis();
            bookService.register(mListener);
            Slog.w("time used: " + (System.currentTimeMillis() - start));
        } else if (id == R.id.btn_unreg) {
            long start = System.currentTimeMillis();
            bookService.unregister(mListener);
            Slog.w("time used: " + (System.currentTimeMillis() - start));
        } else if (id == R.id.btn_reg_comm) {
            bookService.regCallback(mCallback);
        } else if (id == R.id.btn_unreg_comm) {
            bookService.unregCallback(mCallback);
        } else if (id == R.id.btn_remote_view) {
            RemoteViewService remoteViewService = Provider.getInstance().get(RemoteViewService.class);

            mRemoteView = new RemoteViews(getPackageName(), R.layout.layout_notification);
            mRemoteView.setTextViewText(R.id.tv_text, "title");
            Intent intent = new Intent(ACTION_CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            mRemoteView.setOnClickPendingIntent(R.id.btn_add, pendingIntent);
            Slog.i("发送数据 remoteView " + mRemoteView + ", pid " + Process.myPid());

            Bundle b = new Bundle();
            b.putParcelable("remote_view", mRemoteView);
            remoteViewService.sendData(b);
        }
    }

    void method() {
        WeatherService service = Provider.getInstance().get(WeatherService.class);
        String weather = service.getWeather();

    }

}