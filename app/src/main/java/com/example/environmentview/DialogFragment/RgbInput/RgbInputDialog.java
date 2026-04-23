package com.example.environmentview.DialogFragment.RgbInput;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.environmentview.DialogFragment.Base.BaseBottomSheetDialogFragment;
import com.example.environmentview.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

public class RgbInputDialog extends BaseBottomSheetDialogFragment {

    private Slider sliderR, sliderG, sliderB;
    private TextView tvR, tvG, tvB;
    private View viewPreview;
    private MaterialButton btnSend;

    private int initR = 0, initG = 0, initB = 0;
    private OnRgbConfirmListener listener;

    public interface OnRgbConfirmListener {
        void onConfirm(int r, int g, int b);
    }

    public static RgbInputDialog newInstance(int r, int g, int b) {
        RgbInputDialog dialog = new RgbInputDialog();
        Bundle args = new Bundle();
        args.putInt("r", r);
        args.putInt("g", g);
        args.putInt("b", b);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnRgbConfirmListener(OnRgbConfirmListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initR = getArguments().getInt("r", 0);
            initG = getArguments().getInt("g", 0);
            initB = getArguments().getInt("b", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_rgb_input_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sliderR = view.findViewById(R.id.slider_r);
        sliderG = view.findViewById(R.id.slider_g);
        sliderB = view.findViewById(R.id.slider_b);
        tvR = view.findViewById(R.id.tv_r_val);
        tvG = view.findViewById(R.id.tv_g_val);
        tvB = view.findViewById(R.id.tv_b_val);
        viewPreview = view.findViewById(R.id.view_rgb_preview);
        btnSend = view.findViewById(R.id.btn_rgb_send);

        sliderR.setValue(initR);
        sliderG.setValue(initG);
        sliderB.setValue(initB);
        tvR.setText(String.valueOf(initR));
        tvG.setText(String.valueOf(initG));
        tvB.setText(String.valueOf(initB));

        Slider.OnChangeListener changeListener = (slider, value, fromUser) -> {
            tvR.setText(String.valueOf((int) sliderR.getValue()));
            tvG.setText(String.valueOf((int) sliderG.getValue()));
            tvB.setText(String.valueOf((int) sliderB.getValue()));
            updatePreview();
        };

        sliderR.addOnChangeListener(changeListener);
        sliderG.addOnChangeListener(changeListener);
        sliderB.addOnChangeListener(changeListener);

        updatePreview();

        btnSend.setOnClickListener(v -> {
            int r = (int) sliderR.getValue();
            int g = (int) sliderG.getValue();
            int b = (int) sliderB.getValue();
            if (listener != null) {
                listener.onConfirm(r, g, b);
            }
            dismiss();
        });
    }

    private void updatePreview() {
        int r = (int) sliderR.getValue();
        int g = (int) sliderG.getValue();
        int b = (int) sliderB.getValue();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16 * getResources().getDisplayMetrics().density);
        drawable.setColor(Color.rgb(r, g, b));
        viewPreview.setBackground(drawable);
    }
}