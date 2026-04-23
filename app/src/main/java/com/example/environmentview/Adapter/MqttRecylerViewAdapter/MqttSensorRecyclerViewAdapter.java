package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

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

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.List;

public class MqttSensorRecyclerViewAdapter extends RecyclerView.Adapter<MqttSensorRecyclerViewAdapter.MqttInfoViewHolder>{
    private final Context context;
    private final ArrayList<MqttDataModel> mqttDataModel;

    private final Typeface materialIconFont;

    private boolean isFirstLoad = true;
    private static final int[][] ICON_COLORS = {
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFFDEDEC, 0xFFE74C3C},
    };

    public MqttSensorRecyclerViewAdapter(Context context, ArrayList<MqttDataModel> mqttDataModel) {
        this.context = context;
        this.mqttDataModel = mqttDataModel;
        this.materialIconFont = Typeface.createFromAsset(
                context.getAssets(), "fonts/material.otf");
    }

    @NonNull
    @Override
    public MqttSensorRecyclerViewAdapter.MqttInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sensor_data_show_layout, parent, false);
        return new MqttInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MqttSensorRecyclerViewAdapter.MqttInfoViewHolder holder, int position) {
        MqttDataModel model = mqttDataModel.get(position);
        holder.tv_sensor_name.setText(model.getSensorName());
        holder.tv_sensor_tag.setText(model.getSensorTag());
        holder.tv_sensor_data.setText(model.getSensorValue());

        holder.tv_sensor_icon.setTypeface(materialIconFont);
        holder.tv_sensor_icon.setText(model.getSensorIcon());

        int colorIndex = position % ICON_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ICON_COLORS[colorIndex][0]);
        holder.view_sensor_icon_bg.setBackground(bg);
        holder.tv_sensor_icon.setTextColor(ICON_COLORS[colorIndex][1]);
    }

    @Override
    public int getItemCount() {
        return mqttDataModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MqttDataModel> list) {
        // ★ 如果列表为空，清空并重置 ★
        if (list == null || list.isEmpty()) {
            mqttDataModel.clear();
            notifyDataSetChanged();
            isFirstLoad = true;
            return;
        }

        if (isFirstLoad) {
            mqttDataModel.clear();
            mqttDataModel.addAll(list);
            notifyDataSetChanged();
            isFirstLoad = false;
        } else {
            for (int i = 0; i < list.size() && i < mqttDataModel.size(); i++) {
                MqttDataModel oldItem = mqttDataModel.get(i);
                MqttDataModel newItem = list.get(i);
                if (!oldItem.getSensorValue().equals(newItem.getSensorValue())) {
                    oldItem.setSensorValue(newItem.getSensorValue());
                    notifyItemChanged(i);
                }
            }
            if (list.size() > mqttDataModel.size()) {
                int oldSize = mqttDataModel.size();
                for (int i = oldSize; i < list.size(); i++) {
                    mqttDataModel.add(list.get(i));
                }
                notifyItemRangeInserted(oldSize, list.size() - oldSize);
            }
        }
    }

    public static class MqttInfoViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sensor_name, tv_sensor_tag, tv_sensor_data, tv_sensor_icon;
        View view_sensor_icon_bg;
        public MqttInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sensor_name = itemView.findViewById(R.id.tv_sensor_name);
            tv_sensor_tag = itemView.findViewById(R.id.tv_sensor_tag);
            tv_sensor_data = itemView.findViewById(R.id.tv_sensor_data);
            tv_sensor_icon = itemView.findViewById(R.id.tv_sensor_icon);
            view_sensor_icon_bg = itemView.findViewById(R.id.view_sensor_icon_bg);

        }
    }
}
