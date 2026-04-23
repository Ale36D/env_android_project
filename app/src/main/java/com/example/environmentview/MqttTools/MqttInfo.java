package com.example.environmentview.MqttTools;

public class MqttInfo {

    String broker = "";
    String clientID = "";
    String username = "";
    String password = "";
    String subscribe = "";
    String publish = "";

    public MqttInfo(String broker, String clientID, String username, String password, String subscribe, String publish) {
        this.broker = broker;
        this.clientID = clientID;
        this.username = username;
        this.password = password;
        this.subscribe = subscribe;
        this.publish = publish;
    }

    public MqttInfo(String broker, String clientId, String subscribe, String publish) {
        this.broker = broker;
        this.clientID = clientId;
        this.subscribe = subscribe;
        this.publish = publish;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

}
