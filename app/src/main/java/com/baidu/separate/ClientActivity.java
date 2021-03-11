package com.baidu.separate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.che.codriver.xlog.XLog;
import com.baidu.provider.Provider;
import com.baidu.provider.common.util.Slog;
import com.baidu.separate.protocol.BookService;
import com.baidu.separate.protocol.bean.Book;
import com.baidu.separate.protocol.bean.Result;
import com.baidu.separate.protocol.callback.OnBookListener;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClientActivity";

    private final OnBookListener mListener = new OnBookListener() {
        @Override
        public void onChanged(Result result) {
            XLog.i(TAG, "result " + result);
            mText.setText("收到更新 " + result);
        }
    };
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        XLog.d(TAG, "init client");

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_remove).setOnClickListener(this);
        findViewById(R.id.btn_count).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_unreg).setOnClickListener(this);

        mText = findViewById(R.id.tv_text);
        ImageView img = findViewById(R.id.iv_img);

        String currentProcessName = ProcessUtil.getCurrentProcessName(this);
        XLog.i(TAG, "packageName " + getPackageName() + ", processName " + currentProcessName);
        boolean isIpc = !getPackageName().equals(currentProcessName);
        // 初始化
        Provider.getInstance().init(ClientActivity.this, isIpc);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        BookService bookService = Provider.getInstance().get(BookService.class);
        if (id == R.id.btn_add) {
            long start = System.currentTimeMillis();

            Book book = new Book();
            book.setNo(0);
            book.setName("第一行代码");
            book.setAvailable(true);

            boolean result = bookService.addBook(book);
            Slog.w("结果: " + result + ", used: " + (System.currentTimeMillis() - start));
            mText.setText("添加 " + result);
        } else if (id == R.id.btn_remove) {
            bookService.removeBook(0);
        } else if (id == R.id.btn_count) {
            long start = System.currentTimeMillis();
            int count = bookService.getCount();
            Slog.w("count " + count);
            mText.setText("总数 " + count);
            Slog.w("time used: " + (System.currentTimeMillis() - start));
        } else if (id == R.id.btn_register) {
            long start = System.currentTimeMillis();
            bookService.register(mListener);
            Slog.w("time used: " + (System.currentTimeMillis() - start));
        } else if (id == R.id.btn_unreg) {
            long start = System.currentTimeMillis();
            bookService.unregister(mListener);
            Slog.w("time used: " + (System.currentTimeMillis() - start));
        }
    }


}