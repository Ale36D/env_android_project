package com.example.environmentview.DialogFragment.ManageDevice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ManageDeviceDialog extends BaseBottomSheetDialogFragment {

    private EditText et_device_id;
    private EditText et_device_name;
    private MaterialButton btn_add_device;
    private RecyclerView rv_device_list;
    private DeviceListAdapter deviceListAdapter;

    private boolean showAddConfig = true;

    public interface OnDeviceListChangedListener {
        void onChanged();
    }

    public interface ChangedViewListener{
        void onChanged();
    }

    private OnDeviceListChangedListener listener;

    public void setShowAddConfig(boolean show) {
        this.showAddConfig = show;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_manage_device_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 找到配置卡片并按需隐藏
        MaterialCardView configCard = view.findViewById(R.id.card_add_device);
        if (!showAddConfig && configCard != null) {
            configCard.setVisibility(View.GONE);
        }

        et_device_id   = view.findViewById(R.id.et_device_id);
        et_device_name = view.findViewById(R.id.et_device_name);
        btn_add_device = view.findViewById(R.id.btn_add_device);
        rv_device_list = view.findViewById(R.id.rv_device_list);

        List<DeviceManager.DeviceEntry> list = DeviceManager.getDeviceList();
        deviceListAdapter = new DeviceListAdapter(list, this::onDeviceDeleted);
        rv_device_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_device_list.setAdapter(deviceListAdapter);

        btn_add_device.setOnClickListener(v -> {
            String id   = et_device_id.getText().toString().trim();
            String name = et_device_name.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(requireContext(), "请输入设备ID", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = DeviceManager.addDevice(id, name);
            if (success) {
                et_device_id.setText("");
                et_device_name.setText("");
                deviceListAdapter.notifyItemInserted(DeviceManager.getDeviceList().size() - 1);
                if (listener != null) listener.onChanged();
                Toast.makeText(requireContext(), "添加成功：" + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "该设备ID已存在", Toast.LENGTH_SHORT).show();
            }
        });
        // 弹窗内卡片动画
        if (configCard != null && configCard.getVisibility() == View.VISIBLE) {
            AnimationHandler.setItemAnimation(requireContext(), configCard, 0);
        }
        AnimationHandler.setItemAnimation(requireContext(), rv_device_list, 1);
    }


    public void setOnDeviceListChangedListener(OnDeviceListChangedListener listener) {
        this.listener = listener;
    }

    private void onDeviceDeleted(int position, String deviceId) {
        DeviceManager.removeDevice(deviceId);
        deviceListAdapter.notifyItemRemoved(position);
        MqttRepository.removeDevice(deviceId);
        if (listener != null) listener.onChanged();
        Toast.makeText(requireContext(), "已删除：" + deviceId, Toast.LENGTH_SHORT).show();
    }
}