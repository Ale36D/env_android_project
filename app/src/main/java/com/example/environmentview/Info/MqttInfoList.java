package com.example.environmentview.Info;

import android.util.Log;

import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataKeys;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttMessage;
import com.example.environmentview.MqttTools.MqttInfo;
import com.example.environmentview.MqttTools.MqttClientManager;


public class MqttInfoList {
    private static String TAG = "MQTT_INFO_LIST";
    private static MqttInfo mqttInfo;
    private static MqttClientManager mqttClientManager;


    private static MqttMessage mqttMessage;
    private static MqttDataKeys mqttDataKeys;
    private static MqttDataMap mqttDataMap;

    public static void setMyMqttManager() {
        mqttClientManager = new MqttClientManager();
        Log.d(TAG, "初始化MqttClientManager");
    }
    public static MqttClientManager getMyMqttManager() {
        return MqttClientManager.getInstance();
    }
    public static void setMqttInfo(MqttInfo mqttInfo) {
        MqttInfoList.mqttInfo = mqttInfo;
    }

    public static MqttDataMap getMqttDataMap() {
        return mqttDataMap;
    }

    public static void setMqttDataMap(MqttDataMap mqttDataMap) {
        MqttInfoList.mqttDataMap = mqttDataMap;
    }

    public static MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public static void setMqttMessage(MqttMessage mqttMessage) {
        MqttInfoList.mqttMessage = mqttMessage;
    }

    public static MqttDataKeys getMqttDataKeys() {
        return mqttDataKeys;
    }

    public static void setMqttDataKeys(MqttDataKeys mqttDataKeys) {
        MqttInfoList.mqttDataKeys = mqttDataKeys;
    }


    public static void setMqttInfo(String broker, String clientId, String userName,String passWord,String subscribe, String publish){
        mqttInfo = new MqttInfo(broker, clientId, userName, passWord, subscribe, publish);
    }
    public static MqttInfo getMqttInfo(){
        return mqttInfo;
    }

    public static MqttClientManager getMqttClientManager() {
        return mqttClientManager;
    }
}
