package com.example.environmentview.MqttTools.MqttMessageCallback;

public interface MqttMessageCallbackInterface {
    void getMessage(String topic, String message);
}
