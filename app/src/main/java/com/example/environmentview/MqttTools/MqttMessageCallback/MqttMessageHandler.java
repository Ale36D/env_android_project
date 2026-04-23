package com.example.environmentview.MqttTools.MqttMessageCallback;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageHandler implements MqttCallbackExtended {
    private static String TAG = "MQTT_MESSAGE_HANDLER";
    private String message;

    private String topic;

    private MqttMessageCallbackInterface messageCallbackInterface;
    private MqttConnectStateInterface connectStateInterface;

    public void setConnectStateInterface(MqttConnectStateInterface connectStateInterface) {
        this.connectStateInterface = connectStateInterface;
    }

    public void setMessageCallbackInterface(MqttMessageCallbackInterface messageCallbackInterface){
        this.messageCallbackInterface = messageCallbackInterface;
    }
    public void notifyConnectFailed(MqttException e){
        if (connectStateInterface == null) return;

        String reason;
        switch (e.getReasonCode()) {
            case MqttException.REASON_CODE_FAILED_AUTHENTICATION:
                reason = "用户名或密码错误";
                break;
            case MqttException.REASON_CODE_BROKER_UNAVAILABLE:
                reason = "Broker 不可用";
                break;
            default:
                reason = e.getMessage();
        }
        connectStateInterface.onConnectFailed(reason);
    }


    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.w(TAG, "连接成功");
        Log.w(TAG, String.valueOf(reconnect));
        Log.w(TAG, serverURI);
        if(this.connectStateInterface != null) {
            connectStateInterface.onConnectSuccess();
            Log.w(TAG, "调用onConnectSuccess接口方法");
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.w(TAG, "连接断开");
        Log.w(TAG, cause);
        if(this.connectStateInterface != null)
            connectStateInterface.onConnectLost();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String mess = new String(message.getPayload());
        this.setTopic(topic);
        this.setMessage(mess);
        this.messageCallbackInterface.getMessage(topic, mess);
        Log.w("MQTT_MESSAGE", topic +"||" +message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MqttMessageCallbackInterface getMessageCallbackInterface() {
        return messageCallbackInterface;
    }
}
