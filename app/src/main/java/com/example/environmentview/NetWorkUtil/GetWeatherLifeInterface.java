package com.example.environmentview.NetWorkUtil;

import com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.Suggestion;

public interface GetWeatherLifeInterface {
    void getWeatherLifeInfo(Suggestion suggestion);
    void getWeatherLifeInfoError();
}