package com.baidu.separate.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.common.util.Slog;
import com.baidu.separate.protocol.BookService;
import com.baidu.separate.protocol.bean.Book;
import com.baidu.separate.protocol.bean.Result;
import com.baidu.separate.protocol.callback.OnBookListener;
import com.baidu.separate.protocol.callback.OnCommonCallback;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * TODO
 *
 * @author meijie05
 * @since 2020/12/2 11:45 AM
 */

public class BookServiceImpl implements BookService {

    private final List<Book> mBooks = new ArrayList<>();

    // 需使用 RemoteCallbackList
    private final RemoteCallbackList<OnCommonCallback> mCallbacks = new RemoteCallbackList<>();

    private final List<OnBookListener> mListeners = new ArrayList<>();

    public BookServiceImpl() {

    }

    @Override
    public boolean addBook(Book book) {
        Slog.i("book " + book);
        return mBooks.add(book);
    }

    @Override
    public void removeBook(int no) {
        Slog.i("no " + no);
        for (Book book : mBooks) {
            if (book.getNo() == no) {
                mBooks.remove(book);

                Result result = new Result();
                result.setCode(100);
                result.setMsg(no + "移除成功 ");

                Slog.i("result " + result);
                for (OnBookListener listener : mListeners) {
                    listener.onChanged(result);
                }

                Bundle bundle = new Bundle();
                bundle.putInt("code", 100);
                bundle.putString("msg", no + "移除成功 ");

                int count = mCallbacks.beginBroadcast();
                for (int i = 0; i < count; i++) {
                    try {
                        mCallbacks.getBroadcastItem(i).onChanged(bundle);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallbacks.finishBroadcast();

                break;
            }
        }
    }

//    private void notify(Result result) {
//        int count = mListeners.beginBroadcast();
//        for (int i = 0; i < count; i++) {
//            try {
//                mListeners.getBroadcastItem(i).onChanged(result);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        mListeners.finishBroadcast();
//    }

    @Override
    public Book getBook(int id) {
        for (Book book : mBooks) {
            if (book.getNo() == id) {
                return book;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mBooks.size();
    }

    @Override
    public List<Book> getBooks(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            boolean isAvailable = jo.optBoolean("isAvailable", false);
            if (isAvailable) {
                final List<Book> books = new ArrayList<>();
                for (Book book : mBooks) {
                    if (book.isAvailable()) {
                        books.add(book);
                    }
                }
                return books;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void register(OnBookListener listener) {
        Slog.i("listener " + listener);
        mListeners.add(listener);
    }

    @Override
    public void onUnregister(OnBookListener listener) {
        Slog.i("listener " + listener);
        // 跨进程注销普通 listener 存在问题
        mListeners.remove(listener);
    }

    @Override
    public String generateName(String param) {
        return "生成名字 " + param;
    }

    @Override
    public void regCallback(OnCommonCallback callback) {
        Slog.i("callback " + callback);
        mCallbacks.register(callback);
    }

    @Override
    public void unregCallback(OnCommonCallback callback) {
        Slog.i("callback " + callback);
        mCallbacks.unregister(callback);
    }
}
