package com.example.environmentview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.AppConfig.AppConfig;
import com.google.android.material.card.MaterialCardView;


public class AccountActivity extends AppCompatActivity {
    private MaterialCardView cd_account_title_card, cd_account_body_card;
    private TextView tv_account;
    private ImageButton iv_btn_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.viewInit();
        this.btnHandler();
        this.setViewAnimation();

        // 设置用户名
        String username = AppConfig.getUsername();
        if (username != null && !username.isEmpty()) {
            tv_account.setText(username);
        }

    }

    private void btnHandler() {
        iv_btn_back.setOnClickListener(v -> finish());
    }

    private void setViewAnimation() {
        AnimationHandler.setItemAnimation(this, cd_account_title_card, 0);
        AnimationHandler.setItemAnimation(this, cd_account_body_card, 0);
    }

    private void viewInit() {
        cd_account_title_card = findViewById(R.id.cd_account_title_card);
        cd_account_body_card = findViewById(R.id.cd_account_body_card);

        iv_btn_back = findViewById(R.id.iv_btn_back);
        tv_account = findViewById(R.id.tv_account);

    }
}


