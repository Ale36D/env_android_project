package com.example.environmentview.Notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InAppNotification {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static View currentBanner = null;

    /**
     * 显示一条应用内通知横幅（从顶部滑入，自动消失）
     *
     * @param activity  当前 Activity
     * @param iconResId   左侧图标资源 ID（0 则不显示图标）
     * @param title       标题文字
     * @param subtitle    副标题文字（null 则不显示）
     * @param durationMs  显示时长（毫秒）
     */
    public static void show(Activity activity, int iconResId, String title, String subtitle, long durationMs) {
        show(activity, iconResId, title, subtitle, durationMs, "#27AE60");
    }

    /**
     * 显示一条应用内通知横幅（可指定指示条颜色）
     */
    public static void show(Activity activity, int iconResId, String title, String subtitle, long durationMs, String accentColor) {
        if (activity == null || activity.isFinishing()) return;

        handler.post(() -> {
            // 先移除上一条
            dismissCurrent(activity);

            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

            // === 构建横幅布局 ===
            LinearLayout banner = new LinearLayout(activity);
            banner.setOrientation(LinearLayout.HORIZONTAL);
            banner.setGravity(Gravity.CENTER_VERTICAL);

            int hPad = dp(activity, 16);
            int vPad = dp(activity, 12);
            banner.setPadding(hPad, vPad, hPad, vPad);

            // 背景：白色圆角卡片 + 阴影
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.WHITE);
            bg.setCornerRadius(dp(activity, 14));
            bg.setStroke(1, Color.parseColor("#E8E8E8"));
            banner.setBackground(bg);
            banner.setElevation(dp(activity, 8));

            // 左侧彩色指示条
            View indicator = new View(activity);
            GradientDrawable indicatorBg = new GradientDrawable();
            indicatorBg.setColor(Color.parseColor(accentColor));
            indicatorBg.setCornerRadius(dp(activity, 2));
            indicator.setBackground(indicatorBg);
            LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(dp(activity, 4), dp(activity, 36));
            indicatorParams.setMarginEnd(dp(activity, 12));
            banner.addView(indicator, indicatorParams);

            // 图标
            if (iconResId != 0) {
                ImageView icon = new ImageView(activity);
                icon.setImageResource(iconResId);
                icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(activity, 28), dp(activity, 28));
                iconParams.setMarginEnd(dp(activity, 10));
                banner.addView(icon, iconParams);
            }

            // 文字区域
            LinearLayout textArea = new LinearLayout(activity);
            textArea.setOrientation(LinearLayout.VERTICAL);

            TextView tvTitle = new TextView(activity);
            tvTitle.setText(title);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tvTitle.setTextColor(Color.parseColor("#2f3542"));
            tvTitle.setTypeface(null, Typeface.BOLD);
            textArea.addView(tvTitle);

            if (subtitle != null && !subtitle.isEmpty()) {
                TextView tvSub = new TextView(activity);
                tvSub.setText(subtitle);
                tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                tvSub.setTextColor(Color.parseColor("#a4b0be"));
                LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                subParams.topMargin = dp(activity, 2);
                textArea.addView(tvSub, subParams);
            }

            banner.addView(textArea, new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            // 右侧时间标签
            TextView tvNow = new TextView(activity);
            tvNow.setText("刚刚");
            tvNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tvNow.setTextColor(Color.parseColor("#a4b0be"));
            LinearLayout.LayoutParams nowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            nowParams.setMarginStart(dp(activity, 8));
            banner.addView(tvNow, nowParams);

            // === 放置到 DecorView 顶部 ===
            int margin = dp(activity, 16);
            int topMargin = dp(activity, 48); // 留出状态栏空间
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            lp.topMargin = topMargin;

            banner.setTranslationY(-dp(activity, 120));
            decorView.addView(banner, lp);
            currentBanner = banner;

            // === 滑入动画 ===
            ObjectAnimator slideIn = ObjectAnimator.ofFloat(banner, "translationY", -dp(activity, 120), 0);
            slideIn.setDuration(350);
            slideIn.setInterpolator(new android.view.animation.DecelerateInterpolator());
            slideIn.start();

            // === 自动消失 ===
            handler.postDelayed(() -> dismiss(activity, banner), durationMs);

            // 点击提前关闭
            banner.setOnClickListener(v -> {
                handler.removeCallbacksAndMessages(null);
                dismiss(activity, banner);
            });
        });
    }

    /**
     * 简化调用：默认 3 秒、绿色指示条
     */
    public static void show(Activity activity, int iconResId, String title, String subtitle) {
        show(activity, iconResId, title, subtitle, 3000, "#27AE60");
    }

    private static void dismiss(Activity activity, View banner) {
        if (banner == null || activity == null || activity.isFinishing()) return;

        ObjectAnimator slideOut = ObjectAnimator.ofFloat(banner, "translationY", 0, -dp(activity, 120));
        slideOut.setDuration(250);
        slideOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) banner.getParent();
                if (parent != null) {
                    parent.removeView(banner);
                }
                if (currentBanner == banner) {
                    currentBanner = null;
                }
            }
        });
        slideOut.start();
    }

    private static void dismissCurrent(Activity activity) {
        if (currentBanner != null) {
            ViewGroup parent = (ViewGroup) currentBanner.getParent();
            if (parent != null) {
                parent.removeView(currentBanner);
            }
            currentBanner = null;
        }
    }

    private static int dp(Activity activity, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                activity.getResources().getDisplayMetrics());
    }
}