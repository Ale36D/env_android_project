package com.example.environmentview.DialogFragment.LoginGetAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.R;

public class LoginGetAccountDialog extends BaseBottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // 加载刚才写的布局

        return inflater.inflate(
                R.layout.layout_login_bottom_get_account_sheet,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 在这里通过 view.findViewById 设置点击事件
        view.findViewById(R.id.btn_get_account).setOnClickListener(v -> {
            // 处理相机点击
            Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
            dismiss(); // 点击后关闭面板
        });
    }
}
