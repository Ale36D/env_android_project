package com.example.environmentview.FragmentPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.DialogFragment.MessageDetail.MessageDetailDialog;
import com.example.environmentview.Event.MainEventHandler;
import com.example.environmentview.Event.MessageCenterHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttMessageProcess;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.MqttTools.MqttClientManager;
import com.example.environmentview.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageCenterFragment extends Fragment {

    private RecyclerView rvMessages;
    private LinearLayout llEmpty;
    private TextView tvTotalCount, tvOnlineCount, tvLastTime, tvConnectStatus;
    private MaterialButton btnClear;
    private View messageCenterView;

    public final List<MsgItem> msgList = new ArrayList<>();
    public MsgAdapter adapter;
    public int totalReceived = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageCenterView = view;
        MainEventHandler.setMessageCenterView(messageCenterView);

        this.viewInit(view);
        this.viewInitListener();
        this.viewContentSetting(view);
        this.viewAnimationSetting(view);

        this.updateConnectStatus();
        this.updateUI();

        // 监听设备在线状态变化
        MqttDeviceStatusManager.getStatusLiveData().observe(getViewLifecycleOwner(), status -> {
            updateOnlineCount();
            updateConnectStatus();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        MainEventHandler.setMessageCenterAnimationView(requireActivity(), requireContext(), MainEventHandler.getMessageCenterView());
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void viewAnimationSetting(View view) {
        MainEventHandler.setMessageCenterAnimationView(requireActivity(), requireContext(), view);
    }

    private void viewContentSetting(View view) {
        adapter = new MsgAdapter(msgList);
        adapter.setOnItemClickListener(item -> {
            MessageDetailDialog dialog = MessageDetailDialog.newInstance(
                    item.deviceId, item.topic, item.payload, item.time);
            dialog.show(getChildFragmentManager(), "msg_detail");
        });
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(adapter);

        // 注册消息监听（利用现有的接口拦截报文）
        MqttMessageProcess process = new MqttMessageProcess();
        process.setMqttMessageProcessInterface((topic, message) -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                totalReceived++;
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String deviceId = extractDeviceId(message);
                String formatted = formatJson(message);

                msgList.add(0, new MsgItem(topic, deviceId, formatted, time));
                if (msgList.size() > 30) {
                    msgList.remove(msgList.size() - 1);
                }
                adapter.notifyDataSetChanged();
                updateUI();
                updateConnectStatus();
            });
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void viewInitListener() {
        btnClear.setOnClickListener(v -> {
            msgList.clear();
            totalReceived = 0;
            adapter.notifyDataSetChanged();
            updateUI();
        });
    }

    private void viewInit(View view) {
        rvMessages = view.findViewById(R.id.rv_messages);
        llEmpty = view.findViewById(R.id.ll_empty);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
        tvOnlineCount = view.findViewById(R.id.tv_online_count);
        tvLastTime = view.findViewById(R.id.tv_last_time);
        tvConnectStatus = view.findViewById(R.id.tv_connect_status);
        btnClear = view.findViewById(R.id.btn_clear);
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        tvTotalCount.setText(String.valueOf(totalReceived));
        updateOnlineCount();

        if (msgList.isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            rvMessages.setVisibility(View.GONE);
            tvLastTime.setText("--");
        } else {
            llEmpty.setVisibility(View.GONE);
            rvMessages.setVisibility(View.VISIBLE);
            tvLastTime.setText(msgList.get(0).time);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateOnlineCount() {
        int online = 0;
        List<DeviceManager.DeviceEntry> devices = DeviceManager.getDeviceList();
        for (DeviceManager.DeviceEntry d : devices) {
            if (MqttDeviceStatusManager.isDeviceOnline(d.id)) online++;
        }
        tvOnlineCount.setText(String.valueOf(online));
    }

    @SuppressLint("SetTextI18n")
    private void updateConnectStatus() {
        boolean connected = MqttClientManager.getInstance().mqttIsConnected();
        if (connected) {
            tvConnectStatus.setText("已连接");
            tvConnectStatus.setTextColor(0xFF085041);
        } else {
            tvConnectStatus.setText("未连接");
            tvConnectStatus.setTextColor(0xFFE74C3C);
        }
    }

    private String extractDeviceId(String json) {
        try {
            return JsonParser.parseString(json).getAsJsonObject().get("id").getAsString();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String formatJson(String json) {
        try {
            JsonElement el = JsonParser.parseString(json);
            return new GsonBuilder().setPrettyPrinting().create().toJson(el);
        } catch (Exception e) {
            return json;
        }
    }

    // ===== 数据模型 =====
    static class MsgItem {
        String topic, deviceId, payload, time;
        MsgItem(String topic, String deviceId, String payload, String time) {
            this.topic = topic;
            this.deviceId = deviceId;
            this.payload = payload;
            this.time = time;
        }
    }

    // ===== Adapter =====
    static class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.VH> {
        private final List<MsgItem> list;
        private int lastAnimated = -1;
        private OnItemClickListener onItemClickListener;

        interface OnItemClickListener {
            void onItemClick(MsgItem item);
        }

        MsgAdapter(List<MsgItem> list) { this.list = list; }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_mqtt_message_item, parent, false);
            return new VH(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull VH h, @SuppressLint("RecyclerView") int position) {
            MsgItem item = list.get(position);
            h.tvDirection.setText("接收");
            h.tvDeviceId.setText(item.deviceId);
            h.tvTopic.setText(item.topic);
            h.tvPayload.setText(item.payload);
            h.tvTime.setText(item.time);

            h.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            });

            if (position > lastAnimated) {
                AnimationHandler.setItemAnimation(
                        h.itemView.getContext(), h.itemView, Math.min(position, 5));
                lastAnimated = position;
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDirection, tvDeviceId, tvTopic, tvPayload, tvTime;
            VH(View v) {
                super(v);
                tvDirection = v.findViewById(R.id.tv_msg_direction);
                tvDeviceId = v.findViewById(R.id.tv_msg_device_id);
                tvTopic = v.findViewById(R.id.tv_msg_topic);
                tvPayload = v.findViewById(R.id.tv_msg_payload);
                tvTime = v.findViewById(R.id.tv_msg_time);
            }
        }
    }
}