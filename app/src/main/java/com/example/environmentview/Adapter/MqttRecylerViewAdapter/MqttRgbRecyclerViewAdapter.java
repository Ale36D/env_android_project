package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.List;

public class MqttRgbRecyclerViewAdapter extends RecyclerView.Adapter<MqttRgbRecyclerViewAdapter.RgbViewHolder> {

    public interface OnRgbValueChangedListener {
        void onValueChanged(int position, String tag, int value);
    }

    private final Context context;
    private final ArrayList<MqttDataModel> dataList;
    private final Typeface materialIconFont;
    private OnRgbValueChangedListener listener;
    private boolean isFirstLoad = true;

    // R, G, B 各自的颜色
    private static final int[][] RGB_COLORS = {
            {0xFFFDEDEC, 0xFFE74C3C},  // 红
            {0xFFE8F8F5, 0xFF27AE60},  // 绿
            {0xFFEBF5FB, 0xFF2980B9},  // 蓝
    };

    public MqttRgbRecyclerViewAdapter(Context context, ArrayList<MqttDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.materialIconFont = Typeface.createFromAsset(
                context.getAssets(), "fonts/material.otf");
    }

    public void setOnRgbValueChangedListener(OnRgbValueChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RgbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_rgb_data_show_layout, parent, false);
        return new RgbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RgbViewHolder holder, int position) {
        MqttDataModel model = dataList.get(position);

        holder.tv_rgb_name.setText(model.getSensorName());
        holder.tv_rgb_icon.setTypeface(materialIconFont);
        holder.tv_rgb_icon.setText(model.getSensorIcon());

        // 设置颜色圆形背景
        int colorIndex = position % RGB_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(RGB_COLORS[colorIndex][0]);
        holder.view_rgb_color_preview.setBackground(bg);
        holder.tv_rgb_icon.setTextColor(RGB_COLORS[colorIndex][1]);


        // 设置当前值
        holder.tv_rgb_value.setText(model.getSensorValue());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MqttDataModel> list) {
        // ★ 如果列表为空，清空并重置 ★
        if (list == null || list.isEmpty()) {
            dataList.clear();
            notifyDataSetChanged();
            isFirstLoad = true;
            return;
        }

        if (isFirstLoad) {
            dataList.clear();
            dataList.addAll(list);
            notifyDataSetChanged();
            isFirstLoad = false;
        } else {
            for (int i = 0; i < list.size() && i < dataList.size(); i++) {
                MqttDataModel oldItem = dataList.get(i);
                MqttDataModel newItem = list.get(i);
                if (!oldItem.getSensorValue().equals(newItem.getSensorValue())) {
                    oldItem.setSensorValue(newItem.getSensorValue());
                    notifyItemChanged(i);
                }
            }
        }
    }

    /**
     * 获取当前 R, G, B 三个值
     */
    public int[] getCurrentRgbValues() {
        int[] rgb = new int[3];
        for (int i = 0; i < dataList.size() && i < 3; i++) {
            try {
                rgb[i] = Integer.parseInt(dataList.get(i).getSensorValue());
            } catch (NumberFormatException e) {
                rgb[i] = 0;
            }
        }
        return rgb;
    }

    public static class RgbViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rgb_name, tv_rgb_icon, tv_rgb_value;
        View view_rgb_color_preview;

        public RgbViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rgb_name = itemView.findViewById(R.id.tv_rgb_name);
            tv_rgb_icon = itemView.findViewById(R.id.tv_rgb_icon);
            tv_rgb_value = itemView.findViewById(R.id.tv_rgb_value);
            view_rgb_color_preview = itemView.findViewById(R.id.view_rgb_color_preview);
        }
    }
}