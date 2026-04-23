package com.example.environmentview.Info;

import com.example.environmentview.AppConfig.AppConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeviceManager {

    public static class DeviceEntry {
        public String id;
        public String name;

        public DeviceEntry(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static List<DeviceEntry> deviceList = null;

    public static List<DeviceEntry> getDeviceList() {
        if (deviceList == null) {
            String json = AppConfig.getDeviceList();
            Type type = new TypeToken<List<DeviceEntry>>() {}.getType();
            deviceList = new Gson().fromJson(json, type);
            if (deviceList == null) deviceList = new ArrayList<>();
        }
        return deviceList;
    }

    public static boolean addDevice(String id, String name) {
        if (id == null || id.trim().isEmpty()) return false;
        for (DeviceEntry e : getDeviceList()) {
            if (e.id.equals(id.trim())) return false;
        }
        deviceList.add(new DeviceEntry(id.trim(), name.trim().isEmpty() ? id.trim() : name.trim()));
        save();
        return true;
    }

    public static void removeDevice(String id) {
        getDeviceList().removeIf(e -> e.id.equals(id));
        save();
    }

    public static boolean isAllowed(String deviceId) {
        for (DeviceEntry e : getDeviceList()) {
            if (e.id.equals(deviceId)) return true;
        }
        return false;
    }

    public static String getDisplayName(String deviceId) {
        for (DeviceEntry e : getDeviceList()) {
            if (e.id.equals(deviceId)) {
                return (e.name != null && !e.name.isEmpty()) ? e.name : deviceId;
            }
        }
        return deviceId;
    }

    private static void save() {
        AppConfig.setDeviceList(new Gson().toJson(deviceList));
    }
}