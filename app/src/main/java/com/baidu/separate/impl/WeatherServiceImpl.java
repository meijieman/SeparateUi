package com.baidu.separate.impl;

import com.baidu.che.codriver.xlog.XLog;
import com.baidu.separate.protocol.WeatherService;
import com.baidu.separate.protocol.bean.WeatherPayload;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/8 5:38 PM
 */

public class WeatherServiceImpl implements WeatherService {

    private static final String TAG = "WeatherServiceImpl";
    private final List<OnWeatherCallback> mCallbacks = new ArrayList<>();

    @Override
    public String getWeather() {

        return "{\"weather\":\"sunny\", \"temp\":20}";
    }

    @Override
    public WeatherPayload showBodyView(WeatherPayload payload) {
        XLog.i(TAG, "显示天气 " + payload);
        payload.setCity("北京");
        payload.setBean(null);

        for (OnWeatherCallback callback : mCallbacks) {
            callback.onWeather(payload);
        }
        return payload;
    }

    @Override
    public void registerCallback(OnWeatherCallback callback) {
        mCallbacks.add(callback);
    }
}
