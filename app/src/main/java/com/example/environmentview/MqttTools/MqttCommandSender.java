package com.example.environmentview.MqttTools;

import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.Info.MqttInfoList;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttCommandSender {


    /**
     * 查询单个设备在线状态
     */
    public static void queryDeviceOnline(String deviceId) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", deviceId);
        message.put("cmd", new int[]{2});

        String json = new Gson().toJson(message);
        String publishTopic = MqttInfoList.getMqttInfo().getPublish();
        MqttClientManager.getInstance().mqttPublish(publishTopic, json);
    }

    /**
     * 查询所有已添加设备的在线状态
     */
    public static void queryAllDevicesOnline() {
        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();
        for (DeviceManager.DeviceEntry device : deviceList) {
            queryDeviceOnline(device.id);
        }
    }

    /**
     * 发送控制命令（通用）
     */
    public static void sendCommand(String deviceId, int cmd, Map<String, Object> body) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", deviceId);
        message.put("cmd", new int[]{cmd});
        message.put("body", body != null ? body : new HashMap<>());

        String json = new Gson().toJson(message);
        String publishTopic = MqttInfoList.getMqttInfo().getPublish();

        MqttClientManager.getInstance().mqttPublish(publishTopic, json);
    }
}