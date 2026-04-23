package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttActuatorRecyclerViewAdapter extends RecyclerView.Adapter<MqttActuatorRecyclerViewAdapter.MqttInfoViewHolder>{
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

    public MqttActuatorRecyclerViewAdapter(Context context, ArrayList<MqttDataModel> mqttDataModel) {
        this.context = context;
        this.mqttDataModel = mqttDataModel;
        this.materialIconFont = Typeface.createFromAsset(
                context.getAssets(), "fonts/material.otf");

    }

    @NonNull
    @Override
    public MqttActuatorRecyclerViewAdapter.MqttInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_actuator_data_show_layout, parent, false);
        return new MqttActuatorRecyclerViewAdapter.MqttInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MqttActuatorRecyclerViewAdapter.MqttInfoViewHolder holder, int position) {
        MqttDataModel model = mqttDataModel.get(position);
        holder.tv_relay_name.setText(model.getRelayName());
        holder.tv_relay_tag.setText(model.getRelayTag());

        holder.tv_relay_icon.setTypeface(materialIconFont);
        holder.tv_relay_icon.setText(model.getRelayIcon());

        int colorIndex = position % ICON_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ICON_COLORS[colorIndex][0]);
        holder.view_relay_icon_bg.setBackground(bg);
        holder.tv_relay_icon.setTextColor(ICON_COLORS[colorIndex][1]);

        holder.sc_relay_switch.setOnCheckedChangeListener(null);
        boolean isOn = "1".equals(model.getRelayValue());
        holder.sc_relay_switch.setChecked(isOn);

        holder.sc_relay_switch.setOnCheckedChangeListener((buttonView, checked) -> {
            // TODO: 发送 MQTT 控制命令
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            // ★ 先更新本地 model，保证状态一致 ★
            mqttDataModel.get(adapterPos).setRelayValue(checked ? "1" : "0");

            String deviceId = MqttRepository.getCurrentDeviceId();
            if (deviceId == null) return;

            Map<String, Object> relayState = new HashMap<>();
            for (int i = 0; i < mqttDataModel.size(); i++) {
                MqttDataModel item = mqttDataModel.get(i);
                relayState.put(item.getRelayTag(),
                        "1".equals(item.getRelayValue()) ? 1 : 0);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("relay", relayState);
            MqttCommandSender.sendCommand(deviceId, 0, body);
        });
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
                if (!oldItem.getRelayValue().equals(newItem.getRelayValue())) {
                    oldItem.setRelayValue(newItem.getRelayValue());
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
        TextView tv_relay_name, tv_relay_tag, tv_relay_icon;
        View view_relay_icon_bg;
        Switch sc_relay_switch;


        public MqttInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_relay_name = itemView.findViewById(R.id.tv_relay_name);
            tv_relay_tag = itemView.findViewById(R.id.tv_relay_tag);
            tv_relay_icon = itemView.findViewById(R.id.tv_relay_icon);
            view_relay_icon_bg = itemView.findViewById(R.id.view_relay_icon_bg);
            sc_relay_switch = itemView.findViewById(R.id.sc_relay_switch);
        }
    }
}
