package com.example.environmentview.Info;

import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeatherKeys;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.WeatherNowResponse;
import com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.Suggestion;

import java.util.Map;

public class WeatherInfoList {
    private static WeatherNowResponse weatherNowResponse;
    private static NowWeatherKeys nowWeatherKeys;
    private static Suggestion lifeSuggestion;

    public static Suggestion getLifeSuggestion() {
        return lifeSuggestion;
    }

    public static void setLifeSuggestion(Suggestion suggestion) {
        lifeSuggestion = suggestion;
    }
    public static NowWeatherKeys getNowWeatherKeys() {
        return nowWeatherKeys;
    }

    public static void setNowWeatherKeys(Map<String, String> dataMap) {
        WeatherInfoList.nowWeatherKeys = new NowWeatherKeys();
        WeatherInfoList.nowWeatherKeys.setDataMap(dataMap);
    }

    public static WeatherNowResponse getWeatherNowResponse() {
        return weatherNowResponse;
    }

    public static void setWeatherNowResponse(WeatherNowResponse weatherNowResponse) {
        WeatherInfoList.weatherNowResponse = weatherNowResponse;
    }

}