package com.example.environmentview.MqttTools;

import android.util.Log;

import com.example.environmentview.MqttTools.MqttMessageCallback.MqttConnectStateInterface;
import com.example.environmentview.MqttTools.MqttMessageCallback.MqttMessageCallbackInterface;
import com.example.environmentview.MqttTools.MqttMessageCallback.MqttMessageHandler;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClientManager {
    private final String LOG_TAG = "MQTT_CLIENT_MANAGER";
    private static MqttClientManager instance;
    public static synchronized MqttClientManager getInstance() {
        if (instance == null) {
            instance = new MqttClientManager();
        }
        return instance;
    }
    private final MqttConnectOptions options = new MqttConnectOptions();;
    private final MqttMessageHandler messageHandler = new MqttMessageHandler();
    private MqttConnectStateInterface connectStateInterface;
    private MqttClient client;
    private MqttInfo mqttInfo;

    public void setConnectStateInterface(MqttConnectStateInterface connectStateInterface) {

        this.messageHandler.setConnectStateInterface(connectStateInterface);
    }

    public void mqttInit(MqttInfo info, MqttMessageCallbackInterface callback) throws MqttException {
        this.mqttInfo = info;
        options.setCleanSession(false);           // false 保持会话
        options.setAutomaticReconnect(true);      // 自动重连
        options.setKeepAliveInterval(60);         // 心跳
        options.setConnectionTimeout(10);

        if (info.getClientID() == null || info.getClientID().isEmpty()) {
            info.setClientID("android_" + System.currentTimeMillis());
        }

        client = new MqttClient(
                info.getBroker(),
                info.getClientID(),
                new MemoryPersistence()
        );

        messageHandler.setMessageCallbackInterface(callback);

    }

    public void mqttConnect(MqttInfo info) throws MqttException {
        if (client == null || mqttIsConnected()) return;
        options.setUserName(mqttInfo.getUsername());
        options.setPassword(mqttInfo.getPassword().toCharArray());
        new Thread(()->{
            try {
                client.setCallback(this.getMessageHandler());
                client.connect(options);
                client.subscribe(info.getSubscribe());
                Log.w(LOG_TAG, "连接成功");
                this.mqttPublish(info.getPublish(), "Android_Dev_Connect");
            } catch (MqttException e) {
                Log.e(LOG_TAG, "连接失败", e);
                messageHandler.notifyConnectFailed(e);
            }
        }).start();
    }
    public void mqttDisconnect() throws MqttException {
        client.disconnect();
    }
    public boolean mqttIsConnected(){
        try {
            return client != null && client.isConnected();
        }catch (Exception e){
            Log.w(LOG_TAG, e);
            return false;
        }
    }
    public void mqttPublish(String topic, String message)  {
        if (!mqttIsConnected()) return;
        new Thread(() -> {
            try {
                client.publish(topic, message.getBytes(), 1, false);
            } catch (MqttException e) {
                Log.e(LOG_TAG, "MQTT 发布失败", e);
            }
        }).start();


    }
    public MqttClient getClient() {
        return client;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }
    public MqttMessageHandler getMessageHandler() {
        return messageHandler;
    }
}
