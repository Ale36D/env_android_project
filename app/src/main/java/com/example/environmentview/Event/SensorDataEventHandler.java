package com.example.environmentview.Event;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Adapter.MqttRecylerViewAdapter.MqttActuatorRecyclerViewAdapter;
import com.example.environmentview.Adapter.MqttRecylerViewAdapter.MqttRgbRecyclerViewAdapter;
import com.example.environmentview.Adapter.MqttRecylerViewAdapter.MqttSensorRecyclerViewAdapter;
import com.example.environmentview.DialogFragment.ManageDevice.ManageDeviceDialog;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataKeys;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.FragmentPage.HomeFragment;
import com.example.environmentview.FragmentPage.SensorDataFragment;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SensorDataEventHandler {

    // ===== 传感器：tag -> [中文名, 单位, Material Icon] =====
    private static final Map<String, String[]> SENSOR_META = new HashMap<>();
    static {
        SENSOR_META.put(MqttDataKeys.TP,  new String[]{"温度",   "°C",   "thermostat"});
        SENSOR_META.put(MqttDataKeys.HM,  new String[]{"湿度",   "%",    "water_drop"});
        SENSOR_META.put(MqttDataKeys.L,   new String[]{"光照",   "lux",  "light_mode"});
        SENSOR_META.put(MqttDataKeys.VT,  new String[]{"振动",   "",     "vibration"});
        SENSOR_META.put(MqttDataKeys.DP,  new String[]{"倾斜",   "",     "screen_rotation"});
        SENSOR_META.put(MqttDataKeys.PR,  new String[]{"大气压力",   "pa",   "air"});
        SENSOR_META.put(MqttDataKeys.AL,  new String[]{"高度",   "m",  "height"});
        SENSOR_META.put(MqttDataKeys.MADC,new String[]{"甲烷浓度",    "",     "science"});
        SENSOR_META.put(MqttDataKeys.MDI, new String[]{"甲烷报警",    "",     "science"});
        SENSOR_META.put(MqttDataKeys.FD,  new String[]{"水浸",   "",     "water"});
        SENSOR_META.put(MqttDataKeys.AX,  new String[]{"加速X",  "m/s²", "speed"});
        SENSOR_META.put(MqttDataKeys.AY,  new String[]{"加速Y",  "m/s²", "speed"});
        SENSOR_META.put(MqttDataKeys.AZ,  new String[]{"加速Z",  "m/s²", "speed"});
        SENSOR_META.put(MqttDataKeys.RL,  new String[]{"横滚角", "°",    "360"});
        SENSOR_META.put(MqttDataKeys.PC,  new String[]{"俯仰角", "°",    "360"});
    }

    // ===== 执行器：tag -> [中文名, Material Icon] =====
    private static final Map<String, String[]> RELAY_META = new HashMap<>();
    static {
        RELAY_META.put(MqttDataKeys.RELAY_1, new String[]{"继电器1", "power"});
        RELAY_META.put(MqttDataKeys.RELAY_2, new String[]{"继电器2", "power"});
        RELAY_META.put(MqttDataKeys.RELAY_3, new String[]{"继电器3", "power"});
        RELAY_META.put(MqttDataKeys.RELAY_4, new String[]{"继电器4", "power"});
        RELAY_META.put(MqttDataKeys.BUZZER,  new String[]{"蜂鸣器",  "notifications"});
    }
    // ===== RGB：tag -> [中文名, Material Icon] =====
    private static final Map<String, String[]> RGB_META = new HashMap<>();
    static {
        RGB_META.put(MqttDataKeys.RGB_R, new String[]{"红色 R", "palette"});
        RGB_META.put(MqttDataKeys.RGB_G, new String[]{"绿色 G", "palette"});
        RGB_META.put(MqttDataKeys.RGB_B, new String[]{"蓝色 B", "palette"});
    }

    public static List<MqttDataModel> relayMapToList(MqttDataMap dataMap){
        List<MqttDataModel> list = new ArrayList<>();
        if (dataMap == null || dataMap.getRelayMap() == null) return list;

        // ★ 固定顺序：继电器1 → 2 → 3 → 4 → 蜂鸣器 ★
        String[] order = {
                MqttDataKeys.RELAY_1, MqttDataKeys.RELAY_2,
                MqttDataKeys.RELAY_3, MqttDataKeys.RELAY_4,
                MqttDataKeys.BUZZER
        };

        for (String tag : order) {
            String value = dataMap.getRelayMap().get(tag);
            if (value == null) continue;

            MqttDataModel model = new MqttDataModel();
            String[] meta = RELAY_META.get(tag);

            model.setRelayName(meta != null ? meta[0] : tag);
            model.setRelayTag(tag);
            model.setRelayValue(value);
            model.setRelayIcon(meta != null ? meta[1] : "toggle_on");
            list.add(model);
        }
        return list;
    }
    // ===== 传感器：tag -> [中文名, 单位, Material Icon] =====
    public static List<MqttDataModel> sensorMapToList(MqttDataMap dataMap) {
        List<MqttDataModel> list = new ArrayList<>();
        if (dataMap == null || dataMap.getSensorMap() == null) return list;

        // ★ 固定顺序 ★
        String[] order = {
                MqttDataKeys.TP, MqttDataKeys.HM, MqttDataKeys.L,
                MqttDataKeys.VT, MqttDataKeys.DP, MqttDataKeys.FD,
                MqttDataKeys.AX, MqttDataKeys.AY, MqttDataKeys.AZ,
                MqttDataKeys.RL, MqttDataKeys.PC, MqttDataKeys.PR,
                MqttDataKeys.AL, MqttDataKeys.MADC, MqttDataKeys.MDI
        };

        for (String tag : order) {
            String value = dataMap.getSensorMap().get(tag);
            if (value == null) continue;

            MqttDataModel model = new MqttDataModel();
            String[] meta = SENSOR_META.get(tag);

            model.setSensorName(meta != null ? meta[0] : tag);
            model.setSensorTag(tag);
            String unit = (meta != null && !meta[1].isEmpty()) ? meta[1] : "";
            model.setSensorValue(value + unit);
            model.setSensorIcon(meta != null ? meta[2] : "sensors");
            list.add(model);
        }
        return list;
    }
    /**
     * RGB 数据转为卡片列表
     * 复用 sensor 的字段：sensorName=中文名, sensorTag=tag, sensorValue=值, sensorIcon=图标
     */
    public static List<MqttDataModel> rgbMapToList(MqttDataMap dataMap) {
        List<MqttDataModel> list = new ArrayList<>();
        if (dataMap == null || dataMap.getRgbMap() == null) return list;

        String[] order = { MqttDataKeys.RGB_R, MqttDataKeys.RGB_G, MqttDataKeys.RGB_B };

        for (String tag : order) {
            String value = dataMap.getRgbMap().get(tag);
            if (value == null) continue;

            MqttDataModel model = new MqttDataModel();
            String[] meta = RGB_META.get(tag);

            model.setSensorName(meta != null ? meta[0] : tag);
            model.setSensorTag(tag);
            model.setSensorValue(value);
            model.setSensorIcon(meta != null ? meta[1] : "palette");
            list.add(model);
        }
        return list;
    }

    public static void showManageDeviceDialog(FragmentManager fragmentManager, SensorDataFragment sensorDataFragment) {
        ManageDeviceDialog dialog = new ManageDeviceDialog();
        dialog.setOnDeviceListChangedListener(() -> {
            List<String> ids = new ArrayList<>();
            for (DeviceManager.DeviceEntry e : DeviceManager.getDeviceList()) {
                ids.add(e.id);
            }
            sensorDataFragment.refreshDeviceList();

            MqttRepository.notifyDeviceListChanged();
        });

        dialog.show(fragmentManager, "manage_device");
    }

    public static void setNowDateToView(FragmentActivity fragmentActivity, Context context, TextView tvNowDate) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd号 MMMM yyyy", Locale.CHINA);
        tvNowDate.setText(simpleDateFormat.format(date));
    }
}
