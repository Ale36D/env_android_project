package com.example.environmentview.Adapter.Model.WeatherModel;

public class WeatherModel {
    private String tag = "";
    private String data = "";
    private String icon = "";


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public WeatherModel(String tag, String data) {
        this.tag = tag;
        this.data = data;
    }
    public WeatherModel(String tag, String data, String icon) {
        this.tag = tag;
        this.data = data;
        this.icon = icon;
    }

}
