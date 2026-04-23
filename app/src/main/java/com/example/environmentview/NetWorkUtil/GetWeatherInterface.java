package com.example.environmentview.NetWorkUtil;

import com.example.environmentview.NetWorkUtil.GsonToData.Weather.WeatherNowResponse;

public interface GetWeatherInterface {
    void getWeatherResult(WeatherNowResponse result);
    void getWeatherError(String error);

}
