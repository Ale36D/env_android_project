package com.example.environmentview.MqttTools.MqttMessageCallback;

public interface MqttConnectStateInterface {
    void onConnectSuccess();
    void onConnectFailed(String reason);
    void onConnectLost();
}
