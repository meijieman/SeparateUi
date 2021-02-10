package com.baidu.separate.protocol;

import com.baidu.separate.protocol.callback.OnCommonCallback;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/6 9:47 AM
 */
public interface NameService {

    String generateName(String param);

    void regCallback(OnCommonCallback callback);

    void unregCallback(OnCommonCallback callback);
}
