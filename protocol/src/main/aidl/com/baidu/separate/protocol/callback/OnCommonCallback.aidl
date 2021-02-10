// OnCommonCallback.aidl
package com.baidu.separate.protocol.callback;

import android.os.Bundle;

// Declare any non-default types here with import statements

interface OnCommonCallback {

    void onChanged(out Bundle data);
}