// OnBookListener.aidl
package com.baidu.separate.protocol.callback;

import com.baidu.separate.protocol.bean.Result;

// Declare any non-default types here with import statements

interface OnBookListener2 {

    void onChanged(out Result result);
}