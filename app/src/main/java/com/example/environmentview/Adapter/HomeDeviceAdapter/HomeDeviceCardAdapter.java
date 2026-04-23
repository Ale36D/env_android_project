package com.example.environmentview.Adapter.HomeDeviceAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeDeviceCardAdapter extends RecyclerView.Adapter<HomeDeviceCardAdapter.DeviceViewHolder> {

    private final Context context;
    private List<DeviceManager.DeviceEntry> deviceList;
    private OnDeviceClickListener onDeviceClickListener;
    private int lastAnimatedPosition = -1;

    public interface OnDeviceClickListener {
        void onDeviceClick(DeviceManager.DeviceEntry device, int position);
    }

    public HomeDeviceCardAdapter(Context context, List<DeviceManager.DeviceEntry> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.onDeviceClickListener = listener;
    }

    public void updateDeviceList(List<DeviceManager.DeviceEntry> newList) {
        this.deviceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_device_home_info_layout, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DeviceManager.DeviceEntry device = deviceList.get(position);

        // 设备别名
        String displayName = (device.name != null && !device.name.isEmpty()) ? device.name : device.id;
        holder.tvDeviceName.setText(displayName);

        // 设备ID
        holder.tvDeviceId.setText("ID: " + device.id);

        // 在线状态
        boolean isOnline = MqttDeviceStatusManager.isDeviceOnline(device.id);
        if (isOnline) {
            holder.tvDeviceStatus.setText("状态: 在线");
            holder.tvDeviceStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.ivDeviceStatus.setImageResource(R.drawable.online);
        } else {
            holder.tvDeviceStatus.setText("状态: 离线");
            holder.tvDeviceStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.ivDeviceStatus.setImageResource(R.drawable.offline);
        }

        // 点击事件
        holder.cardDevice.setOnClickListener(v -> {
            if (onDeviceClickListener != null) {
                onDeviceClickListener.onDeviceClick(device, position);
            }
        });

        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(context, holder.itemView, position);
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return deviceList == null ? 0 : deviceList.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardDevice;
        ImageView ivDeviceIcon;
        TextView tvDeviceName;   // 别名
        TextView tvDeviceStatus; // 状态
        TextView tvDeviceId;     // ID
        ImageView ivDeviceStatus;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDevice = itemView.findViewById(R.id.cb_main_devices_info_card);
            ivDeviceIcon = itemView.findViewById(R.id.iv_dev_icon);
            tvDeviceName = itemView.findViewById(R.id.tv_dev_name);
            tvDeviceStatus = itemView.findViewById(R.id.tv_dev_status);
            tvDeviceId = itemView.findViewById(R.id.tv_dev_tag);
            ivDeviceStatus = itemView.findViewById(R.id.iv_dev_status);
        }
    }
}