// CallbackProxy.aidl
package com.baidu.provider;

import android.os.Bundle;
import com.baidu.provider.Call;

// Declare any non-default types here with import statements

interface CallbackProxy {

    Call onChange(in Bundle bundle);
}