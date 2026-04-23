package com.example.environmentview.NetWorkUtil.GsonToData.WeatherLife;

import java.util.Map;

public class WeatherLifeKeys {
    // Result 层
    public static final String RESULTS = "results";
    public static final String LOCATION = "location";
    public static final String SUGGESTION = "suggestion";
    public static final String LAST_UPDATE = "last_update";

    // Location 层
    public static final String LOCATION_ID = "id";
    public static final String LOCATION_NAME = "name";
    public static final String LOCATION_COUNTRY = "country";
    public static final String LOCATION_PATH = "path";
    public static final String LOCATION_TIMEZONE = "timezone";
    public static final String LOCATION_TIMEZONE_OFFSET = "timezone_offset";

    // Suggestion 层
    public static final String DATE = "date";

    public static final String AC = "ac";
    public static final String AIR_POLLUTION = "air_pollution";
    public static final String AIRING = "airing";
    public static final String ALLERGY = "allergy";
    public static final String BEER = "beer";
    public static final String BOATING = "boating";
    public static final String CAR_WASHING = "car_washing";
    public static final String COMFORT = "comfort";
    public static final String DRESSING = "dressing";
    public static final String FISHING = "fishing";
    public static final String FLU = "flu";
    public static final String KITEFLYING = "kiteflying";
    public static final String MAKEUP = "makeup";
    public static final String MOOD = "mood";
    public static final String MORNING_SPORT = "morning_sport";
    public static final String ROAD_CONDITION = "road_condition";
    public static final String SHOPPING = "shopping";
    public static final String SPORT = "sport";
    public static final String SUNSCREEN = "sunscreen";
    public static final String TRAFFIC = "traffic";
    public static final String UMBRELLA = "umbrella";
    public static final String UV = "uv";

    // Suggestion 内部对象 key
    public static final String BRIEF = "brief";
    public static final String DETAILS = "details";

    private Map<String, Suggestion.Index> dataMap;

    public Map<String, Suggestion.Index> getDataMap() {
        return dataMap;
    }
    public void setDataMap(Map<String, Suggestion.Index> dataMap) {
        this.dataMap = dataMap;
    }
}
