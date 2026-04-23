package com.example.environmentview.Event;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.environmentview.AboutActivity;
import com.example.environmentview.AccountActivity;
import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.DialogFragment.MainSignOut.MainSignOutDialog;
import com.example.environmentview.DialogFragment.ManageDevice.ManageDeviceDialog;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.LoginActivity;
import com.example.environmentview.MqttConfigActivity;
import com.example.environmentview.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class UserInfoHandler {


    public static void showDeviceListDialog(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager) {
        ManageDeviceDialog dialog = new ManageDeviceDialog();
        dialog.setShowAddConfig(false);  // ★ 只显示设备列表
        dialog.show(parentFragmentManager, "device_list");
    }

    public static void logoutAcountAndStopPoll(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager) {
        MainSignOutDialog dialog = new MainSignOutDialog();
        dialog.setOnLogoutListener(() -> {
            // 停止设备状态轮询
            MqttDeviceStatusManager.stopPolling();

            // MQTT 连接
            try {
                if (MqttInfoList.getMyMqttManager() != null) {
                    MqttInfoList.getMyMqttManager().mqttDisconnect();
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }

            // 清除登录状态
            AppConfig.setLogin(false);
            AppConfig.setAutoLogin(true);
            LoginEventHandler.LOGIN_SUCCESS_TAG = false;

            // 跳转登录页
            fragmentActivity.startActivity(new Intent(fragmentActivity, LoginActivity.class));
            fragmentActivity.finish();
        });
        dialog.show(parentFragmentManager, "sign_out");
    }

    public static void jumpMqttConfigActivity(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager) {
        context.startActivity(new Intent(fragmentActivity, MqttConfigActivity.class));
        fragmentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void jumpAboutActivity(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager) {
        context.startActivity(new Intent(fragmentActivity, AboutActivity.class));
        fragmentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public static void jumpAccountActivity(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager) {
        context.startActivity(new Intent(fragmentActivity, AccountActivity.class));
        fragmentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public static void setViewAnimation(View view, FragmentActivity fragmentActivity, Context context) {
        MainEventHandler.setUserAnimationView(fragmentActivity, context, view);
    }

    public static void updateDeviceStats(FragmentActivity fragmentActivity, Context context, FragmentManager parentFragmentManager, View view) {
        TextView tv_device_count, tv_online_count, tv_offline_count;

        tv_device_count = view.findViewById(R.id.tv_device_count);
        tv_online_count = view.findViewById(R.id.tv_online_count);
        tv_offline_count = view.findViewById(R.id.tv_offline_count);

        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();
        int total = deviceList != null ? deviceList.size() : 0;
        int online = 0;
        int offline = 0;

        if (deviceList != null) {
            for (DeviceManager.DeviceEntry device : deviceList) {
                if (MqttDeviceStatusManager.isDeviceOnline(device.id)) {
                    online++;
                } else {
                    offline++;
                }
            }
        }

        tv_device_count.setText(String.valueOf(total));
        tv_online_count.setText(String.valueOf(online));
        tv_offline_count.setText(String.valueOf(offline));
    }


}
