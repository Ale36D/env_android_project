package com.example.environmentview;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class TestActivity extends AppCompatActivity {
    public Button btn_test;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn_test = findViewById(R.id.btn_test);
//        btn_test.setOnClickListener(view->{
//            MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog();
//            bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
//        });

    }
}


