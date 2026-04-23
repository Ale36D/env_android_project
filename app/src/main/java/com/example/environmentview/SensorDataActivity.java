package com.example.environmentview;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Adapter.MqttRecylerViewAdapter.ExpandableDeviceAdapter;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Event.SensorDataEventHandler;
import com.example.environmentview.Info.DeviceManager;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SensorDataActivity extends AppCompatActivity {

    private RecyclerView rvDataList;

    MaterialCardView cd_sensor_title_card;
    private TextView tvSensorType, tvNowDate;
    private String type;
    private ExpandableDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensor_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.viewInit();

        // ★ 监听所有设备数据变化，实时刷新 ★
        MqttRepository.getAllDeviceData().observe(this, allData -> {
            if (allData != null) {
                rvDataList.post(this::refreshData);
            }
        });
    }

    private void viewInit() {
        this.findViewID();
        this.setAdapter();
        this.viewAnimationSetting();
    }

    private void setAdapter() {
        String date = new SimpleDateFormat("EEE, dd MMMM yyyy", Locale.CHINA).format(new Date());
        tvNowDate.setText("Update: " + date);

        type = getIntent().getStringExtra("type");
        if (type == null) type = "sensor";

        if ("actuator".equals(type)) {
            tvSensorType.setText("所有执行器");
        } else {
            tvSensorType.setText("所有传感器");
        }

        // 初始化 Adapter 和 LayoutManager
        adapter = new ExpandableDeviceAdapter(this, type);
        GridLayoutManager glm = new GridLayoutManager(this, 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 分组头占满整行，子项各占1格
                return adapter.getItemViewType(position) == 0 ? 2 : 1;
            }
        });
        rvDataList.setLayoutManager(glm);
        rvDataList.setAdapter(adapter);
        // 先加载一次数据
        refreshData();
    }


    private void findViewID() {
        tvSensorType = findViewById(R.id.tv_sensor_type);
        tvNowDate = findViewById(R.id.tv_now_date);
        rvDataList = findViewById(R.id.rv_data_list);

        cd_sensor_title_card = findViewById(R.id.cd_sensor_title_card);

        ImageButton btnBack = findViewById(R.id.iv_btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void viewAnimationSetting() {
        // 添加动画
        AnimationHandler.setTopAnimation(this, tvSensorType);
        AnimationHandler.setTopAnimation(this, cd_sensor_title_card);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * 构建分组数据并刷新列表
     */
    private void refreshData() {
        List<ExpandableDeviceAdapter.DeviceGroup> groups = new ArrayList<>();

        for (DeviceManager.DeviceEntry device : DeviceManager.getDeviceList()) {
            // 不在线的跳过
            if (!MqttDeviceStatusManager.isDeviceOnline(device.id)) continue;

            MqttDataMap data = MqttRepository.getDeviceData(device.id);
            if (data == null) continue;

            List<MqttDataModel> items;
            if ("actuator".equals(type)) {
                items = SensorDataEventHandler.relayMapToList(data);
            } else {
                items = SensorDataEventHandler.sensorMapToList(data);
            }

            if (!items.isEmpty()) {
                groups.add(new ExpandableDeviceAdapter.DeviceGroup(device.id, device.name, items));
            }
        }

        adapter.refreshAllGroups(groups);
    }
}