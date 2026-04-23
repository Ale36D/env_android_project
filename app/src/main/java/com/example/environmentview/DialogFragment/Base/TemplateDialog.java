package com.example.environmentview.DialogFragment.Base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.R;

public class TemplateDialog extends BaseBottomSheetDialogFragment{
    private TextView tv_description;
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.layout_test_dialog_sheet,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button close = view.findViewById(R.id.bt_close);
        tv_description = view.findViewById(R.id.tv_description);
        close.setOnClickListener(v -> dismiss());
    }
    public void setDescription(String description){
//        tv_description.setText(description);
    }


}
