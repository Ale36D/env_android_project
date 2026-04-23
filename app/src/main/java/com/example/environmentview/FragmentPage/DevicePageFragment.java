package com.example.environmentview.FragmentPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Adapter.MqttRecylerViewAdapter.SensorPageAdapter;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Event.SensorDataEventHandler;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DevicePageFragment extends Fragment {

    private static final String ARG_DEVICE_ID = "device_id";
    private String deviceId;

    private RecyclerView rvAllData;
    private SensorPageAdapter sensorPageAdapter;
    private TextView tvDevStatus, tvDevTag, tvUpdateTimeMsg;
    private ImageView ivDevStatus;

    // ★ 新增：空状态控件
    private LinearLayout llEmptyData;
    private TextView tvEmptyTitle, tvEmptySubtitle;

    public static DevicePageFragment newInstance(String deviceId) {
        DevicePageFragment fragment = new DevicePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_ID, deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceId = getArguments().getString(ARG_DEVICE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_all_sensor_data_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAllData = view.findViewById(R.id.rv_all_data);
        tvDevStatus = view.findViewById(R.id.tv_dev_status);
        tvDevTag = view.findViewById(R.id.tv_dev_tag);
        tvUpdateTimeMsg = view.findViewById(R.id.tv_update_time_msg);
        ivDevStatus = view.findViewById(R.id.iv_dev_status);

        llEmptyData = view.findViewById(R.id.ll_empty_data);
        tvEmptyTitle = view.findViewById(R.id.tv_empty_title);
        tvEmptySubtitle = view.findViewById(R.id.tv_empty_subtitle);

        this.initRecyclerView();
        this.updateDeviceInfo();

        // 初始化时立即检查一次是否有数据
        this.checkAndShowData();

        MqttRepository.getAllDeviceData().observe(getViewLifecycleOwner(), allData -> {
            if (allData == null) return;
            MqttDataMap data = allData.get(deviceId);
            if (data != null) {
                rvAllData.setVisibility(View.VISIBLE);
                llEmptyData.setVisibility(View.GONE);
                refreshSensorPage(data);
                updateDeviceStatusCard(data);
            }
        });

        MqttDeviceStatusManager.getStatusLiveData().observe(getViewLifecycleOwner(), statusMap -> {
            this.updateDeviceInfo();
            this.updateEmptyState();
        });
    }




    private void initRecyclerView() {
        sensorPageAdapter = new SensorPageAdapter(requireContext());

        GridLayoutManager glm = new GridLayoutManager(requireContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = sensorPageAdapter.getItemViewType(position);
                if (type == 0 || type == 4) return 2;
                return 1;
            }
        });
        rvAllData.setLayoutManager(glm);
        rvAllData.setAdapter(sensorPageAdapter);
        rvAllData.setNestedScrollingEnabled(true);

        sensorPageAdapter.setOnRgbSendListener(rgb -> {
            if (deviceId == null) return;
            Map<String, Object> body = new HashMap<>();
            body.put("rgb", new int[]{rgb[0], rgb[1], rgb[2]});
            MqttCommandSender.sendCommand(deviceId, 1, body);
        });

        MqttDataMap data = MqttRepository.getDeviceData(deviceId);
        if (data != null) {
            refreshSensorPage(data);
            updateDeviceStatusCard(data);
        }
    }



    private void refreshSensorPage(MqttDataMap data) {
        List<SensorPageAdapter.SectionGroup> groups = new ArrayList<>();

        List<MqttDataModel> actuatorList = SensorDataEventHandler.relayMapToList(data);
        if (!actuatorList.isEmpty()) {
            groups.add(new SensorPageAdapter.SectionGroup("执行器", 1, actuatorList));
        }

        List<MqttDataModel> sensorList = SensorDataEventHandler.sensorMapToList(data);
        if (!sensorList.isEmpty()) {
            groups.add(new SensorPageAdapter.SectionGroup("传感器", 2, sensorList));
        }

        List<MqttDataModel> rgbList = SensorDataEventHandler.rgbMapToList(data);
        if (!rgbList.isEmpty()) {
            groups.add(new SensorPageAdapter.SectionGroup("RGB", 3, rgbList));
        }

        sensorPageAdapter.setGroups(groups);
    }

    @SuppressLint("SetTextI18n")
    private void updateDeviceInfo() {
        if (deviceId == null) return;

        String displayName = DeviceManager.getDisplayName(deviceId);
        tvDevTag.setText("设备标识: " + deviceId + "(" + displayName + ")");

        boolean isOnline = MqttDeviceStatusManager.isDeviceOnline(deviceId);
        if (isOnline) {
            tvDevStatus.setText("运行状态: 在线");
            tvDevStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            ivDevStatus.setImageResource(R.drawable.online);
        } else {
            tvDevStatus.setText("运行状态: 离线");
            tvDevStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
            ivDevStatus.setImageResource(R.drawable.offline);
        }
    }
    private void checkAndShowData() {
        MqttDataMap data = MqttRepository.getDeviceData(deviceId);
        if (data != null && hasAnyData(data)) {
            // 有数据：显示列表，隐藏空状态
            rvAllData.setVisibility(View.VISIBLE);
            llEmptyData.setVisibility(View.GONE);
            refreshSensorPage(data);
            updateDeviceStatusCard(data);
        } else {
            // 无数据：隐藏列表，显示空状态
            rvAllData.setVisibility(View.GONE);
            llEmptyData.setVisibility(View.VISIBLE);
            updateEmptyState();
            updateDeviceInfo();
            tvUpdateTimeMsg.setText("暂无数据");
        }
    }



    private void updateEmptyState() {
        if (llEmptyData.getVisibility() != View.VISIBLE) return;

        boolean isOnline = MqttDeviceStatusManager.isDeviceOnline(deviceId);
        if (isOnline) {
            tvEmptyTitle.setText("等待设备上传数据…");
            tvEmptySubtitle.setText("设备已在线，但尚未收到传感器数据");
        } else {
            tvEmptyTitle.setText("设备离线，暂无数据");
            tvEmptySubtitle.setText("请检查设备是否已开启并连接网络");
        }
    }
    private boolean hasAnyData(MqttDataMap data) {
        boolean hasRelay = data.getRelayMap() != null && !data.getRelayMap().isEmpty();
        boolean hasSensor = data.getSensorMap() != null && !data.getSensorMap().isEmpty();
        boolean hasRgb = data.getRgbMap() != null && !data.getRgbMap().isEmpty();
        return hasRelay || hasSensor || hasRgb;
    }

    @SuppressLint("SetTextI18n")
    private void updateDeviceStatusCard(MqttDataMap data) {
        updateDeviceInfo();
        boolean hasData = data.getRelayMap() != null && !data.getRelayMap().isEmpty();
        if (hasData) {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            tvUpdateTimeMsg.setText(currentTime);
        } else {
            tvUpdateTimeMsg.setText("暂无数据");
        }
    }
}