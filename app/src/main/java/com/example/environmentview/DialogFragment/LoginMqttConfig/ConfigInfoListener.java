package com.example.environmentview.DialogFragment.LoginMqttConfig;

public interface ConfigInfoListener {
    void setInfo(String broker, String clientID ,String subscribe, String publish);
}
