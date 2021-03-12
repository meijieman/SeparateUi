package com.baidu.separate.protocol.callback;

import com.baidu.separate.protocol.bean.Result;

public interface OnBookListener {

    void onChanged(Result result);

    void onChanged(Result result, int type);

    String getDate(Result result);

}