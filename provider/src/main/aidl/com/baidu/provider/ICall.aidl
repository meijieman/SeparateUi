// ICall.aidl
package com.baidu.provider;

import com.baidu.provider.Call;
import com.baidu.provider.CallbackProxy;

interface ICall {

    Call getCallResult(inout Call call);

    void register(in CallbackProxy proxy);

    void unregister(in CallbackProxy proxy);
}