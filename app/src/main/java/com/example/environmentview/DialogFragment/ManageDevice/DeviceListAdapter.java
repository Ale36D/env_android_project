package com.example.environmentview.DialogFragment.ManageDevice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(int position, String deviceId);
    }

    private final List<DeviceManager.DeviceEntry> list;
    private final OnDeleteListener deleteListener;
    // 动画标志
    private int lastAnimatedPosition = -1;

    public DeviceListAdapter(List<DeviceManager.DeviceEntry> list, OnDeleteListener deleteListener) {
        this.list = list;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_device_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DeviceManager.DeviceEntry entry = list.get(position);
        holder.tv_device_name.setText(entry.name);
        holder.tv_device_id.setText("ID: " + entry.id);
        holder.ib_delete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                deleteListener.onDelete(pos, list.get(pos).id);
            }
        });
        // 只播放一次
        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(holder.itemView.getContext(), holder.itemView, position);
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_device_name, tv_device_id;
        ImageButton ib_delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_device_name = itemView.findViewById(R.id.tv_device_name);
            tv_device_id   = itemView.findViewById(R.id.tv_device_id);
            ib_delete      = itemView.findViewById(R.id.ib_delete_device);
        }
    }
}