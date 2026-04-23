package com.example.environmentview;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.Map;

public class DeviceDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvIcon, tvName, tvTag, tvValue;
    private TextView tvDeviceName, tvDeviceId, tvOnlineStatus, tvDataType;
    private View viewIconBg;
    private MaterialCardView cardIcon, cardInfo, cardSwitch, cd_device_detail_title_card;
    private Switch scSwitch;

    private String deviceId, itemTag, itemType; // type: "actuator" / "sensor"
    private int iconColor, iconBgColor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取传入参数
        deviceId = getIntent().getStringExtra("device_id");
        itemTag = getIntent().getStringExtra("item_tag");
        itemType = getIntent().getStringExtra("item_type");
        String name = getIntent().getStringExtra("item_name");
        String icon = getIntent().getStringExtra("item_icon");
        String value = getIntent().getStringExtra("item_value");
        iconColor = getIntent().getIntExtra("icon_color", 0xFF3498DB);
        iconBgColor = getIntent().getIntExtra("icon_bg_color", 0xFFEBF5FB);

        // 绑定控件
        tvTitle = findViewById(R.id.tv_detail_title);
        tvIcon = findViewById(R.id.tv_detail_icon);
        tvName = findViewById(R.id.tv_detail_name);
        tvTag = findViewById(R.id.tv_detail_tag);
        tvValue = findViewById(R.id.tv_detail_value);
        viewIconBg = findViewById(R.id.view_detail_icon_bg);
        tvDeviceName = findViewById(R.id.tv_detail_device_name);
        tvDeviceId = findViewById(R.id.tv_detail_device_id);
        tvOnlineStatus = findViewById(R.id.tv_detail_online_status);
        tvDataType = findViewById(R.id.tv_detail_data_type);
        cd_device_detail_title_card = findViewById(R.id.cd_device_detail_title_card);
        cardIcon = findViewById(R.id.card_icon);
        cardInfo = findViewById(R.id.card_info);
        cardSwitch = findViewById(R.id.card_switch);
        scSwitch = findViewById(R.id.sc_detail_switch);

        ImageButton btnBack = findViewById(R.id.iv_btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 设置图标字体
        Typeface materialFont = Typeface.createFromAsset(getAssets(), "fonts/material.otf");
        tvIcon.setTypeface(materialFont);

        // 填充静态信息
        tvTitle.setText(name != null ? name : "设备详情");
        tvName.setText(name);
        tvTag.setText("Tag: " + itemTag);
        tvIcon.setText(icon);
        tvIcon.setTextColor(iconColor);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(iconBgColor);
        viewIconBg.setBackground(bg);

        // 设备信息
        String displayName = DeviceManager.getDisplayName(deviceId);
        tvDeviceName.setText(displayName);
        tvDeviceId.setText(deviceId);
        tvDataType.setText("actuator".equals(itemType) ? "执行器" : "传感器");

        // 执行器显示开关卡片
        if ("actuator".equals(itemType)) {
            cardSwitch.setVisibility(View.VISIBLE);
        }

        // 填充初始值
        updateValue(value);
        updateOnlineStatus();

        // 开关控制
        scSwitch.setOnCheckedChangeListener((btn, checked) -> {
            if (deviceId == null || itemTag == null) return;

            MqttDataMap data = MqttRepository.getDeviceData(deviceId);
            if (data == null || data.getRelayMap() == null) return;

            Map<String, Object> relayState = new HashMap<>();
            for (Map.Entry<String, String> entry : data.getRelayMap().entrySet()) {
                relayState.put(entry.getKey(),
                        entry.getKey().equals(itemTag) ?
                                (checked ? 1 : 0) :
                                Integer.parseInt(entry.getValue()));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("relay", relayState);
            MqttCommandSender.sendCommand(deviceId, 0, body);
        });

        // 实时监听数据变化
        MqttRepository.getAllDeviceData().observe(this, allData -> {
            if (allData == null || deviceId == null) return;
            MqttDataMap data = allData.get(deviceId);
            if (data == null) return;

            if ("actuator".equals(itemType)) {
                String val = data.getRelayMap() != null ? data.getRelayMap().get(itemTag) : null;
                if (val != null) {
                    updateValue("1".equals(val) ? "已开启" : "已关闭");
                    scSwitch.setOnCheckedChangeListener(null);
                    scSwitch.setChecked("1".equals(val));
                    scSwitch.setOnCheckedChangeListener((btn, checked) -> {
                        MqttDataMap d = MqttRepository.getDeviceData(deviceId);
                        if (d == null || d.getRelayMap() == null) return;
                        Map<String, Object> relayState = new HashMap<>();
                        for (Map.Entry<String, String> e : d.getRelayMap().entrySet()) {
                            relayState.put(e.getKey(),
                                    e.getKey().equals(itemTag) ? (checked ? 1 : 0) : Integer.parseInt(e.getValue()));
                        }
                        Map<String, Object> body = new HashMap<>();
                        body.put("relay", relayState);
                        MqttCommandSender.sendCommand(deviceId, 0, body);
                    });
                }
            } else {
                String val = data.getSensorMap() != null ? data.getSensorMap().get(itemTag) : null;
                if (val != null) {
                    updateValue(val);
                }
            }
        });

        // 监听在线状态
        MqttDeviceStatusManager.getStatusLiveData().observe(this, statusMap -> {
            updateOnlineStatus();
        });

        // 动画
        AnimationHandler.setItemAnimation(this, cd_device_detail_title_card, 0);
        AnimationHandler.setItemAnimation(this, cardIcon, 1);
        AnimationHandler.setItemAnimation(this, cardInfo, 2);

        if (cardSwitch.getVisibility() == View.VISIBLE) {
            AnimationHandler.setItemAnimation(this, cardSwitch, 3);
        }
    }

    private void updateValue(String value) {
        if (value != null) {
            tvValue.setText(value);
        }
    }

    private void updateOnlineStatus() {
        boolean isOnline = MqttDeviceStatusManager.isDeviceOnline(deviceId);
        if (isOnline) {
            tvOnlineStatus.setText("在线");
            tvOnlineStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvOnlineStatus.setText("离线");
            tvOnlineStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}