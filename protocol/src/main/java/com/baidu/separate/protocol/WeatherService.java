package com.baidu.separate.protocol;

import com.baidu.separate.protocol.bean.WeatherPayload;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/2/8 5:37 PM
 */

public interface WeatherService {

    String getWeather();

    WeatherPayload showBodyView(WeatherPayload payload);

    void registerCallback(OnWeatherCallback callback);

    interface OnWeatherCallback {
        void onWeather(WeatherPayload payload);
    }
}
