package com.example.environmentview.Adapter.WeaterAdapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.List;

public class LifeSuggestionAdapter extends RecyclerView.Adapter<LifeSuggestionAdapter.VH> {

    public static class LifeItem {
        public String name;
        public String brief;
        public String details;
        public String icon;

        public LifeItem(String name, String brief, String details, String icon) {
            this.name = name;
            this.brief = brief;
            this.details = details;
            this.icon = icon;
        }
    }

    private final Context context;
    private final List<LifeItem> items;
    private final Typeface materialIconFont;
    // 记录每个 item 的展开状态
    private final List<Boolean> expandedStates;
    private int lastAnimatedPosition = -1;

    private static final int[][] COLORS = {
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFFDEDEC, 0xFFE74C3C},
            {0xFFFEF9E7, 0xFFF39C12},
            {0xFFEAF2F8, 0xFF2980B9},
    };

    public LifeSuggestionAdapter(Context context, List<LifeItem> items) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.materialIconFont = Typeface.createFromAsset(context.getAssets(), "fonts/material.otf");
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < this.items.size(); i++) {
            expandedStates.add(false);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_life_suggestion_card_layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, @SuppressLint("RecyclerView") int position) {
        LifeItem item = items.get(position);

        h.tvName.setText(item.name);
        h.tvBrief.setText(item.brief);
        h.tvDetails.setText(item.details);
        h.tvIcon.setTypeface(materialIconFont);
        h.tvIcon.setText(item.icon);

        int colorIndex = position % COLORS.length;
        int bgColor = COLORS[colorIndex][0];
        int iconColor = COLORS[colorIndex][1];

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setShape(GradientDrawable.OVAL);
        bgDrawable.setColor(bgColor);
        h.viewIconBg.setBackground(bgDrawable);
        h.tvIcon.setTextColor(iconColor);
        h.tvBrief.setTextColor(iconColor);

        // 展开/收起状态
        boolean expanded = expandedStates.get(position);
        h.tvDetails.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.ivArrow.setRotation(expanded ? 180f : 0f);

        // 点击展开/收起
        h.itemView.setOnClickListener(v -> {
//            int pos = h.getAdapterPosition();
//            if (pos == RecyclerView.NO_POSITION) return;
//
//            boolean isExpanded = expandedStates.get(pos);
//            expandedStates.set(pos, !isExpanded);
//
//            // 箭头动画
//            h.ivArrow.animate()
//                    .rotation(isExpanded ? 0f : 180f)
//                    .setDuration(200)
//                    .start();
//
//            if (isExpanded) {
//                // 收起
//                collapseView(h.tvDetails);
//            } else {
//                // 展开
//                expandView(h.tvDetails);
//            }
        });

        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(context, h.itemView, Math.min(position, 6));
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void expandView(View view) {
        view.setVisibility(View.VISIBLE);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.UNSPECIFIED);
        int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(200);
        animator.addUpdateListener(a -> {
            view.getLayoutParams().height = (int) a.getAnimatedValue();
            view.requestLayout();
        });
        animator.start();
    }

    private void collapseView(View view) {
        int initialHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(200);
        animator.addUpdateListener(a -> {
            int val = (int) a.getAnimatedValue();
            if (val == 0) {
                view.setVisibility(View.GONE);
            }
            view.getLayoutParams().height = val;
            view.requestLayout();
        });
        animator.start();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvBrief, tvDetails, tvIcon;
        View viewIconBg;
        ImageView ivArrow;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_life_name);
            tvBrief = v.findViewById(R.id.tv_life_brief);
            tvDetails = v.findViewById(R.id.tv_life_details);
            tvIcon = v.findViewById(R.id.tv_life_icon);
            viewIconBg = v.findViewById(R.id.view_life_icon_bg);
            ivArrow = v.findViewById(R.id.iv_life_arrow);
        }
    }
}