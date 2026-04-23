package com.example.environmentview.Event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.DialogFragment.LoginMqttConfig.LoginMqttConfigDialog;
import com.example.environmentview.DialogFragment.LoginGetAccount.LoginGetAccountDialog;
import com.example.environmentview.Event.Process.MqttMessageProcess;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.MainActivity;
import com.example.environmentview.MqttTools.MqttInfo;
import com.example.environmentview.MqttTools.MqttClientManager;
import com.example.environmentview.MqttTools.MqttMessageCallback.MqttConnectStateInterface;
import com.example.environmentview.NetWorkUtil.NetWorkUtil;
import com.example.environmentview.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginEventHandler {
    public static boolean LOGIN_SUCCESS_TAG = false; // 静态或类成员
    private static String LOGIN_TAG= "LOGIN_TAG";
    private static Intent intent;
    private static MqttClientManager MqttClientManager;
    public static View.OnClickListener loginClickHandler(final Activity activity, final Context context){
        return view -> {
            FrameLayout loadingMask = activity.findViewById(R.id.loading_mask);
            loadingMask.setVisibility(View.VISIBLE);
            try {
                if(NetWorkUtil.isNetworkAvailable(context)) {
                    EditText ed_account = activity.findViewById(R.id.et_account);
                    EditText ed_password = activity.findViewById(R.id.ed_password);
                    Map<String, String> map = new HashMap<>();
                    map.put("broker", AppConfig.getBroker());
                    map.put("clientId", AppConfig.getClientId());
                    map.put("subscribe", AppConfig.getSubscribe());
                    map.put("publish", AppConfig.getPublish());
                    map.put("account", ed_account.getText().toString().trim());
                    map.put("password", ed_password.getText().toString().trim());
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                            Log.w("LOGIN", entry.getValue() + "||" + entry.getKey());
                            Toast.makeText(context, entry.getKey() + "字段为空", Toast.LENGTH_SHORT).show();
                            loadingMask.setVisibility(View.GONE);
                            return;
                        }
                    }

                    MqttInfo info = MqttInfoList.getMqttInfo();

                    info.setUsername(map.get("account"));
                    info.setPassword(map.get("password"));

                    MqttInfoList.getMyMqttManager().setConnectStateInterface(new MqttConnectStateInterface() {
                        @Override
                        public void onConnectSuccess() {
                            if (LOGIN_SUCCESS_TAG)
                                return;
                            if (MqttInfoList.getMyMqttManager().getClient() != null && MqttInfoList.getMyMqttManager().mqttIsConnected()){
                                MqttInfoList.getMyMqttManager().mqttPublish(AppConfig.getSubscribe(), "Android_Dev_Connect");
                                Log.w("LOGIN", "推送在线消息");
                                LOGIN_SUCCESS_TAG = true;
                                activity.runOnUiThread(()->{
                                    loadingMask.setVisibility(View.GONE);
                                    AppConfig.setLogin(true);
                                    AppConfig.setUsername(map.get("account"));
                                    AppConfig.setPassword(map.get("password"));
                                    activity.startActivity(new Intent(context, MainActivity.class));
                                    activity.finish();
                                });
                            }
                        }
                        @Override
                        public void onConnectFailed(String reason) {
                            activity.runOnUiThread(() -> {
                                loadingMask.setVisibility(View.GONE);
                                Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onConnectLost() {
                            activity.runOnUiThread(()->{
                                loadingMask.setVisibility(View.GONE);
                            });
                        }
                    });
                    MqttInfoList.getMyMqttManager().mqttConnect(MqttInfoList.getMqttInfo());
                }
                else{
                    loadingMask.setVisibility(View.GONE);
                    Toast.makeText(context, "没有连接到网络!", Toast.LENGTH_SHORT).show();
                }
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }

        };
    }
    public static View.OnClickListener forgetClickHandler(final FragmentManager fragmentManager){
        return view -> {
            LoginGetAccountDialog loginBottomSheetDialog = new LoginGetAccountDialog();
            loginBottomSheetDialog.show(fragmentManager, "LoginBottomSheetDialog");
        };
    }
    public static CheckBox.OnCheckedChangeListener autoLoginHandler(final Activity activity ) {
        return (buttonView, isChecked) -> {
            AppConfig.setAutoLogin(isChecked);
        };
    }
    public static View.OnClickListener bottomBarClickHandler(final FragmentManager fragmentManager) {
        return view -> {
            LoginMqttConfigDialog loginMqttConfigDialog = new LoginMqttConfigDialog();
            loginMqttConfigDialog.show(fragmentManager, "loginMqttConfigBottomSheetDialog");
        };
    }

    public static void setMqttClientManager(final Context context, final Activity activity) {
        MqttInfoList.setMyMqttManager();
        MqttClientManager = MqttInfoList.getMyMqttManager();
        try {
            MqttClientManager.mqttInit(
                    MqttInfoList.getMqttInfo(),
                    MqttMessageProcess::messageProcess
            );
        }catch (Exception e){
            Log.w(LOGIN_TAG, e);
        }

    }

    public static View.OnClickListener sendTestMessage(Context context, Activity activity) {
        return view->{
            MqttClientManager.mqttPublish(MqttInfoList.getMqttInfo().getPublish(), "Test_Message!");
        };
    }
}
