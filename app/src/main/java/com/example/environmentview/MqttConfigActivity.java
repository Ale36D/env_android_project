package com.example.environmentview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.MqttTools.MqttClientManager;
import com.example.environmentview.MqttTools.MqttInfo;
import com.example.environmentview.MqttTools.MqttMessageCallback.MqttMessageCallbackInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MqttConfigActivity extends AppCompatActivity {

    private TextView tvConnectStatus, tvBroker, tvClientId, tvSubscribe, tvPublish;
    private TextView tvMsgCount;
    private RecyclerView rvMessages;
    private LinearLayout llEmptyMsg;
    private MaterialButton btnClear;
    private MaterialCardView cd_mqtt_config_title_card, cd_mqtt_config_card, cd_mqtt_messages_card;

    private final List<MqttMessageItem> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 原始回调备份
    private MqttMessageCallbackInterface originalCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mqtt_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadMqttConfig();
        updateConnectStatus();
        setupMessageListener();

        // 动画
        AnimationHandler.setItemAnimation(this, cd_mqtt_config_title_card, 0);
        AnimationHandler.setItemAnimation(this, cd_mqtt_config_card, 1);
        AnimationHandler.setItemAnimation(this, cd_mqtt_messages_card, 2);
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.iv_btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvBroker = findViewById(R.id.tv_cfg_broker);
        tvClientId = findViewById(R.id.tv_cfg_client_id);
        tvSubscribe = findViewById(R.id.tv_cfg_subscribe);
        tvPublish = findViewById(R.id.tv_cfg_publish);
        tvMsgCount = findViewById(R.id.tv_msg_count);
        rvMessages = findViewById(R.id.rv_mqtt_messages);
        llEmptyMsg = findViewById(R.id.ll_empty_msg);
        btnClear = findViewById(R.id.btn_clear_msg);
        cd_mqtt_config_title_card = findViewById(R.id.cd_mqtt_config_title_card);
        cd_mqtt_config_card = findViewById(R.id.cd_mqtt_config_card);
        cd_mqtt_messages_card = findViewById(R.id.cd_mqtt_messages_card);

        messageAdapter = new MessageAdapter(messageList);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messageAdapter);

        btnClear.setOnClickListener(v -> {
            messageList.clear();
            messageAdapter.notifyDataSetChanged();
            updateMessageCount();
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadMqttConfig() {
        MqttInfo info = MqttInfoList.getMqttInfo();
        if (info != null) {
            tvBroker.setText(info.getBroker());
            tvClientId.setText(info.getClientID());
            tvSubscribe.setText(info.getSubscribe());
            tvPublish.setText(info.getPublish());
        } else {
            tvBroker.setText("未配置");
            tvClientId.setText("未配置");
            tvSubscribe.setText("未配置");
            tvPublish.setText("未配置");
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateConnectStatus() {
        boolean connected = MqttClientManager.getInstance().mqttIsConnected();
        if (connected) {
            tvConnectStatus.setText("已连接");
            tvConnectStatus.setTextColor(0xFF27AE60);
        } else {
            tvConnectStatus.setText("未连接");
            tvConnectStatus.setTextColor(0xFFE74C3C);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupMessageListener() {
        // 拦截 MQTT 消息用于展示，同时保留原有处理逻辑
        MqttClientManager manager = MqttClientManager.getInstance();
        if (manager.getMessageHandler() != null) {
            MqttMessageCallbackInterface existingCallback =
                    manager.getMessageHandler().getMessageCallbackInterface();

            manager.getMessageHandler().setMessageCallbackInterface((topic, message) -> {
                // 先执行原有回调
                if (existingCallback != null) {
                    existingCallback.getMessage(topic, message);
                }

                // 添加到报文列表
                mainHandler.post(() -> {
                    String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());

                    // 格式化 JSON
                    String formatted = formatJson(message);

                    messageList.add(0, new MqttMessageItem("接收", topic, formatted, time));

                    // 最多保留 100 条
                    if (messageList.size() > 100) {
                        messageList.remove(messageList.size() - 1);
                    }
                    messageAdapter.notifyDataSetChanged();

                    updateMessageCount();
                    updateConnectStatus();
                });
            });

            // 保存原始回调，退出时恢复
            originalCallback = existingCallback;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateMessageCount() {
        tvMsgCount.setText(messageList.size() + " 条");
        if (messageList.isEmpty()) {
            llEmptyMsg.setVisibility(View.VISIBLE);
            rvMessages.setVisibility(View.GONE);
        } else {
            llEmptyMsg.setVisibility(View.GONE);
            rvMessages.setVisibility(View.VISIBLE);
        }
    }

    private String formatJson(String json) {
        try {
            com.google.gson.JsonElement el = com.google.gson.JsonParser.parseString(json);
            return new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(el);
        } catch (Exception e) {
            return json;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 恢复原始回调
        MqttClientManager manager = MqttClientManager.getInstance();
        if (manager.getMessageHandler() != null && originalCallback != null) {
            manager.getMessageHandler().setMessageCallbackInterface(originalCallback);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ===== 数据模型 =====
    static class MqttMessageItem {
        String direction, topic, payload, time;

        MqttMessageItem(String direction, String topic, String payload, String time) {
            this.direction = direction;
            this.topic = topic;
            this.payload = payload;
            this.time = time;
        }
    }

    // ===== Adapter =====
    static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
        private final List<MqttMessageItem> list;
        private int lastAnimated = -1;

        MessageAdapter(List<MqttMessageItem> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_mqtt_message_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, @SuppressLint("RecyclerView") int position) {
            MqttMessageItem item = list.get(position);
            holder.tvDirection.setText(item.direction);
            holder.tvTopic.setText(item.topic);
            holder.tvPayload.setText(item.payload);
            holder.tvTime.setText(item.time);

            if (position > lastAnimated) {
                AnimationHandler.setItemAnimation(holder.itemView.getContext(), holder.itemView, position);
                lastAnimated = position;
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDirection, tvTopic, tvPayload, tvTime;
            VH(View v) {
                super(v);
                tvDirection = v.findViewById(R.id.tv_msg_direction);
                tvTopic = v.findViewById(R.id.tv_msg_topic);
                tvPayload = v.findViewById(R.id.tv_msg_payload);
                tvTime = v.findViewById(R.id.tv_msg_time);
            }
        }
    }
}