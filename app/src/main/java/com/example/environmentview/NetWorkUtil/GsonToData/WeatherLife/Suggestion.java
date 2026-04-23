package com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife;

import java.util.Map;

public class Suggestion {
    public static class Index {
        public String brief;

        public String details;
        public Index() {
        }

        public Index(String brief, String details) {
            this.brief = brief;
            this.details = details;
        }



        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }
    }
    public String date;
    public Index ac;
    public Index air_pollution;
    public Index airing;
    public Index allergy;
    public Index beer;
    public Index boating;
    public Index car_washing;
    public Index comfort;
    public Index dressing;
    public Index fishing;
    public Index flu;
    public Index kiteflying;
    public Index makeup;
    public Index mood;
    public Index morning_sport;
    public Index road_condition;
    public Index shopping;
    public Index sport;
    public Index sunscreen;
    public Index traffic;
    public Index umbrella;
    public Index uv;

}

