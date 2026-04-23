package com.example.environmentview.Event.Process.MqttJsonParsing;

import java.util.HashMap;
import java.util.Map;

public class MqttDataMap {
    Map<String, String> relayMap;
    Map<String, String> sensorMap;
    Map<String, String> rgbMap;



    public MqttDataMap(Map<String, String> relayMap, Map<String, String> sensorMap, Map<String, String> rgbMap) {
        this.relayMap = relayMap;
        this.sensorMap = sensorMap;
        this.rgbMap = rgbMap;
    }

    public static MqttDataMap empty() {
        return new MqttDataMap(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }



    public Map<String, String> getRelayMap() {
        return relayMap;
    }

    public void setRelayMap(Map<String, String> relayMap) {
        this.relayMap = relayMap;
    }

    public Map<String, String> getSensorMap() {
        return sensorMap;
    }

    public void setSensorMap(Map<String, String> sensorMap) {
        this.sensorMap = sensorMap;
    }
    public Map<String, String> getRgbMap() {
        return rgbMap;
    }

    public void setRgbMap(Map<String, String> rgbMap) {
        this.rgbMap = rgbMap;
    }
}
