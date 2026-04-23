package com.example.environmentview.AppConfig;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {

    private static SharedPreferences sp;

    public static void init(Context context) {
        if (sp == null) {
            sp = context
                    .getApplicationContext()
                    .getSharedPreferences(Keys.PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    // 登录状态
    public static void setLogin(boolean isLogin) {
        sp.edit().putBoolean(Keys.IS_LOGIN, isLogin).apply();
    }

    public static boolean getLogin() {
        return sp.getBoolean(Keys.IS_LOGIN, false);
    }
    // 设置自动登录
    public static void setAutoLogin(boolean isAutoLogin) {
        sp.edit().putBoolean(Keys.AUTO_LOGIN, isAutoLogin).apply();
    }
    public static boolean getAutoLogin() {
        return sp.getBoolean(Keys.AUTO_LOGIN, false);
    }
    // ===== 设置Broker =====
    public static void setBroker(String broker) {
        sp.edit().putString(Keys.BROKER, broker).apply();
    }

    public static String getBroker() {
        return sp.getString(Keys.BROKER, "");
    }
    // ===== 设置ClientId =====
    public static void setClientId(String clientId) {
        sp.edit().putString(Keys.CLIENT_ID, clientId).apply();
    }

    public static String getClientId() {
        return sp.getString(Keys.CLIENT_ID, "");
    }

    // ===== 设置Subscribe Topic =====
    public static void setSubscribe(String subscribe) {
        sp.edit().putString(Keys.SUBSCRIBE, subscribe).apply();
    }

    public static String getSubscribe() {
        return sp.getString(Keys.SUBSCRIBE, "");
    }

    // ===== 设置Publish Topic =====
    public static void setPublish(String publish) {
        sp.edit().putString(Keys.PUBLISH, publish).apply();
    }

    public static String getPublish() {
        return sp.getString(Keys.PUBLISH, "");
    }
    // 设置用户名
    public static void setUsername(String username) {
        sp.edit().putString(Keys.USERNAME, username).apply();
    }

    public static String getUsername() {
        return sp.getString(Keys.USERNAME, "");
    }
    // 设置用户密码
    public static void setPassword(String password) {
        sp.edit().putString(Keys.PASSWORD, password).apply();
    }

    // City
    public static void setCity(String token) {
        sp.edit().putString(Keys.CITY, token).apply();
    }

    public static String getCity() {
        return sp.getString(Keys.CITY, "菏泽");
    }

    public static String getPassword() {
        return sp.getString(Keys.PASSWORD, "");
    }

    // Token
    public static void setToken(String token) {
        sp.edit().putString(Keys.TOKEN, token).apply();
    }

    public static String getToken() {
        return sp.getString(Keys.TOKEN, "");
    }


    // 清空（退出登录）
    public static void clear() {
        sp.edit().clear().apply();
    }

    // ===== 设备列表（JSON 字符串，由 DeviceManager 负责序列化） =====
    public static void setDeviceList(String json) {
        sp.edit().putString(Keys.DEVICE_LIST, json).apply();
    }

    public static String getDeviceList() {
        return sp.getString(Keys.DEVICE_LIST, "[]");
    }

    // ===== 设备数据缓存（JSON 字符串，由 MqttRepository 负责序列化） =====
    public static void setDeviceDataCache(String json) {
        sp.edit().putString(Keys.DEVICE_DATA_CACHE, json).apply();
    }

    public static String getDeviceDataCache() {
        return sp.getString(Keys.DEVICE_DATA_CACHE, "{}");
    }

    // ===== 设备最后一次数据缓存（JSON 字符串，由 MqttRepository 负责序列化） =====
    public static void setDeviceData(String json) {
        sp.edit().putString(Keys.DEVICE_DATA, json).apply();
    }

    public static String getDeviceData() {
        return sp.getString(Keys.DEVICE_DATA, "{}");
    }
}