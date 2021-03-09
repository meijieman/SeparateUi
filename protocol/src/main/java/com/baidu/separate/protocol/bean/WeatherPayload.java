package com.baidu.separate.protocol.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author meijie05
 * @since 2021/3/8 5:16 PM
 */

public class WeatherPayload implements Serializable {

    private String city;
    private WeatherForecastBean bean;

    private ArrayList<WeatherForecastBean> weatherForecast;

    public static class WeatherForecastBean implements Serializable {

        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "WeatherForecastBean{" +
                    "date='" + date + '\'' +
                    '}';
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public WeatherForecastBean getBean() {
        return bean;
    }

    public void setBean(WeatherForecastBean bean) {
        this.bean = bean;
    }

    public List<WeatherForecastBean> getWeatherForecast() {
        return weatherForecast;
    }

    public void setWeatherForecast(ArrayList<WeatherForecastBean> weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    @Override
    public String toString() {
        return "WeatherPayload{" +
                "city='" + city + '\'' +
                ", bean=" + bean +
                ", weatherForecast=" + weatherForecast +
                '}' + hashCode();
    }
}
