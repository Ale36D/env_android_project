package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.environmentview.FragmentPage.DevicePageFragment;
import com.example.environmentview.Info.DeviceManager;

import java.util.ArrayList;
import java.util.List;

public class DevicePagerAdapter extends FragmentStateAdapter {

    private List<DeviceManager.DeviceEntry> deviceList = new ArrayList<>();

    public DevicePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void setDeviceList(List<DeviceManager.DeviceEntry> list) {
        this.deviceList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<DeviceManager.DeviceEntry> getDeviceList() {
        return deviceList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DevicePageFragment.newInstance(deviceList.get(position).id);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public long getItemId(int position) {
        return deviceList.get(position).id.hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (DeviceManager.DeviceEntry entry : deviceList) {
            if (entry.id.hashCode() == itemId) return true;
        }
        return false;
    }
}