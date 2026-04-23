package com.example.environmentview.FragmentPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.environmentview.AboutActivity;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.DialogFragment.MainSignOut.MainSignOutDialog;
import com.example.environmentview.DialogFragment.ManageDevice.ManageDeviceDialog;
import com.example.environmentview.Event.LoginEventHandler;
import com.example.environmentview.Event.MainEventHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.SensorDataEventHandler;
import com.example.environmentview.Event.UserInfoHandler;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.LoginActivity;
import com.example.environmentview.MqttConfigActivity;
import com.example.environmentview.R;
import com.google.android.material.card.MaterialCardView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class UserInfoFragment extends Fragment {

    private TextView tvUserName;
    private LinearLayout menuAccount, menuDevices, menuMqtt, menuAbout;

    private Button btn_logout;
    private View userView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userView = view;
        MainEventHandler.setUserView(userView);

        this.viewInit(view);
        this.viewInitListener();
        this.viewAnimationSetting(view);
        this.updateStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainEventHandler.setUserAnimationView(requireActivity(), requireContext(), userView);
        updateStats();
    }

    private void viewInit(View view) {
        btn_logout = view.findViewById(R.id.btn_logout);
        tvUserName = view.findViewById(R.id.tv_user_name);

        menuAccount = view.findViewById(R.id.menu_account);
        menuDevices = view.findViewById(R.id.menu_devices);
        menuMqtt = view.findViewById(R.id.menu_mqtt);
        menuAbout = view.findViewById(R.id.menu_about);

        // 设置用户名
        String username = AppConfig.getUsername();
        if (username != null && !username.isEmpty()) {
            tvUserName.setText(username);
        }
    }

    private void viewInitListener() {
        menuDevices.setOnClickListener(v -> {
            UserInfoHandler.showDeviceListDialog(requireActivity(), requireContext(), getParentFragmentManager());
        });

        btn_logout.setOnClickListener(v -> {
            UserInfoHandler.logoutAcountAndStopPoll(requireActivity(), requireContext(), getParentFragmentManager());
        });

        menuAccount.setOnClickListener(v -> {
            UserInfoHandler.jumpAccountActivity(requireActivity(), requireContext(), getParentFragmentManager());
        });

        menuMqtt.setOnClickListener(v -> {
            UserInfoHandler.jumpMqttConfigActivity(requireActivity(), requireContext(), getParentFragmentManager());
        });

        menuAbout.setOnClickListener(v -> {
            UserInfoHandler.jumpAboutActivity(requireActivity(), requireContext(), getParentFragmentManager());
        });
    }

    private void viewAnimationSetting(View view) {
        UserInfoHandler.setViewAnimation(view, requireActivity(), requireContext());
    }

    private void updateStats() {
        UserInfoHandler.updateDeviceStats(requireActivity(), requireContext(), getParentFragmentManager(), userView);
    }
}