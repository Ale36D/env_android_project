package com.example.environmentview.Event.Process;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataKeys;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttMessage;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.Info.MqttInfoList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class MqttMessageProcess {

    private static String TAG = "MQTT_MESSAGE_PROCESS";

    public interface MqttMessageProcessInterface{
        void messageProcess(String topic, String message);
    }
    private static MqttMessageProcessInterface mqttMessageProcessInterface;

    public MqttMessageProcessInterface getMqttMessageProcessInterface() {
        return mqttMessageProcessInterface;
    }

    public void setMqttMessageProcessInterface(MqttMessageProcessInterface mqttMessageProcessInterface) {
        MqttMessageProcess.mqttMessageProcessInterface = mqttMessageProcessInterface;
    }


    public static void messageProcess(String topic, String message) {
        mqttRawDataProcess(topic, message);
        if(mqttMessageProcessInterface != null)
            mqttMessageProcessInterface.messageProcess(topic, message);
    }
    // 处理接收到的数据,并且根据添加的标识进行更新数据,不在的则不显示
    public static void mqttRawDataProcess(String topic, String message) {
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            // 获取 cmd
            int cmd = 0;
            JsonElement cmdElement = jsonObject.get("cmd");
            if (cmdElement != null) {
                if (cmdElement.isJsonArray()) {
                    JsonArray cmdArray = cmdElement.getAsJsonArray();
                    if (cmdArray.size() > 0) {
                        cmd = cmdArray.get(0).getAsInt();
                    }
                } else if (cmdElement.isJsonPrimitive()) {
                    cmd = cmdElement.getAsInt();
                }
            }

            String deviceId = jsonObject.has("id") && !jsonObject.get("id").isJsonNull()
                    ? jsonObject.get("id").getAsString()
                    : "unknown";

            Log.d(TAG, "收到消息 - 设备: " + deviceId + ", cmd: " + cmd);

            // 只处理已添加的设备
            if (!DeviceManager.isAllowed(deviceId)) {
                Log.d(TAG, "设备未添加，忽略: " + deviceId);
                return;
            }

            // ★ 收到任何消息都刷新设备在线状态 ★
            MqttDeviceStatusManager.onDeviceResponse(deviceId);

            // ★ 处理不同的 cmd ★
            switch (cmd) {
                case 999:
                    // 环境数据
                    processEnvironmentData(deviceId, jsonObject);
                    break;
                default:
                    Log.d(TAG, "其他cmd: " + cmd + "，已刷新设备 " + deviceId + " 在线状态");
                    break;
            }

        } catch (Exception e) {
            Log.e(TAG, "消息解析失败: " + e.getMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    private static void processEnvironmentData(String deviceId, JsonObject jsonObject) {
        try {
            Gson gson = new Gson();
            MqttMessage msg = gson.fromJson(jsonObject, MqttMessage.class);

            // 解析数据
            Map<String, String> relayMap = new HashMap<>();
            if (msg.body.relay != null) {
                relayMap.put(MqttDataKeys.RELAY_1, String.valueOf(msg.body.relay.r_1));
                relayMap.put(MqttDataKeys.RELAY_2, String.valueOf(msg.body.relay.r_2));
                relayMap.put(MqttDataKeys.RELAY_3, String.valueOf(msg.body.relay.r_3));
                relayMap.put(MqttDataKeys.RELAY_4, String.valueOf(msg.body.relay.r_4));
                relayMap.put(MqttDataKeys.BUZZER, String.valueOf(msg.body.relay.bz));
            }

            Map<String, String> sensorMap = new HashMap<>();
            if (msg.body.sensor != null) {
                sensorMap.put(MqttDataKeys.TP, String.valueOf(msg.body.sensor.tp));
                sensorMap.put(MqttDataKeys.HM, String.valueOf(msg.body.sensor.hm));
                sensorMap.put(MqttDataKeys.L, String.valueOf(msg.body.sensor.l));
                sensorMap.put(MqttDataKeys.VT, String.valueOf(msg.body.sensor.vt));
                sensorMap.put(MqttDataKeys.DP, String.valueOf(msg.body.sensor.dp));
                sensorMap.put(MqttDataKeys.PR, String.format("%.2f", msg.body.sensor.pr / 100.0));
                sensorMap.put(MqttDataKeys.AL, String.valueOf(msg.body.sensor.al));
                sensorMap.put(MqttDataKeys.MADC, String.valueOf(msg.body.sensor.madc));
                sensorMap.put(MqttDataKeys.MDI, String.valueOf(msg.body.sensor.mdi));
                sensorMap.put(MqttDataKeys.FD, String.valueOf(msg.body.sensor.fd));
                sensorMap.put(MqttDataKeys.AX, String.valueOf(msg.body.sensor.aX));
                sensorMap.put(MqttDataKeys.AY, String.valueOf(msg.body.sensor.aY));
                sensorMap.put(MqttDataKeys.AZ, String.valueOf(msg.body.sensor.aZ));
                sensorMap.put(MqttDataKeys.RL, String.valueOf(msg.body.sensor.rl));
                sensorMap.put(MqttDataKeys.PC, String.valueOf(msg.body.sensor.pc));
            }

            Map<String, String> rgbMap = new HashMap<>();
            if (msg.body.rgb != null) {
                rgbMap.put(MqttDataKeys.RGB_R, String.valueOf(msg.body.rgb.r));
                rgbMap.put(MqttDataKeys.RGB_G, String.valueOf(msg.body.rgb.g));
                rgbMap.put(MqttDataKeys.RGB_B, String.valueOf(msg.body.rgb.b));
            } else {
                rgbMap.put(MqttDataKeys.RGB_R, "0");
                rgbMap.put(MqttDataKeys.RGB_G, "0");
                rgbMap.put(MqttDataKeys.RGB_B, "0");
            }

            MqttDataMap dataMap = new MqttDataMap(relayMap, sensorMap, rgbMap);
            MqttInfoList.setMqttDataMap(dataMap);
            MqttRepository.postData(deviceId, dataMap);

        } catch (Exception e) {
            Log.e(TAG, "处理环境数据失败: " + e.getMessage());
        }
    }
}