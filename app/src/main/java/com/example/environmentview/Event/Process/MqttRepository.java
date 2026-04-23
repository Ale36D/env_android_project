package com.example.environmentview.Event.Process;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Info.DeviceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MqttRepository {

    private static final String TAG = "MQTT_REPO";

    // 所有设备数据：deviceId -> MqttDataMap
    private static final Map<String, MqttDataMap> deviceDataMap = new LinkedHashMap<>();

    // 当前选中的设备 ID
    private static String currentDeviceId = null;

    // 当前设备的数据（UI 观察这个）
    private static final MutableLiveData<MqttDataMap> currentDeviceData = new MutableLiveData<>();

    // 设备列表变化通知（新设备上线时触发）
    private static final MutableLiveData<List<String>> deviceListData = new MutableLiveData<>();

    private static final MutableLiveData<Map<String, MqttDataMap>> allDeviceData = new MutableLiveData<>();

    private static boolean loaded = false;

    /**
     * 从 SharedPreferences 恢复上次缓存的设备数据
     * 应在 MainActivity 启动时调用一次
     */
    public static void loadCache() {
        if (loaded) return;
        loaded = true;
        try {
            String json = AppConfig.getDeviceDataCache();
            Type type = new TypeToken<Map<String, MqttDataMap>>() {}.getType();
            Map<String, MqttDataMap> cached = new Gson().fromJson(json, type);
            if (cached != null && !cached.isEmpty()) {
                // 只恢复仍在设备列表中的设备数据
                for (Map.Entry<String, MqttDataMap> entry : cached.entrySet()) {
                    if (DeviceManager.isAllowed(entry.getKey())) {
                        deviceDataMap.put(entry.getKey(), entry.getValue());
                    }
                }
                // 自动选中第一个有数据的设备
                if (currentDeviceId == null && !deviceDataMap.isEmpty()) {
                    List<DeviceManager.DeviceEntry> list = DeviceManager.getDeviceList();
                    for (DeviceManager.DeviceEntry d : list) {
                        if (deviceDataMap.containsKey(d.id)) {
                            currentDeviceId = d.id;
                            break;
                        }
                    }
                }
                // 通知 UI
                if (currentDeviceId != null && deviceDataMap.containsKey(currentDeviceId)) {
                    currentDeviceData.postValue(deviceDataMap.get(currentDeviceId));
                }
                allDeviceData.postValue(new LinkedHashMap<>(deviceDataMap));
                deviceListData.postValue(new ArrayList<>(deviceDataMap.keySet()));
                Log.w(TAG, "已恢复 " + deviceDataMap.size() + " 个设备的缓存数据");
            }
        } catch (Exception e) {
            Log.e(TAG, "恢复缓存失败: " + e.getMessage());
        }
    }

    /**
     * 将当前所有设备数据持久化到 SharedPreferences
     */
    private static void saveCache() {
        try {
            String json = new Gson().toJson(deviceDataMap);
            AppConfig.setDeviceDataCache(json);
        } catch (Exception e) {
            Log.e(TAG, "保存缓存失败: " + e.getMessage());
        }
    }

    /**
     * MQTT 线程调用：按设备 ID 存储数据
     */
    public static void postData(String deviceId, MqttDataMap data) {

        if (!DeviceManager.isAllowed(deviceId)) {
            return;
        }

        // ★ 检测是否是新设备（之前没有数据） ★
        boolean isNewDevice = !deviceDataMap.containsKey(deviceId);

        deviceDataMap.put(deviceId, data);

        // 如果还没选过设备，自动选第一个
        if (currentDeviceId == null || !DeviceManager.isAllowed(currentDeviceId)) {
            List<DeviceManager.DeviceEntry> list = DeviceManager.getDeviceList();
            if (!list.isEmpty()) {
                currentDeviceId = list.get(0).id;
            }
        }

        // 如果是当前选中设备的数据，通知 UI 更新
        if (deviceId.equals(currentDeviceId)) {
            currentDeviceData.postValue(data);
        }
        allDeviceData.postValue(new LinkedHashMap<>(deviceDataMap));

        // ★ 新设备上线，通知设备列表刷新（触发 SensorDataFragment 刷新 Tab） ★
        if (isNewDevice) {
            List<String> ids = new ArrayList<>();
            for (DeviceManager.DeviceEntry e : DeviceManager.getDeviceList()) {
                ids.add(e.id);
            }
            deviceListData.postValue(ids);
            Log.w(TAG, "新设备数据到达: " + deviceId + "，已通知UI刷新");
        }

        // ★ 每次收到新数据都持久化 ★
        saveCache();
    }

    /**
     * 切换当前设备
     */
    public static void selectDevice(String deviceId) {
        currentDeviceId = deviceId;
        MqttDataMap data = deviceDataMap.get(deviceId);

        // ★ 关键：没有数据时发送空的 MqttDataMap，而不是 null ★
        if (data != null) {
            currentDeviceData.postValue(data);
        } else {
            // 发送空数据，触发 UI 清空
            currentDeviceData.postValue(MqttDataMap.empty());
        }
    }
    public static LiveData<Map<String, MqttDataMap>> getAllDeviceData() {
        return allDeviceData;
    }

    public static LiveData<MqttDataMap> getCurrentDeviceData() {
        return currentDeviceData;
    }

    public static LiveData<List<String>> getDeviceListData() {
        return deviceListData;
    }

    public static String getCurrentDeviceId() {
        return currentDeviceId;
    }

    public static List<String> getDeviceIds() {
        return new ArrayList<>(deviceDataMap.keySet());
    }

    // 兼容旧代码：获取当前设备数据
    public static LiveData<MqttDataMap> getMqttData() {
        return currentDeviceData;
    }

    public static void notifyDeviceListChanged() {
        deviceListData.postValue(new ArrayList<>(deviceDataMap.keySet()));

        // 获取当前设备ID列表
        List<String> ids = new ArrayList<>();
        for (DeviceManager.DeviceEntry e : DeviceManager.getDeviceList()) {
            ids.add(e.id);
        }

        deviceListData.postValue(ids);
    }

    public static void removeDevice(String deviceId) {
        deviceDataMap.remove(deviceId);
        if (deviceId.equals(currentDeviceId)) {
            if (!deviceDataMap.isEmpty()) {
                currentDeviceId = deviceDataMap.keySet().iterator().next();
                currentDeviceData.postValue(deviceDataMap.get(currentDeviceId));
            } else {
                currentDeviceId = null;
                currentDeviceData.postValue(null);
            }
        }
        deviceListData.postValue(new ArrayList<>(deviceDataMap.keySet()));

        // ★ 删除设备后也更新缓存 ★
        saveCache();
    }
    // 获取指定设备的数据（不触发选中）
    public static MqttDataMap getDeviceData(String deviceId) {
        return deviceDataMap.get(deviceId);
    }
    // 获取所有在线设备的数据
    public static Map<String, MqttDataMap> getAllOnlineDeviceData() {
        Map<String, MqttDataMap> result = new LinkedHashMap<>();
        for (Map.Entry<String, MqttDataMap> entry : deviceDataMap.entrySet()) {
            if (MqttDeviceStatusManager.isDeviceOnline(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}