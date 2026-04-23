package com.example.environmentview.FragmentPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.environmentview.Adapter.MqttRecylerViewAdapter.DevicePagerAdapter;
import com.example.environmentview.Event.MainEventHandler;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Event.SensorDataEventHandler;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.R;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class SensorDataFragment extends Fragment {
    private static final String TAG = "SENSOR_DATA_FRAGMENT";

    private TabLayout tabDevices;
    private ViewPager2 vpDevicePager;
    private ImageView ivBtnManageDevices;
    private LinearLayout llEmptyDevice;
    private TextView tv_now_date;

    private DevicePagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_sensor_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainEventHandler.setAllSensorView(view);

        this.viewInit(view);
        this.viewInitListener();
        this.viewContentSetting(view);
        this.viewAnimationSetting(view);
        this.refreshDeviceList();

        MqttRepository.getDeviceListData().observe(getViewLifecycleOwner(),
                ids -> refreshDeviceList()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDeviceList();
    }

    private void viewAnimationSetting(View view) {
        MainEventHandler.setAllSensorAnimationView(requireActivity(), requireContext(), view);
    }

    private void viewContentSetting(View view) {
        pagerAdapter = new DevicePagerAdapter(this);
        vpDevicePager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabDevices, vpDevicePager, (tab, position) -> {
            List<DeviceManager.DeviceEntry> list = pagerAdapter.getDeviceList();
            if (position < list.size()) {
                tab.setText(DeviceManager.getDisplayName(list.get(position).id));
            }
        }).attach();

        vpDevicePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                List<DeviceManager.DeviceEntry> list = pagerAdapter.getDeviceList();
                if (position < list.size()) {
                    MqttRepository.selectDevice(list.get(position).id);
                }
            }
        });
        SensorDataEventHandler.setNowDateToView(requireActivity(), requireContext(), tv_now_date);
    }

    private void viewInitListener() {
        ivBtnManageDevices.setOnClickListener(v -> {
            SensorDataEventHandler.showManageDeviceDialog(getParentFragmentManager(), this);
        });
    }

    private void viewInit(View view) {
        tabDevices = view.findViewById(R.id.tab_devices);
        vpDevicePager = view.findViewById(R.id.vp_device_pager);
        ivBtnManageDevices = view.findViewById(R.id.iv_btn_manage_devices);
        llEmptyDevice = view.findViewById(R.id.ll_empty_device);
        tv_now_date = view.findViewById(R.id.tv_now_date);
    }

    public void refreshDeviceList() {
        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();

        if (deviceList == null || deviceList.isEmpty()) {
            llEmptyDevice.setVisibility(View.VISIBLE);
            vpDevicePager.setVisibility(View.GONE);
            tabDevices.setVisibility(View.GONE);
        } else {
            llEmptyDevice.setVisibility(View.GONE);
            vpDevicePager.setVisibility(View.VISIBLE);
            tabDevices.setVisibility(View.VISIBLE);

            pagerAdapter.setDeviceList(new ArrayList<>(deviceList));

            String currentId = MqttRepository.getCurrentDeviceId();
            if (currentId != null) {
                for (int i = 0; i < deviceList.size(); i++) {
                    if (deviceList.get(i).id.equals(currentId)) {
                        vpDevicePager.setCurrentItem(i, false);
                        break;
                    }
                }
            } else if (!deviceList.isEmpty()) {
                MqttRepository.selectDevice(deviceList.get(0).id);
            }
        }
    }
}