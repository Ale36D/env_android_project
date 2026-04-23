package com.example.environmentview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.environmentview.Event.MainEventHandler;
import com.example.environmentview.Event.Process.MqttDeviceStatusManager;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.FragmentPage.HomeFragment;
import com.example.environmentview.MqttTools.MqttClientManager;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.MqttTools.MqttInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {


    public BottomNavigationView bottomNav;
    private FrameLayout fl_loading;
    public MqttInfo mqttInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.viewInit();
        this.fl_loading.setVisibility(View.VISIBLE);
        MainEventHandler.initFragmentView(savedInstanceState, this);
        // 绑定消息通知
        MqttDeviceStatusManager.bindActivity(this);
        // 读缓存数据
        MqttRepository.loadCache();
        // 启动设备在线状态查询
        MqttDeviceStatusManager.startPolling();
        this.fl_loading.setVisibility(View.GONE);
    }



    public void viewInit(){
        this.findView();
        this.setViewInfo();
        this.btnHandler();
    }

    private void setViewInfo() {

    }

    private void btnHandler() {
        bottomNav.setOnItemSelectedListener(
            MainEventHandler.createBottomNavListener(this, this));
    }

    private void findView() {
        bottomNav = findViewById(R.id.bottom_nav);
        fl_loading = findViewById(R.id.fl_loading);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}