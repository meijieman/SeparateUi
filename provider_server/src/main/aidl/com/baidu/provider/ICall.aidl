// ICall.aidl
package com.baidu.provider;

import com.baidu.provider.Call;

interface ICall {
    Call getCallResult(inout Call call);
}