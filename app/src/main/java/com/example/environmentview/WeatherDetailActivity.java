package com.example.environmentview;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Info.WeatherInfoList;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeather;
import com.example.environmentview.NetWorkUtil.GsonToData.Weather.NowWeatherKeys;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.Map;

public class WeatherDetailActivity extends AppCompatActivity {

    // tag -> [描述, 单位, 建议, 颜色索引, 范围标题, 范围低标签, 范围高标签, 最小值, 最大值, 刻度]
    private static final Map<String, String[]> WEATHER_DETAIL = new HashMap<>();
    static {
        WEATHER_DETAIL.put("温度", new String[]{"当前环境温度", "°C", "适宜温度为18~26°C，注意适时增减衣物。当前温度下建议穿着轻薄长袖，适合户外活动。", "0",
                "温度范围", "寒冷", "炎热", "-10", "45", "-10,0,20,35,45"});
        WEATHER_DETAIL.put("体感", new String[]{"人体感知温度", "°C", "体感温度受风速和湿度影响，可能与实际温度有较大差异。体感偏高时注意防暑降温。", "1",
                "体感范围", "寒冷", "炎热", "-15", "50", "-15,0,20,35,50"});
        WEATHER_DETAIL.put("天气", new String[]{"当前天气状况", "", "出行前请关注天气变化，做好相应防护准备。", "2",
                "", "", "", "", "", ""});
        WEATHER_DETAIL.put("代码", new String[]{"天气图标代码", "", "和风天气图标编码，用于匹配天气图标资源。", "3",
                "", "", "", "", "", ""});
        WEATHER_DETAIL.put("角度", new String[]{"风向角度", "°", "0°为正北方向，顺时针增加。了解风向有助于户外运动规划。", "4",
                "风向角度", "北", "北", "0", "360", "0,90,180,270,360"});
        WEATHER_DETAIL.put("风向", new String[]{"当前风向", "", "注意迎风面防护，高楼间风速可能更大。", "5",
                "", "", "", "", "", ""});
        WEATHER_DETAIL.put("风级", new String[]{"风力等级", "级", "6级以上大风注意出行安全，关好门窗。8级以上应避免户外活动。", "6",
                "风力范围", "微风", "强风", "0", "12", "0,3,6,9,12"});
        WEATHER_DETAIL.put("风速", new String[]{"当前风速", "km/h", "风速过大时减少户外活动，注意高空坠物。", "0",
                "风速范围", "平静", "暴风", "0", "120", "0,30,60,90,120"});
        WEATHER_DETAIL.put("湿度", new String[]{"相对湿度", "%", "舒适湿度为40%~70%。湿度过低皮肤干燥，过高则闷热不适。", "1",
                "湿度范围", "干燥", "潮湿", "0", "100", "0,25,50,75,100"});
        WEATHER_DETAIL.put("降水", new String[]{"近期降水量", "mm", "降水量大时请携带雨具，注意道路湿滑。", "2",
                "降水范围", "无雨", "暴雨", "0", "100", "0,10,25,50,100"});
        WEATHER_DETAIL.put("气压", new String[]{"大气压强", "hPa", "标准大气压约为1013hPa。气压骤降可能预示天气变化。", "3",
                "气压范围", "低压", "高压", "950", "1060", "950,980,1013,1040,1060"});
        WEATHER_DETAIL.put("可视", new String[]{"能见度距离", "km", "能见度低于1km注意行车安全，打开雾灯减速慢行。", "4",
                "能见度", "极差", "极佳", "0", "30", "0,1,5,10,30"});
        WEATHER_DETAIL.put("云量", new String[]{"天空云量", "%", "云量影响紫外线强度。云量少时注意防晒。", "5",
                "云量范围", "晴朗", "阴天", "0", "100", "0,25,50,75,100"});
        WEATHER_DETAIL.put("露点", new String[]{"露点温度", "°C", "露点越接近气温，空气越潮湿，体感越闷热。", "6",
                "露点范围", "干爽", "闷热", "-10", "35", "-10,0,10,20,35"});
    }

    // 舒适度关联数据：tag -> [[标签1,key1,颜色bg,颜色text], [标签2,key2,...], [标签3,key3,...]]
    private static final Map<String, String[][]> COMFORT_DATA = new HashMap<>();
    static {
        COMFORT_DATA.put("温度", new String[][]{
                {"体感温度", "feelsLike", "#E8F8F5", "#085041"},
                {"相对湿度", "humidity", "#EBF5FB", "#0C447C"},
                {"风力等级", "windScale", "#FAEEDA", "#633806"}});
        COMFORT_DATA.put("体感", new String[][]{
                {"实际温度", "temp", "#EBF5FB", "#0C447C"},
                {"风速", "windSpeed", "#FAEEDA", "#633806"},
                {"湿度", "humidity", "#E8F8F5", "#085041"}});
        COMFORT_DATA.put("湿度", new String[][]{
                {"温度", "temp", "#EBF5FB", "#0C447C"},
                {"露点", "dew", "#F5EEF8", "#3C3489"},
                {"降水量", "precip", "#E8F8F5", "#085041"}});
        COMFORT_DATA.put("风级", new String[][]{
                {"风速", "windSpeed", "#EBF5FB", "#0C447C"},
                {"风向", "windDir", "#FAEEDA", "#633806"},
                {"气压", "pressure", "#E8F8F5", "#085041"}});
        COMFORT_DATA.put("风速", new String[][]{
                {"风力等级", "windScale", "#EBF5FB", "#0C447C"},
                {"风向", "windDir", "#FAEEDA", "#633806"},
                {"气压", "pressure", "#E8F8F5", "#085041"}});
        COMFORT_DATA.put("气压", new String[][]{
                {"温度", "temp", "#EBF5FB", "#0C447C"},
                {"湿度", "humidity", "#E8F8F5", "#085041"},
                {"风速", "windSpeed", "#FAEEDA", "#633806"}});
        COMFORT_DATA.put("降水", new String[][]{
                {"湿度", "humidity", "#EBF5FB", "#0C447C"},
                {"云量", "cloud", "#F5EEF8", "#3C3489"},
                {"能见度", "vis", "#E8F8F5", "#085041"}});
        COMFORT_DATA.put("可视", new String[][]{
                {"湿度", "humidity", "#EBF5FB", "#0C447C"},
                {"降水量", "precip", "#E8F8F5", "#085041"},
                {"云量", "cloud", "#F5EEF8", "#3C3489"}});
        COMFORT_DATA.put("云量", new String[][]{
                {"天气", "text", "#EBF5FB", "#0C447C"},
                {"能见度", "vis", "#E8F8F5", "#085041"},
                {"降水量", "precip", "#FAEEDA", "#633806"}});
        COMFORT_DATA.put("露点", new String[][]{
                {"温度", "temp", "#EBF5FB", "#0C447C"},
                {"湿度", "humidity", "#E8F8F5", "#085041"},
                {"体感温度", "feelsLike", "#FAEEDA", "#633806"}});
    }

    private static final int[][] COLORS = {
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFFDEDEC, 0xFFE74C3C},
            {0xFFFEF9E7, 0xFFF39C12},
            {0xFFEAF2F8, 0xFF2980B9},
    };

    // 渐变色组
    private static final int[][] RANGE_GRADIENTS = {
            {0xFF3498DB, 0xFF2ECC71, 0xFFF1C40F, 0xFFE67E22, 0xFFE74C3C},  // 温度/体感
            {0xFFF1C40F, 0xFF2ECC71, 0xFF3498DB},                            // 湿度
            {0xFF2ECC71, 0xFF3498DB, 0xFFE67E22, 0xFFE74C3C},               // 风
            {0xFF3498DB, 0xFF2ECC71, 0xFFF1C40F},                            // 气压
            {0xFF2ECC71, 0xFF3498DB, 0xFF9B59B6, 0xFFE74C3C},               // 通用
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        String tag = getIntent().getStringExtra("tag");
        String data = getIntent().getStringExtra("data");
        String icon = getIntent().getStringExtra("icon");
        if (tag == null) tag = "未知";
        if (data == null) data = "--";
        if (icon == null) icon = "help";

        String[] detail = WEATHER_DETAIL.get(tag);
        String desc = detail != null ? detail[0] : "天气数据";
        String unit = detail != null ? detail[1] : "";
        String tip = detail != null ? detail[2] : "";
        int colorIndex = detail != null ? Integer.parseInt(detail[3]) : 0;

        int bgColor = COLORS[colorIndex % COLORS.length][0];
        int accentColor = COLORS[colorIndex % COLORS.length][1];

        // 绑定控件
        ImageButton btnBack = findViewById(R.id.iv_btn_back);
        TextView tvTitle = findViewById(R.id.tv_detail_title);
        TextView tvDesc = findViewById(R.id.tv_detail_desc);
        TextView tvIcon = findViewById(R.id.tv_detail_icon);
        TextView tvValue = findViewById(R.id.tv_detail_value);
        TextView tvUnit = findViewById(R.id.tv_detail_unit);
        TextView tvTip = findViewById(R.id.tv_detail_tip);
        View viewIconBg = findViewById(R.id.view_detail_icon_bg);
        MaterialCardView cardValue = findViewById(R.id.card_value);
        MaterialCardView cardRange = findViewById(R.id.card_range);
        MaterialCardView cardComfort = findViewById(R.id.card_comfort);
        MaterialCardView cardTip = findViewById(R.id.card_tip);

        btnBack.setOnClickListener(v -> finish());

        tvTitle.setText(tag);
        tvDesc.setText(desc);
        tvValue.setText(data);
        tvUnit.setText(unit);
        tvTip.setText(tip);

        Typeface materialFont = Typeface.createFromAsset(getAssets(), "fonts/material.otf");
        tvIcon.setTypeface(materialFont);
        tvIcon.setText(icon);
        tvIcon.setTextColor(accentColor);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(bgColor);
        viewIconBg.setBackground(iconBg);

        // ★ 范围指示条 ★
        if (detail != null && !detail[4].isEmpty()) {
            setupRangeBar(detail, data, tag);
            cardRange.setVisibility(View.VISIBLE);
        } else {
            cardRange.setVisibility(View.GONE);
        }

        // ★ 舒适度评估 ★
        String[][] comfortInfo = COMFORT_DATA.get(tag);
        if (comfortInfo != null) {
            setupComfortCards(comfortInfo);
            cardComfort.setVisibility(View.VISIBLE);
        } else {
            cardComfort.setVisibility(View.GONE);
        }

        // 动画
        AnimationHandler.setTopAnimation(this, findViewById(R.id.ll_header));
        AnimationHandler.setItemAnimation(this, cardValue, 0);
        if (cardRange.getVisibility() == View.VISIBLE)
            AnimationHandler.setItemAnimation(this, cardRange, 1);
        if (cardComfort.getVisibility() == View.VISIBLE)
            AnimationHandler.setItemAnimation(this, cardComfort, 2);
        AnimationHandler.setItemAnimation(this, cardTip, 3);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * 设置范围指示条
     */
    private void setupRangeBar(String[] detail, String data, String tag) {
        TextView tvRangeTitle = findViewById(R.id.tv_range_title);
        TextView tvRangeLow = findViewById(R.id.tv_range_low_label);
        TextView tvRangeHigh = findViewById(R.id.tv_range_high_label);
        View viewBar = findViewById(R.id.view_range_bar);
        View viewIndicator = findViewById(R.id.view_range_indicator);
        LinearLayout llLabels = findViewById(R.id.ll_range_labels);

        tvRangeTitle.setText(detail[4]);
        tvRangeLow.setText(detail[5]);
        tvRangeHigh.setText(detail[6]);

        float min = Float.parseFloat(detail[7]);
        float max = Float.parseFloat(detail[8]);
        String[] ticks = detail[9].split(",");

        // 渐变条
        int[] gradientColors;
        if (tag.equals("温度") || tag.equals("体感") || tag.equals("露点")) {
            gradientColors = RANGE_GRADIENTS[0];
        } else if (tag.equals("湿度") || tag.equals("云量")) {
            gradientColors = RANGE_GRADIENTS[1];
        } else if (tag.contains("风")) {
            gradientColors = RANGE_GRADIENTS[2];
        } else if (tag.equals("气压")) {
            gradientColors = RANGE_GRADIENTS[3];
        } else {
            gradientColors = RANGE_GRADIENTS[4];
        }

        GradientDrawable barBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        barBg.setCornerRadius(12f);
        viewBar.setBackground(barBg);

        // 指示器位置
        float value;
        try {
            value = Float.parseFloat(data.replaceAll("[^\\d.\\-]", ""));
        } catch (NumberFormatException e) {
            value = min;
        }
        float percent = Math.max(0, Math.min(1, (value - min) / (max - min)));

        viewBar.post(() -> {
            int barWidth = viewBar.getWidth();
            int indicatorSize = viewIndicator.getWidth();
            int leftMargin = (int) (percent * (barWidth - indicatorSize));

            GradientDrawable indicatorBg = new GradientDrawable();
            indicatorBg.setShape(GradientDrawable.OVAL);
            indicatorBg.setColor(0xFFFFFFFF);
            indicatorBg.setStroke(3, 0xFF2ECC71);
            viewIndicator.setBackground(indicatorBg);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewIndicator.getLayoutParams();
            params.leftMargin = leftMargin;
            viewIndicator.setLayoutParams(params);
        });

        // 刻度标签
        llLabels.removeAllViews();
        for (String tick : ticks) {
            TextView tvTick = new TextView(this);
            tvTick.setText(tick.trim());
            tvTick.setTextSize(11);
            tvTick.setTextColor(0xFFA4B0BE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvTick.setLayoutParams(lp);
            tvTick.setGravity(Gravity.CENTER);
            llLabels.addView(tvTick);
        }
    }

    /**
     * 设置舒适度评估小卡片
     */
    private void setupComfortCards(String[][] comfortInfo) {
        LinearLayout llComfort = findViewById(R.id.ll_comfort_cards);
        llComfort.removeAllViews();

        NowWeatherKeys keys = WeatherInfoList.getNowWeatherKeys();
        Map<String, String> dataMap = keys != null ? keys.getDataMap() : new HashMap<>();

        for (String[] info : comfortInfo) {
            String label = info[0];
            String key = info[1];
            int cardBg = android.graphics.Color.parseColor(info[2]);
            int cardTextColor = android.graphics.Color.parseColor(info[3]);

            String value = dataMap.getOrDefault(key, "--");

            // 创建小卡片
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            cardLp.setMargins(4, 0, 4, 0);
            card.setLayoutParams(cardLp);
            card.setPadding(12, 16, 12, 16);

            GradientDrawable cardBgDrawable = new GradientDrawable();
            cardBgDrawable.setCornerRadius(16f);
            cardBgDrawable.setColor(cardBg);
            card.setBackground(cardBgDrawable);

            // 数值
            TextView tvVal = new TextView(this);
            tvVal.setText(value);
            tvVal.setTextSize(18);
            tvVal.setTextColor(cardTextColor);
            tvVal.setTypeface(null, Typeface.BOLD);
            tvVal.setGravity(Gravity.CENTER);
            card.addView(tvVal);

            // 标签
            TextView tvLabel = new TextView(this);
            tvLabel.setText(label);
            tvLabel.setTextSize(11);
            tvLabel.setTextColor(cardTextColor);
            tvLabel.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelLp.topMargin = 4;
            tvLabel.setLayoutParams(labelLp);
            card.addView(tvLabel);

            llComfort.addView(card);
        }
    }
}