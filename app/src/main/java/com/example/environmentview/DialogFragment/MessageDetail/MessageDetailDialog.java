package com.example.environmentview.DialogFragment.MessageDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.R;

public class MessageDetailDialog extends BaseBottomSheetDialogFragment {

    private static final String ARG_DEVICE_ID = "device_id";
    private static final String ARG_TOPIC = "topic";
    private static final String ARG_PAYLOAD = "payload";
    private static final String ARG_TIME = "time";

    public static MessageDetailDialog newInstance(String deviceId, String topic, String payload, String time) {
        MessageDetailDialog dialog = new MessageDetailDialog();
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_ID, deviceId);
        args.putString(ARG_TOPIC, topic);
        args.putString(ARG_PAYLOAD, payload);
        args.putString(ARG_TIME, time);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_message_detail_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // include 复用的 list_mqtt_message_item 内的控件
        TextView tvDirection = view.findViewById(R.id.tv_msg_direction);
        TextView tvDeviceId = view.findViewById(R.id.tv_msg_device_id);
        TextView tvTopic = view.findViewById(R.id.tv_msg_topic);
        TextView tvPayload = view.findViewById(R.id.tv_msg_payload);
        TextView tvTime = view.findViewById(R.id.tv_msg_time);

        // 移除列表中的行数限制，弹窗内显示完整报文
        tvPayload.setMaxLines(Integer.MAX_VALUE);
        tvPayload.setEllipsize(null);
        tvPayload.setTextIsSelectable(true);


        Bundle args = getArguments();
        if (args != null) {
            tvDirection.setText("接收");
            tvDeviceId.setText(args.getString(ARG_DEVICE_ID, ""));
            tvTopic.setText(args.getString(ARG_TOPIC, ""));
            tvPayload.setText(args.getString(ARG_PAYLOAD, ""));
            tvTime.setText(args.getString(ARG_TIME, ""));
        }
    }
}