package com.example.environmentview.Adapter.Model.MqttDataModel;

public class MqttDataModel {
    String relayName = "";
    String relayTag = "";
    String relayValue = "";
    String sensorName = "";
    String sensorTag = "";
    String sensorValue = "";

    String relayIcon = "";

    public String getSensorIcon() {
        return sensorIcon;
    }

    public void setSensorIcon(String sensorIcon) {
        this.sensorIcon = sensorIcon;
    }

    public String getRelayIcon() {
        return relayIcon;
    }

    public void setRelayIcon(String relayIcon) {
        this.relayIcon = relayIcon;
    }

    String sensorIcon = "";

    public String getSensorTag() {
        return sensorTag;
    }

    public void setSensorTag(String sensorTag) {
        this.sensorTag = sensorTag;
    }
    public String getRelayName() {
        return relayName;
    }
    public String getRelayTag() {
        return relayTag;
    }

    public void setRelayTag(String relayTag) {
        this.relayTag = relayTag;
    }
    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public String getRelayValue() {
        return relayValue;
    }

    public void setRelayValue(String relayValue) {
        this.relayValue = relayValue;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }
}
