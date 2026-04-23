package com.example.environmentview.Event;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.example.environmentview.Adapter.WeaterAdapter.LifeSuggestionAdapter;
import com.example.environmentview.Adapter.WeaterAdapter.LifeSuggestionAdapter.LifeItem;
import com.example.environmentview.Adapter.Model.WeatherModel.WeatherModel;
import com.example.environmentview.Adapter.WeaterAdapter.WeatherRecyclerViewAdapter;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Info.WeatherInfoList;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeather;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeatherKeys;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.WeatherNowResponse;
import com.example.environmentview.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherEventHandler {

    public static void setViewAdapter(final Activity activity, final Context context, RecyclerView rv_weather_info){
        ArrayList<WeatherModel> weatherInfo = new ArrayList<>();
        weatherInfo.add(new WeatherModel("温度", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.TEMP), "thermostat"));
        weatherInfo.add(new WeatherModel("体感", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.FEELS_LIKE), "accessibility"));
        weatherInfo.add(new WeatherModel("代码", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.ICON), "cloud"));
        weatherInfo.add(new WeatherModel("天气", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.TEXT), "water_drop"));
        weatherInfo.add(new WeatherModel("角度", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.WIND_360), "explore"));
        weatherInfo.add(new WeatherModel("风向", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.WIND_DIR), "air"));
        weatherInfo.add(new WeatherModel("风级", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.WIND_SCALE), "speed"));
        weatherInfo.add(new WeatherModel("风速", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.WIND_SPEED), "compress"));
        weatherInfo.add(new WeatherModel("湿度", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.HUMIDITY), "visibility"));
        weatherInfo.add(new WeatherModel("降水", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.PRECIP), "filter_drama"));
        weatherInfo.add(new WeatherModel("气压", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.PRESSURE), "compress"));
        weatherInfo.add(new WeatherModel("可视", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.VIS), "visibility"));
        weatherInfo.add(new WeatherModel("云量", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.CLOUD), "thermostat"));
        weatherInfo.add(new WeatherModel("露点", WeatherInfoList.getNowWeatherKeys().getDataMap().get(NowWeatherKeys.DEW), "thermostat"));
        WeatherRecyclerViewAdapter adapter = new WeatherRecyclerViewAdapter(context, weatherInfo);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();

        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);

        rv_weather_info.setItemAnimator(itemAnimator);
        rv_weather_info.setAdapter(adapter);
        rv_weather_info.setLayoutManager(new GridLayoutManager(context, 2));
    }
    public static void setWeatherCard(final Activity activity, final Context context){

        WeatherNowResponse weatherNowResponse = WeatherInfoList.getWeatherNowResponse();
        TextView tv_now_date = activity.findViewById(R.id.tv_now_date);
        TextView tv_weather_status = activity.findViewById(R.id.tv_weather_status);
        TextView tv_weather_temp = activity.findViewById(R.id.tv_weather_temp);
        TextView tv_weather_update_time = activity.findViewById(R.id.tv_weather_update_time);
        TextView tv_weather_humidity_bottom = activity.findViewById(R.id.tv_weather_humidity_bottom);
        TextView tv_weather_wind_level_bottom = activity.findViewById(R.id.tv_weather_wind_level_bottom);
        TextView tv_weather_pressure_bottom = activity.findViewById(R.id.tv_weather_pressure_bottom);
        TextView tv_weather_temp_bottom = activity.findViewById(R.id.tv_weather_temp_bottom);
        ImageView iv_weather_status = activity.findViewById(R.id.iv_weather_status);
        NowWeather now = weatherNowResponse.now;

        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd号 MMMM yyyy", Locale.CHINA);
            tv_now_date.setText(simpleDateFormat.format(date));
            tv_weather_status.setText(now.text != null ? now.text : "--");
            tv_weather_update_time.setText(weatherNowResponse.updateTime != null ? weatherNowResponse.updateTime : "--");
            tv_weather_temp.setText(now.temp != null ? now.temp + "°" : "--");
            tv_weather_temp_bottom.setText(now.temp != null ? now.temp + "℃" : "--");
            tv_weather_humidity_bottom.setText(now.humidity != null ? now.humidity + "%" : "--");
            tv_weather_wind_level_bottom.setText(now.windSpeed != null ? now.windSpeed + "m/s" : "--");
            tv_weather_pressure_bottom.setText(now.pressure != null ?  now.pressure + "hpa": "--");
            String filePath = "weather/" + now.icon + ".svg";
            SVG svg = SVG.getFromAsset(context.getAssets(), filePath);
            if (svg != null) {
                PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                drawable.setColorFilter(porterDuffColorFilter);
                iv_weather_status.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 避免硬件加速渲染问题
                iv_weather_status.setImageDrawable(drawable);
            }
        }catch (Exception e){
            Log.w("WEATHER_ICON_ERROR", e.toString());
        }
    }
    public static void setWeatherCardAnimation(final Activity activity, final Context context){
        MaterialCardView cd_weather_title_card = activity.findViewById(R.id.cd_weather_title_card);
        MaterialCardView cd_weather_info_card = activity.findViewById(R.id.cd_weather_info_card);

        LinearLayout ll_weather_info_title_layout = activity.findViewById(R.id.ll_weather_info_title_layout);

        View[] viewList = {
                cd_weather_title_card,
                cd_weather_info_card,
                ll_weather_info_title_layout,
        };
        int count = 0;
        for(View tempView : viewList){
            AnimationHandler.setItemAnimation(context, tempView, count++);
        }
    }

    /**
     * 设置生活指数横向滑动列表
     */
    public static void setLifeSuggestionAdapter(final Activity activity, final Context context, RecyclerView rvLifeSuggestion) {
        com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.Suggestion suggestion =
                WeatherInfoList.getLifeSuggestion();

        ArrayList<LifeItem> items = new ArrayList<>();

        if (suggestion != null) {
            if (suggestion.dressing != null)
                items.add(new LifeItem("穿衣", suggestion.dressing.brief, suggestion.dressing.details, "checkroom"));
            if (suggestion.comfort != null)
                items.add(new LifeItem("舒适度", suggestion.comfort.brief, suggestion.comfort.details, "sentiment_satisfied"));
            if (suggestion.uv != null)
                items.add(new LifeItem("紫外线", suggestion.uv.brief, suggestion.uv.details, "wb_sunny"));
            if (suggestion.car_washing != null)
                items.add(new LifeItem("洗车", suggestion.car_washing.brief, suggestion.car_washing.details, "local_car_wash"));
            if (suggestion.sport != null)
                items.add(new LifeItem("运动", suggestion.sport.brief, suggestion.sport.details, "fitness_center"));
            if (suggestion.flu != null)
                items.add(new LifeItem("感冒", suggestion.flu.brief, suggestion.flu.details, "masks"));
            if (suggestion.umbrella != null)
                items.add(new LifeItem("雨伞", suggestion.umbrella.brief, suggestion.umbrella.details, "umbrella"));
            if (suggestion.air_pollution != null)
                items.add(new LifeItem("空气", suggestion.air_pollution.brief, suggestion.air_pollution.details, "air"));
            if (suggestion.allergy != null)
                items.add(new LifeItem("过敏", suggestion.allergy.brief, suggestion.allergy.details, "healing"));
            if (suggestion.sunscreen != null)
                items.add(new LifeItem("防晒", suggestion.sunscreen.brief, suggestion.sunscreen.details, "brightness_high"));
            if (suggestion.morning_sport != null)
                items.add(new LifeItem("晨练", suggestion.morning_sport.brief, suggestion.morning_sport.details, "directions_run"));
            if (suggestion.mood != null)
                items.add(new LifeItem("心情", suggestion.mood.brief, suggestion.mood.details, "mood"));
            if (suggestion.makeup != null)
                items.add(new LifeItem("化妆", suggestion.makeup.brief, suggestion.makeup.details, "face_retouching_natural"));
            if (suggestion.traffic != null)
                items.add(new LifeItem("交通", suggestion.traffic.brief, suggestion.traffic.details, "commute"));
            if (suggestion.fishing != null)
                items.add(new LifeItem("钓鱼", suggestion.fishing.brief, suggestion.fishing.details, "phishing"));
            if (suggestion.beer != null)
                items.add(new LifeItem("啤酒", suggestion.beer.brief, suggestion.beer.details, "local_bar"));
            if (suggestion.shopping != null)
                items.add(new LifeItem("逛街", suggestion.shopping.brief, suggestion.shopping.details, "shopping_bag"));
            if (suggestion.airing != null)
                items.add(new LifeItem("晾晒", suggestion.airing.brief, suggestion.airing.details, "dry_cleaning"));
            if (suggestion.kiteflying != null)
                items.add(new LifeItem("放风筝", suggestion.kiteflying.brief, suggestion.kiteflying.details, "toys"));
            if (suggestion.road_condition != null)
                items.add(new LifeItem("路况", suggestion.road_condition.brief, suggestion.road_condition.details, "road"));
            if (suggestion.boating != null)
                items.add(new LifeItem("划船", suggestion.boating.brief, suggestion.boating.details, "sailing"));
            if (suggestion.ac != null)
                items.add(new LifeItem("空调", suggestion.ac.brief, suggestion.ac.details, "ac_unit"));
        }

        LifeSuggestionAdapter adapter = new LifeSuggestionAdapter(context, items);
        rvLifeSuggestion.setLayoutManager(
                new GridLayoutManager(context, 2));
        rvLifeSuggestion.setAdapter(adapter);
    }
}