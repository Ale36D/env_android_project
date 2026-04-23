package com.example.environmentview.Animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.example.environmentview.R;

public class AnimationHandler {
    public static void setTopAnimation(Context context, View view){
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.top_card);
        view.startAnimation(animation);
    }
    public static void setBottomAnimation(Context context, View view){
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.bottom_card);
        animation.setStartOffset(200);
        animation.setDuration(400);
        view.startAnimation(animation);
    }
    // 列表 item 动画：从下滑入，支持交错延迟
    public static void setItemAnimation(Context context, View view, int position) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.item_slide_in);
        animation.setStartOffset(position * 80L); // 每个item延迟80ms，产生交错效果
        view.startAnimation(animation);
    }

    // 横向列表 item 动画：从右滑入
    public static void setHorizontalItemAnimation(Context context, View view, int position) {
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.item_slide_left);
        animation.setStartOffset(position * 100L);
        view.startAnimation(animation);
    }

}
