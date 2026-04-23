package com.example.environmentview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Event.WeatherEventHandler;

public class WeatherInfo extends AppCompatActivity {

    private RecyclerView rv_weather_info;
    private RecyclerView rv_life_suggestion;
    private LinearLayout ll_life_header, ll_detail_header;
    private ImageButton iv_life_expand_arrow, iv_detail_expand_arrow;
    private TextView tv_life_count, tv_detail_count;
    private boolean lifeExpanded = true;
    private boolean detailExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.viewInit();
        this.setViewInfo();
        this.setViewAnimation(this);
        this.initLifeHeader();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void setViewInfo() {
        WeatherEventHandler.setViewAdapter(this, this, rv_weather_info);
        WeatherEventHandler.setWeatherCard(this, this);
        WeatherEventHandler.setLifeSuggestionAdapter(this, this, rv_life_suggestion);
    }
    private void setViewAnimation(Activity activity){
        WeatherEventHandler.setWeatherCardAnimation(this, this);
    }

    private void viewInit() {
        rv_weather_info = findViewById(R.id.rv_weather_info);
        rv_life_suggestion = findViewById(R.id.rv_life_suggestion);
        ll_life_header = findViewById(R.id.ll_life_header);
        iv_life_expand_arrow = findViewById(R.id.iv_life_expand_arrow);
        tv_life_count = findViewById(R.id.tv_life_count);
        ll_detail_header = findViewById(R.id.ll_detail_header);
        iv_detail_expand_arrow = findViewById(R.id.iv_detail_expand_arrow);
        tv_detail_count = findViewById(R.id.tv_detail_count);
    }

    private void initLifeHeader() {
        // 生活指数数量
        int lifeCount = rv_life_suggestion.getAdapter() != null
                ? rv_life_suggestion.getAdapter().getItemCount() : 0;
        tv_life_count.setText(lifeCount + "项");

        // 详情数量
        int detailCount = rv_weather_info.getAdapter() != null
                ? rv_weather_info.getAdapter().getItemCount() : 0;
        tv_detail_count.setText(detailCount + "项");

        // 生活指数折叠
        ll_life_header.setOnClickListener(v -> {
            lifeExpanded = !lifeExpanded;
            rv_life_suggestion.setVisibility(lifeExpanded ? View.VISIBLE : View.GONE);
            iv_life_expand_arrow.animate()
                    .rotation(lifeExpanded ? 0f : 180f)
                    .setDuration(200)
                    .start();
        });

        // 详情折叠
        ll_detail_header.setOnClickListener(v -> {
            detailExpanded = !detailExpanded;
            rv_weather_info.setVisibility(detailExpanded ? View.VISIBLE : View.GONE);
            iv_detail_expand_arrow.animate()
                    .rotation(detailExpanded ? 0f : 180f)
                    .setDuration(200)
                    .start();
        });
    }
}