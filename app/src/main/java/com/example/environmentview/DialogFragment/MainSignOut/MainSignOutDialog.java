package com.example.environmentview.DialogFragment.MainSignOut;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.R;


public class MainSignOutDialog extends BaseBottomSheetDialogFragment {
    private SignOutListener listener;

    public void setOnLogoutListener(SignOutListener listener) {
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // 加载刚才写的布局
        return inflater.inflate(R.layout.layout_main_sign_out_sheet, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 在这里通过 view.findViewById 设置点击事件
        view.findViewById(R.id.iv_sign_out).setOnClickListener(v -> {
            if(listener != null){
                listener.onSignOut();
            }
            dismiss(); // 点击后关闭面板
        });
    }
}
