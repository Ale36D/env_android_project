package com.example.environmentview.Event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.caverock.androidsvg.SVG;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.AppConfig.AppConfig;

import com.example.environmentview.DialogFragment.MainSignOut.MainSignOutDialog;
import com.example.environmentview.DialogFragment.ManageDevice.ManageDeviceDialog;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.FragmentPage.HomeFragment;
import com.example.environmentview.FragmentPage.MessageCenterFragment;
import com.example.environmentview.FragmentPage.SensorDataFragment;
import com.example.environmentview.FragmentPage.UserInfoFragment;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.Info.WeatherInfoList;
import com.example.environmentview.LoginActivity;
import com.example.environmentview.MainActivity;
import com.example.environmentview.NetWorkUtil.GetNetWorkData;
import com.example.environmentview.NetWorkUtil.GetOneKitInterface;
import com.example.environmentview.NetWorkUtil.GetWeatherInterface;
import com.example.environmentview.NetWorkUtil.GetWeatherLifeInterface;
import com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife.Suggestion;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeather;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.WeatherNowResponse;
import com.example.environmentview.NetWorkUtil.NetWorkUtil;
import com.example.environmentview.R;
import com.example.environmentview.SensorDataActivity;
import com.example.environmentview.WeatherInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainEventHandler {


    static View homeView;
    static View allSensorView;
    static View messageCenterView;
    static View userView;


    private static Fragment homeFragment;
    private static Fragment sensorDataFragment;
    private static Fragment messageFragment;
    private static Fragment userInfoFragment;
    private static Fragment currentFragment;

    public static void initFragmentView(Bundle bundle, AppCompatActivity activity){
        FragmentManager fm = activity.getSupportFragmentManager();
        if (bundle == null) {
            homeFragment = new HomeFragment();
            sensorDataFragment = new SensorDataFragment();
            messageFragment = new MessageCenterFragment();
            userInfoFragment = new UserInfoFragment();
            fm.beginTransaction()
                    .add(R.id.fm_container, homeFragment, "HOME")
                    .add(R.id.fm_container, sensorDataFragment, "SENSOR").hide(sensorDataFragment)
                    .add(R.id.fm_container, messageFragment, "MESSAGE").hide(messageFragment)
                    .add(R.id.fm_container, userInfoFragment, "SETTING").hide(userInfoFragment)
                    .commit();

            currentFragment = homeFragment;
        }else {
            homeFragment = fm.findFragmentByTag("HOME");
            sensorDataFragment = fm.findFragmentByTag("ALL_SENSOR");
            messageFragment = fm.findFragmentByTag("MESSAGE");
            userInfoFragment = fm.findFragmentByTag("USERINFO");
            currentFragment = homeFragment; // 默认
        }
    }
    private static void switchFragment(Fragment target, AppCompatActivity activity, Context context,int position) {
        if (currentFragment == target) return;

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.hide(currentFragment);
        ft.show(target);
        ft.commit();
        if (position == R.id.nav_home)
            setHomeAnimation(activity, context, getHomeView());
        if (position == R.id.nav_search)
            setAllSensorAnimationView(activity, context, getAllSensorView());
        if (position == R.id.nav_message)
            setMessageCenterAnimationView(activity, context, getMessageCenterView());
        if (position == R.id.nav_profile)
            setUserAnimationView(activity, context, getUserView());
        currentFragment = target;

    }

    public static BottomNavigationView.OnItemSelectedListener createBottomNavListener(
            final AppCompatActivity activity,
            Context context) {

        return item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
                switchFragment(homeFragment, activity, context, id);
            if (id == R.id.nav_search)
                switchFragment(sensorDataFragment, activity, context, id);
            if (id == R.id.nav_message)
                switchFragment(messageFragment, activity, context, id);
            if (id == R.id.nav_profile)
                switchFragment(userInfoFragment, activity, context, id);
            return true;
        };
    }
    public static View.OnClickListener relayClickHandler(final Activity activity){
        return view -> {
            Intent intent = new Intent(activity, SensorDataActivity.class);
            intent.putExtra("type", "actuator");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        };
    }
    public static View.OnClickListener SensorClickHandler(final Activity activity){
        return view -> {
            Intent intent = new Intent(activity, SensorDataActivity.class);
            intent.putExtra("type", "sensor");
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        };
    }

    public static View.OnClickListener bottomBarClickHandler(final FragmentManager fragmentManager, final Activity activity) {
        return view -> {

            MainSignOutDialog mainSignOutDialog = new MainSignOutDialog();
            mainSignOutDialog.setOnLogoutListener(()->{
                // 停止发送设备在线命令
                MqttDeviceStatusManager.stopPolling();
                AppConfig.setLogin(false);
                AppConfig.setAutoLogin(true);
                LoginEventHandler.LOGIN_SUCCESS_TAG = false;
                try {
                    MqttInfoList.getMyMqttManager().mqttDisconnect();
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            });
            mainSignOutDialog.show(fragmentManager, "MainAppBottomSheetDialog");
        };
    }
    public static View.OnClickListener openWeatherActivity(final FragmentManager fragmentManager, final Activity activity) {
        return view -> {
            activity.startActivity(new Intent(activity, WeatherInfo.class));
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        };
    }
    public static void setOneKitToView(final Activity activity, final Context context, TextView textView){
        if(NetWorkUtil.isNetworkAvailable(activity)) {
            GetNetWorkData.fetchOneSentence(new GetOneKitInterface() {
                @Override
                public void getOneKitResult(String result) {
                    activity.runOnUiThread(()->{
                        textView.setText(result);
                    });
                    Log.w("ONE_KIT", result);
                }
                @SuppressLint("SetTextI18n")
                @Override
                public void getOneKitError(String error) {
                    activity.runOnUiThread(()->{
                        textView.setText("error");
                    });
                    Log.w("ONE_KIT", error);
                }
            });
        }
    }
    public static void setWeatherToView(final Activity activity, final Context context, View view){
        if (activity == null || view == null) return;
        if(NetWorkUtil.isNetworkAvailable(activity)) {
            TextView tv_weather_status = view.findViewById(R.id.tv_weather_status);
            TextView tv_weather_temp = view.findViewById(R.id.tv_weather_temp);
            TextView tv_weather_update_time = view.findViewById(R.id.tv_weather_update_time);
            TextView tv_weather_humidity_bottom = view.findViewById(R.id.tv_weather_humidity_bottom);
            TextView tv_weather_wind_level_bottom = view.findViewById(R.id.tv_weather_wind_level_bottom);
            TextView tv_weather_pressure_bottom = view.findViewById(R.id.tv_weather_pressure_bottom);
            TextView tv_weather_temp_bottom = view.findViewById(R.id.tv_weather_temp_bottom);
            ImageView iv_weather_status = view.findViewById(R.id.iv_weather_status);

            GetNetWorkData.getWeather(new GetWeatherInterface() {
                @Override
                public void getWeatherResult(WeatherNowResponse result) {
                    activity.runOnUiThread(() -> {
                        if(result != null && result.now != null){
                            NowWeather now = result.now;
                            tv_weather_status.setText(now.text != null ? now.text : "--");
                            tv_weather_update_time.setText(result.updateTime != null ? result.updateTime : "--");
                            tv_weather_temp.setText(now.temp != null ? now.temp + "°" : "--");
                            tv_weather_temp_bottom.setText(now.temp != null ? now.temp + "℃" : "--");
                            tv_weather_humidity_bottom.setText(now.humidity != null ? now.humidity + "%" : "--");
                            tv_weather_wind_level_bottom.setText(now.windSpeed != null ? now.windSpeed + "m/s" : "--");
                            tv_weather_pressure_bottom.setText(now.pressure != null ?  now.pressure + "hpa": "--");
                            String filePath = "weather/" + now.icon + ".svg";
                            try {
                                SVG svg = SVG.getFromAsset(context.getAssets(), filePath);
                                if (svg != null) {
                                    PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                                    PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                    drawable.setColorFilter(porterDuffColorFilter);
                                    iv_weather_status.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 避免硬件加速渲染问题
                                    iv_weather_status.setImageDrawable(drawable);
                                }

                            } catch (Exception e){
                                Log.w("WEATHER_ICON_ERROR", e.toString());
                            }
                        }
                    });
                }

                @Override
                public void getWeatherError(String error) {
                    activity.runOnUiThread(()->{
                        tv_weather_status.setText("error");
                    });
                }
            });
            GetNetWorkData.getLifeInfoOrRefreshView(new GetWeatherLifeInterface() {
                @Override
                public void getWeatherLifeInfo(Suggestion suggestion) {
                    WeatherInfoList.setLifeSuggestion(suggestion);
                }

                @Override
                public void getWeatherLifeInfoError() {
                    Log.w("LIFE_INFO", "获取生活指数失败");
                }
            });
        }

    }

    public static void setUserNameToView(FragmentActivity fragmentActivity, Context context, TextView tvUsername) {
        tvUsername.setText("Hey! " + MqttInfoList.getMqttInfo().getUsername());
    }

    public static void setNowDateToView(FragmentActivity fragmentActivity, Context context, TextView tvNowDate) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd号 MMMM yyyy", Locale.CHINA);
        tvNowDate.setText(simpleDateFormat.format(date));
    }

    @SuppressLint("SetTextI18n")
    public static void setDevStatusView(FragmentActivity fragmentActivity, Context context, View view, MqttDataMap dataMap) {
        TextView tv_dev_actuator_num, tv_dev_sensor_num;

        tv_dev_actuator_num = view.findViewById(R.id.tv_dev_actuator_num);
        tv_dev_sensor_num = view.findViewById(R.id.tv_dev_sensor_num);

        tv_dev_actuator_num.setText(dataMap.getRelayMap().size() + " Devices");
        tv_dev_sensor_num.setText(dataMap.getSensorMap().size()+ " Devices");
    }
    public static void setHomeAnimation(FragmentActivity fragmentActivity, Context context, View view) {
        MaterialCardView cb_main_title_card = view.findViewById(R.id.cb_main_title_card);
        MaterialCardView cb_main_weather_card = view.findViewById(R.id.cb_main_weather_card);
        MaterialCardView cb_main_actuator_card = view.findViewById(R.id.cb_main_actuator_card);
        MaterialCardView cb_main_sensor_card = view.findViewById(R.id.cb_main_sensor_card);
        LinearLayout ll_home_dev_layout = view.findViewById(R.id.ll_home_dev_layout);
        RecyclerView rv_home_device_list = view.findViewById(R.id.rv_home_device_list);

        LinearLayout ll_home_title_layout = view.findViewById(R.id.ll_home_title_layout);

        View[] viewList = {
                cb_main_title_card,
                cb_main_weather_card,
                cb_main_actuator_card,
                cb_main_sensor_card,
                ll_home_dev_layout,
                rv_home_device_list,
                ll_home_title_layout
        };
        int count = 0;
        for (View tempView : viewList){
            AnimationHandler.setItemAnimation(context, tempView, count++);
        }
    }
    public static void setAllSensorAnimationView(FragmentActivity fragmentActivity, Context context, View view) {
        MaterialCardView cd_all_sensor_title_card = view.findViewById(R.id.cd_all_sensor_title_card);
        TabLayout tabDevices = view.findViewById(R.id.tab_devices);
        ViewPager2 viewPager2 = view.findViewById(R.id.vp_device_pager);

        LinearLayout ll_all_sensor_title_layout = view.findViewById(R.id.ll_all_sensor_title_layout);

        View[] viewList = {cd_all_sensor_title_card, tabDevices, viewPager2, ll_all_sensor_title_layout};
        int count = 0;
        for (View tempView : viewList){
            AnimationHandler.setItemAnimation(context, tempView, count++);
        }
    }

    public static void setMessageCenterAnimationView(FragmentActivity fragmentActivity, Context context, View view) {
        MaterialCardView cb_msg_title_card, cb_msg_mes_num_card, cb_msg_dev_online_num_card;

        cb_msg_title_card = view.findViewById(R.id.cb_msg_title_card);
        cb_msg_mes_num_card = view.findViewById(R.id.cb_msg_mes_num_card);
        cb_msg_dev_online_num_card = view.findViewById(R.id.cb_msg_dev_online_num_card);
        LinearLayout ll_msg_clear_layout = view.findViewById(R.id.ll_msg_clear_layout);
        RecyclerView rv_messages = view.findViewById(R.id.rv_messages);

        LinearLayout ll_msg_title_layout = view.findViewById(R.id.ll_msg_title_layout);

        View[] viewList = {
                cb_msg_title_card,
                ll_msg_clear_layout,
                rv_messages,
                cb_msg_title_card,
                cb_msg_mes_num_card,
                cb_msg_dev_online_num_card,
                ll_msg_title_layout
        };
        int count = 0;
        for (View tempView : viewList){
            AnimationHandler.setItemAnimation(context, tempView, count++);
        }
    }
    public static void setUserAnimationView(FragmentActivity fragmentActivity, Context context, View view) {
        MaterialCardView cdTitleCard, cdUserInfoCard, cdStatsCard, cdSettingsCard;
        Button btn_logout;

        cdTitleCard = view.findViewById(R.id.cb_user_title_card);
        cdUserInfoCard = view.findViewById(R.id.cd_user_info_card);
        cdStatsCard = view.findViewById(R.id.cd_stats_card);
        cdSettingsCard = view.findViewById(R.id.cd_settings_card);
        btn_logout = view.findViewById(R.id.btn_logout);

        LinearLayout ll_user_title_layout = view.findViewById(R.id.ll_user_title_layout);

        View[] viewList = {
                cdTitleCard,
                cdUserInfoCard,
                cdStatsCard,
                cdSettingsCard,
                btn_logout,
                ll_user_title_layout
        };
        int count = 0;
        for (View tempView : viewList){
            AnimationHandler.setItemAnimation(context, tempView, count++);
        }
    }


    public static View getHomeView() {
        return homeView;
    }

    public static void setHomeView(View homeView) {
        MainEventHandler.homeView = homeView;
    }

    public static View getAllSensorView() {
        return allSensorView;
    }

    public static void setAllSensorView(View allSensorView) {
        MainEventHandler.allSensorView = allSensorView;
    }

    public static View getMessageCenterView() {
        return messageCenterView;
    }

    public static void setMessageCenterView(View messageCenterView) {
        MainEventHandler.messageCenterView = messageCenterView;
    }

    public static void setUserView(View userView) {
        MainEventHandler.userView = userView;
    }

    private static View getUserView() {
        return userView;
    }


    public static void jumpToSensorFramement() {
//        MqttRepository.selectDevice(device.id);
//        // 跳转到传感器页
//        if (requireActivity() instanceof MainActivity) {
//            ((MainActivity) requireActivity()).bottomNav.setSelectedItemId(R.id.nav_sensor_data);
//        }
    }

    public static void setDeviceCardInfoHandler(FragmentManager fragmentManager, HomeFragment homeFragment) {
        ManageDeviceDialog dialog = new ManageDeviceDialog();
        dialog.setOnDeviceListChangedListener(() -> {
            // 刷新 HomeFragment
            homeFragment.refreshDeviceList();

            // ★ 通知 SensorDataFragment 刷新（通过 LiveData）★
            MqttRepository.notifyDeviceListChanged();
        });
        dialog.show(fragmentManager, "manage_device");
    }
}