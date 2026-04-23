package com.example.environmentview.FragmentPage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.HomeDeviceAdapter.HomeDeviceCardAdapter;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.DialogFragment.ManageDevice.ManageDeviceDialog;
import com.example.environmentview.Event.MainEventHandler;
import com.example.environmentview.Event.Process.MqttJsonParsing.MqttDataMap;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.MainActivity;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static String TAG = "HOME_FRAGMENT";
    private ImageButton ib_bottom_bar;
    private TextView tv_one_kit, tv_username, tv_now_date;
    private MaterialCardView cb_main_weather_card, cb_main_actuator_card, cb_main_sensor_card;
    private FrameLayout loading_mask;
    private View homeView;

    // 新增控件, 设备信息卡片
    private RecyclerView rv_home_device_list;
    private LinearLayout ll_empty_device;
    private TextView tv_device_count;
    private TextView tv_all_devices;
    private HomeDeviceCardAdapter deviceCardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeView = view;
        MainEventHandler.setHomeView(view);
        this.viewInit(view);
        this.viewInitListener();
        this.viewContentSetting(view);
        this.viewAnimationSetting(view);
        this.initDeviceList();

        MqttRepository.getMqttData().observe(getViewLifecycleOwner(), data -> {
            if(data == null) return;
            MainEventHandler.setDevStatusView(requireActivity(), requireContext(), view, data);
            Log.w(TAG, String.valueOf(data.getRelayMap().size()));
        });

        // 监听设备列表变化 添加/删除都会触发
        MqttRepository.getDeviceListData().observe(getViewLifecycleOwner(), deviceIds -> {
            refreshDeviceList();  // 刷新首页设备卡片
        });

        // 监听MQTT数据变化 - 更新设备在线状态
        MqttRepository.getMqttData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            MainEventHandler.setDevStatusView(requireActivity(), requireContext(), view, data);

            // 刷新设备卡片的在线状态
            if (deviceCardAdapter != null) {
                deviceCardAdapter.notifyDataSetChanged();
            }
        });

        // 监听设备列表变化（添加/删除设备）
        MqttRepository.getDeviceListData().observe(getViewLifecycleOwner(), deviceIds -> {
            refreshDeviceList();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MainEventHandler.setHomeAnimation(requireActivity(), requireContext(), homeView);
        refreshDeviceList();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void viewAnimationSetting(View view) {
        MainEventHandler.setHomeAnimation(requireActivity(), requireContext(), view);
    }

    private void viewContentSetting(View view) {
        MainEventHandler.setOneKitToView(requireActivity(), requireContext(), tv_one_kit);
        MainEventHandler.setUserNameToView(requireActivity(), requireContext(), tv_username);
        MainEventHandler.setNowDateToView(requireActivity(), requireContext(), tv_now_date);
        MainEventHandler.setWeatherToView(requireActivity(), requireContext(), view);
    }

    public void viewInit(View view){
        ib_bottom_bar = view.findViewById(R.id.ib_bottom_bar);
        cb_main_weather_card = view.findViewById(R.id.cb_main_weather_card);
        cb_main_actuator_card = view.findViewById(R.id.cb_main_actuator_card);
        cb_main_sensor_card = view.findViewById(R.id.cb_main_sensor_card);
        tv_one_kit = view.findViewById(R.id.tv_one_kit);
        tv_username = view.findViewById(R.id.tv_username);
        tv_now_date = view.findViewById(R.id.tv_now_date);
        loading_mask = view.findViewById(R.id.loading_mask);

        // 设备信息卡片控件
        rv_home_device_list = view.findViewById(R.id.rv_home_device_list);
        ll_empty_device = view.findViewById(R.id.ll_empty_device);
        tv_device_count = view.findViewById(R.id.tv_device_count);
        tv_all_devices = view.findViewById(R.id.tv_all_devices);
    }
    public void viewInitListener(){
        Activity activity = requireActivity();
        FragmentManager fragmentManager = getParentFragmentManager();
        cb_main_actuator_card.setOnClickListener(
                MainEventHandler.relayClickHandler(activity));
        cb_main_sensor_card.setOnClickListener(
                MainEventHandler.SensorClickHandler(activity));
        ib_bottom_bar.setOnClickListener(
                MainEventHandler.bottomBarClickHandler(getParentFragmentManager(), activity));
        cb_main_weather_card.setOnClickListener(
                MainEventHandler.openWeatherActivity(getParentFragmentManager(), activity)
        );
        tv_all_devices.setOnClickListener(v -> {
            MainEventHandler.setDeviceCardInfoHandler(fragmentManager, this);
        });
    }
    private void initDeviceList() {
        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();

        deviceCardAdapter = new HomeDeviceCardAdapter(requireContext(), new ArrayList<>(deviceList));
        rv_home_device_list.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rv_home_device_list.setAdapter(deviceCardAdapter);

        // 点击设备卡片
        deviceCardAdapter.setOnDeviceClickListener((device, position) -> {
            MainEventHandler.jumpToSensorFramement();
        });

        updateEmptyState(deviceList);
    }

    public void refreshDeviceList() {
        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();
        if (deviceCardAdapter != null) {
            deviceCardAdapter.updateDeviceList(new ArrayList<>(deviceList));
        }
        updateEmptyState(deviceList);
    }

    private void updateEmptyState(List<DeviceManager.DeviceEntry> deviceList) {
        if (deviceList == null || deviceList.isEmpty()) {
            ll_empty_device.setVisibility(View.VISIBLE);
            rv_home_device_list.setVisibility(View.GONE);
            tv_device_count.setText("0 台设备");
        } else {
            ll_empty_device.setVisibility(View.GONE);
            rv_home_device_list.setVisibility(View.VISIBLE);
            tv_device_count.setText(deviceList.size() + " 台设备");
        }
    }
}