package com.example.environmentview.Event.Process.MqttJsonParsing;

import java.util.Map;

public class MqttMessage {
    public String topic;

    public String id;
    public String cmd;
    public Body body;


    public static class Body {
        public Relay relay;
        public Sensor sensor;
        public Rgb rgb;
        public int Online;
    }
    public static class Relay {
        public int r_1;
        public int r_2;
        public int r_3;
        public int r_4;
        public int bz;
    }

    public static class Sensor {
        public float tp;
        public float hm;
        public int l;
        public int vt;
        public int dp;
        public int pr;
        public int al;
        public int madc;
        public int mdi;
        public int fd;
        public int aX;
        public int aY;
        public int aZ;
        public int rl;
        public int pc;
    }

    public static class Rgb{
        public int r = 0;
        public int g = 0;
        public int b = 0;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
