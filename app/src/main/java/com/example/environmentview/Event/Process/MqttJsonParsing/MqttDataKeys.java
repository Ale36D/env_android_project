package com.example.environmentview.Event.Process.MqttJsonParsing;


public class MqttDataKeys {
    public static final String TAG = "tag";
    public static final String DATA = "data";
    public static final String TYPE = "type";
    // ===== root =====
    public static final String ID = "id";
    public static final String UPDATE_TIME = "updateTime";
    public static final String BODY = "body";
    // ===== body =====
    public static final String RELAY = "relay";
    public static final String SENSOR = "sensor"; // 注意拼写

    // ===== relay =====
    public static final String RELAY_1 = "r_1";
    public static final String RELAY_2 = "r_2";
    public static final String RELAY_3 = "r_3";
    public static final String RELAY_4 = "r_4";
    public static final String BUZZER = "bz";

    // ===== sensor =====
    public static final String TP = "tp";
    public static final String HM = "hm";
    public static final String L  = "l";
    public static final String VT = "vt";
    public static final String DP = "dp";
    public static final String PR = "pr";
    public static final String AL = "al";
    public static final String MADC = "madc";
    public static final String MDI = "mdi";
    public static final String FD = "fd";
    public static final String AX = "aX";
    public static final String AY = "aY";
    public static final String AZ = "aZ";
    public static final String RL = "rl";
    public static final String PC = "pc";

    // ===== rgb =====
    public static final String RGB_R = "r";
    public static final String RGB_G = "g";
    public static final String RGB_B = "b";
}
