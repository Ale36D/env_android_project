package com.example.environmentview.DialogFragment.LoginMqttConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.AppConfig.AppConfig;
import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.Info.MqttInfoList;
import com.example.environmentview.MqttTools.MqttInfo;
import com.example.environmentview.R;
import com.google.android.material.card.MaterialCardView;


public class LoginMqttConfigDialog extends BaseBottomSheetDialogFragment {
//    private ConfigInfoListener listener;
//    public void setLoginConfigMqttInfoListener(ConfigInfoListener listener) {
//        this.listener = listener;
//    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // 加载刚才写的布局
        return inflater.inflate(R.layout.layout_login_config_mqtt_info_sheet, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialCardView cb_config_mqtt_card = view.findViewById(R.id.cb_config_mqtt_card);

        // 在这里通过 view.findViewById 设置点击事件
        EditText etBroker = view.findViewById(R.id.et_mqtt_broker);
        EditText etClientId = view.findViewById(R.id.et_mqtt_client_id);
        EditText etPublishTopic = view.findViewById(R.id.et_mqtt_publish_topic);
        EditText etSubscribeTopic = view.findViewById(R.id.et_mqtt_subscribe_topic);
        Button btnSetMtConfig = view.findViewById(R.id.btn_set_mqtt_config);

        // 将保存到本地的数据读取出来
        etBroker.setText(AppConfig.getBroker());
        etClientId.setText(AppConfig.getClientId());
        etPublishTopic.setText(AppConfig.getPublish());
        etSubscribeTopic.setText(AppConfig.getSubscribe());

        cb_config_mqtt_card.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.login_card_anim));

        btnSetMtConfig.setOnClickListener(v ->{

            String broker = etBroker.getText().toString().trim();
            String clientId = etClientId.getText().toString().trim();
            String pub = etPublishTopic.getText().toString().trim();
            String sub = etSubscribeTopic.getText().toString().trim();

            MqttInfo info = MqttInfoList.getMqttInfo();

            if(info == null){
                info = new MqttInfo("", "", "", "", "", "");
                MqttInfoList.setMqttInfo("", "", "", "", "", "");
            }

            // 将信息写入到全局变量里面
            info.setBroker(broker);
            info.setClientID(clientId);
            info.setPublish(pub);
            info.setSubscribe(sub);

            // 持久化保存到本地
            AppConfig.setBroker(broker);
            AppConfig.setClientId(clientId);
            AppConfig.setSubscribe(sub);
            AppConfig.setPublish(pub);


            dismiss();
        });
    }


}
