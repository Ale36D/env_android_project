package com.example.environmentview.Adapter.WeaterAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.WeatherModel.WeatherModel;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.R;
import com.example.environmentview.WeatherDetailActivity;

import java.util.ArrayList;


public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherInfoViewHolder> {
    private final Context context;
    private final ArrayList<WeatherModel> weatherModel;
    private final Typeface materialIconFont;

    // 控制动画
    private int lastAnimatedPosition = -1;

    // 7组配色：[背景色, 图标色]，按 position 交替使用
    private static final int[][] ICON_COLORS = {
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFFDEDEC, 0xFFE74C3C},
            {0xFFFEF9E7, 0xFFF39C12},
            {0xFFEAF2F8, 0xFF2980B9},
    };



    public WeatherRecyclerViewAdapter(Context context, ArrayList<WeatherModel> weatherModel) {
        this.context = context;
        this.weatherModel = weatherModel;
        this.materialIconFont = Typeface.createFromAsset(
                context.getAssets(), "fonts/material.otf");
    }

    @NonNull
    @Override
    public WeatherRecyclerViewAdapter.WeatherInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取控件上的对象
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_weather_info_card_layout, parent, false);
        return new WeatherInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRecyclerViewAdapter.WeatherInfoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WeatherModel model = this.weatherModel.get(position);
        holder.tv_weather_tag.setText(model.getTag());
        holder.tv_weather_data.setText(model.getData());

        // 设置 Material Icon 字体 + 图标文本
        holder.tv_weather_icon.setTypeface(materialIconFont);
        holder.tv_weather_icon.setText(model.getIcon());

        int colorIndex = position % ICON_COLORS.length;
        int bgColor = ICON_COLORS[colorIndex][0];
        int iconColor = ICON_COLORS[colorIndex][1];

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setShape(GradientDrawable.OVAL);
        bgDrawable.setColor(bgColor);
        holder.view_icon_bg.setBackground(bgDrawable);
        holder.tv_weather_icon.setTextColor(iconColor);

        // 首次加载时播放动画
        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(context, holder.itemView, position);
            lastAnimatedPosition = position;
        }
        // 点击跳转天气详情页
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, WeatherDetailActivity.class);
            intent.putExtra("tag", model.getTag());
            intent.putExtra("data", model.getData());
            intent.putExtra("icon", model.getIcon());
            context.startActivity(intent);
            // 进入动画
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.weatherModel.size();
    }

    public static class WeatherInfoViewHolder extends RecyclerView.ViewHolder {
        TextView tv_weather_tag;
        TextView tv_weather_data;
        TextView tv_weather_icon;
        View view_icon_bg;

        public WeatherInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_weather_tag = itemView.findViewById(R.id.tv_weather_tag);
            tv_weather_data = itemView.findViewById(R.id.tv_weather_data);
            tv_weather_icon = itemView.findViewById(R.id.tv_weather_icon);

            view_icon_bg = itemView.findViewById(R.id.view_icon_bg);
        }
    }
}
