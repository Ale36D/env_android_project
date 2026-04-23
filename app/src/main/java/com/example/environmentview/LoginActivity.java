package com.example.environmentview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.Event.LoginEventHandler;
import com.example.environmentview.Info.MqttInfoList;
import com.google.android.material.card.MaterialCardView;



public class LoginActivity extends AppCompatActivity {


    public Button btn_login;
    public ImageButton ib_bottom_bar;
    public TextView tv_forget_password, tv_get_account;
    public EditText et_account, et_password;
    public CheckBox cb_auto_login;
    public MaterialCardView cd_login_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.objInit();
        this.viewInit();

    }

    private void objInit() {
        AppConfig.init(this);
        MqttInfoList.setMqttInfo(
                AppConfig.getBroker(),
                AppConfig.getClientId(),
                AppConfig.getUsername(),
                AppConfig.getPassword(),
                AppConfig.getSubscribe(),
                AppConfig.getPublish()
        );
        LoginEventHandler.setMqttClientManager(getApplicationContext(), this);
//        Toast.makeText(this,
//                MqttInfoList.getMqttInfo().getBroker() + "\n"+
//                        MqttInfoList.getMqttInfo().getClientID() + "\n"+
//                        MqttInfoList.getMqttInfo().getUsername() + "\n"+
//                        MqttInfoList.getMqttInfo().getPassword() + "\n"+
//                        MqttInfoList.getMqttInfo().getPublish() + "\n"+
//                        MqttInfoList.getMqttInfo().getSubscribe() + "\n"
//                , Toast.LENGTH_SHORT).show();
    }

    public void viewInit(){
        this.findView();
        this.setViewInfo();
        this.eventHandler();

    }
    private void setViewInfo() {
        this.cb_auto_login.setChecked(AppConfig.getAutoLogin());
        et_account.setText(AppConfig.getUsername());
        et_password.setText(AppConfig.getPassword());
        cd_login_card.startAnimation(AnimationUtils.loadAnimation(this,R.anim.login_card_anim));
    }

    private void eventHandler() {
        this.btn_login.setOnClickListener(LoginEventHandler.loginClickHandler(this, this));
        this.tv_get_account.setOnClickListener(LoginEventHandler.forgetClickHandler(getSupportFragmentManager()));
        this.cb_auto_login.setOnCheckedChangeListener(LoginEventHandler.autoLoginHandler(this));
        this.ib_bottom_bar.setOnClickListener(LoginEventHandler.bottomBarClickHandler(getSupportFragmentManager()));
//        if(AppConfig.getAutoLogin() && AppConfig.getLogin()){
//            this.btn_login.performClick();
//        }
        this.tv_forget_password.setOnClickListener(LoginEventHandler.sendTestMessage(getApplicationContext(), this));
    }

    private void findView() {
        btn_login = findViewById(R.id.btn_login);
        tv_get_account = findViewById(R.id.tv_get_account);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        cb_auto_login = findViewById(R.id.cb_auto_login);
        ib_bottom_bar = findViewById(R.id.ib_bottom_bar);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.ed_password);
        cd_login_card = findViewById(R.id.cd_login_card);
    }




}