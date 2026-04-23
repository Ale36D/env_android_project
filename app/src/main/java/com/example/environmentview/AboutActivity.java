package com.example.environmentview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.environmentview.Animation.AnimationHandler;
import com.google.android.material.card.MaterialCardView;


public class AboutActivity extends AppCompatActivity {
    private MaterialCardView cd_about_title_card, cd_about_body_card, cd_about_data_quote_card;
    private ImageButton iv_btn_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.viewInit();
    }

    public void viewInit(){
        this.findView();
        this.btnHandler();
        this.setViewAnimation();
    }

    private void setViewAnimation() {
        AnimationHandler.setItemAnimation(this, cd_about_title_card, 0);
        AnimationHandler.setItemAnimation(this, cd_about_body_card, 1);
        AnimationHandler.setItemAnimation(this, cd_about_data_quote_card, 2);
    }

    private void btnHandler() {
        iv_btn_back.setOnClickListener(v -> finish());
    }

    private void findView() {
        cd_about_title_card = findViewById(R.id.cd_about_body_card);
        cd_about_body_card = findViewById(R.id.cd_about_body_card);
        cd_about_data_quote_card = findViewById(R.id.cd_about_data_quote_card);

        iv_btn_back = findViewById(R.id.iv_btn_back);
    }
}


