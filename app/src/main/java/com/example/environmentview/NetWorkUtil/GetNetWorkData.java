package com.example.environmentview.NetWorkUtil;

import android.os.Build;
import android.util.Log;

import com.example.environmentview.Info.WeatherInfoList;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeatherKeys;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.WeatherNowResponse;
import com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.WeatherLifeSuggestResponse;
import com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.WeatherLifeWrapper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetNetWorkData {
    public static void fetchOneSentence(GetOneKitInterface getOneKitInterface) {
        new Thread(()->{
            String url = "https://v1.hitokoto.cn/?c=f&encode=text";
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                int responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    String result = new String(con.getInputStream().readAllBytes());
                    getOneKitInterface.getOneKitResult(result);
                }
            }catch (Exception e){
                getOneKitInterface.getOneKitError(e.toString());
            }
        }).start();
    }
    public static void getWeather(GetWeatherInterface getWeatherInterface) {

        new Thread(()->{
            String url = "https://devapi.qweather.com/v7/weather/now?key=c79d6408bf00490c8cb6c79f4866eca4&location=101121008";
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                int responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    String result = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        result = new String(con.getInputStream().readAllBytes());
                    }
                    Log.w("WEATHER_RESULT", result);
                    Gson gson = new Gson();
                    WeatherNowResponse response = gson.fromJson(result, WeatherNowResponse.class);
                    Map<String, String> map = new HashMap<>();
                    map.put(NowWeatherKeys.OBS_TIME, response.now.obsTime);
                    map.put(NowWeatherKeys.TEMP, response.now.temp + "℃");
                    map.put(NowWeatherKeys.FEELS_LIKE, response.now.feelsLike + "℃");
                    map.put(NowWeatherKeys.ICON, response.now.icon);
                    map.put(NowWeatherKeys.TEXT, response.now.text);
                    map.put(NowWeatherKeys.WIND_360, response.now.wind360 + "°");
                    map.put(NowWeatherKeys.WIND_DIR, response.now.windDir);
                    map.put(NowWeatherKeys.WIND_SCALE, response.now.windScale + "级");
                    map.put(NowWeatherKeys.WIND_SPEED, response.now.windSpeed + "m/s");
                    map.put(NowWeatherKeys.HUMIDITY, response.now.humidity + "%");
                    map.put(NowWeatherKeys.PRECIP, response.now.precip + "mm");
                    map.put(NowWeatherKeys.PRESSURE, response.now.pressure + "hpa");
                    map.put(NowWeatherKeys.VIS, response.now.vis + "km");
                    map.put(NowWeatherKeys.CLOUD, response.now.cloud + "%");
                    map.put(NowWeatherKeys.DEW, response.now.dew + "℃");
                    WeatherInfoList.setNowWeatherKeys(map);
                    Log.w("WEATHER_RESULT", response.updateTime);
                    Log.w("WEATHER_RESULT", response.now.text);
                    WeatherInfoList.setWeatherNowResponse(response);
                    getWeatherInterface.getWeatherResult(response);
                }

            } catch (Exception e) {
                getWeatherInterface.getWeatherError(e.toString());
                Log.w("WEATHER_ERROR", e.toString());
            }
        }).start();
    }
    public static void getLifeInfoOrRefreshView(GetWeatherLifeInterface listener){
        new Thread(()-> {
            try {
                String url1 = "https://api.seniverse.com/v3/life/suggestion.json?key=SyZvqn30cUHlDqcRq&location=linyi&language=zh-Hans&days=1";
                URL url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String result = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        result = new String(connection.getInputStream().readAllBytes());
                    }
                    Log.w("LIFE_RESULT", result);
                    Gson gson = new Gson();
                    WeatherLifeWrapper wrapper = gson.fromJson(result, WeatherLifeWrapper.class);

                    if (wrapper != null && wrapper.results != null && !wrapper.results.isEmpty()) {
                        WeatherLifeSuggestResponse response = wrapper.results.get(0);
                        if (response.suggestion != null && !response.suggestion.isEmpty()) {
                            listener.getWeatherLifeInfo(response.suggestion.get(0));
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                listener.getWeatherLifeInfoError();
            }

        }).start();
    }


}