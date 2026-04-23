package com.example.environmentview.NetWorkUtil.GsonToData.Weather;

import java.util.Map;

public class NowWeatherKeys {
    public static final String OBS_TIME   = "obsTime";
    public static final String TEMP       = "temp";
    public static final String FEELS_LIKE = "feelsLike";
    public static final String ICON       = "icon";
    public static final String TEXT       = "text";
    public static final String WIND_360   = "wind360";
    public static final String WIND_DIR   = "windDir";
    public static final String WIND_SCALE = "windScale";
    public static final String WIND_SPEED = "windSpeed";
    public static final String HUMIDITY   = "humidity";
    public static final String PRECIP     = "precip";
    public static final String PRESSURE   = "pressure";
    public static final String VIS        = "vis";
    public static final String CLOUD      = "cloud";
    public static final String DEW        = "dew";
    private Map<String, String> dataMap;

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

}
