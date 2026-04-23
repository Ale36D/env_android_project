package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.DeviceDetailActivity;
import com.example.environmentview.DialogFragment.RgbInput.RgbInputDialog;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ACTUATOR = 1;
    private static final int TYPE_SENSOR = 2;
    private static final int TYPE_RGB = 3;
    private static final int TYPE_RGB_SEND = 4;

    private final Context context;
    private final Typeface materialIconFont;
    private final List<Object> displayList = new ArrayList<>();
    private final List<SectionGroup> groups = new ArrayList<>();
    private int lastAnimatedPosition = -1;
    private RecyclerView recyclerView;
    private OnRgbSendListener rgbSendListener;

    private static final int[][] ICON_COLORS = {
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFFDEDEC, 0xFFE74C3C},
    };

    private static final int[][] RGB_COLORS = {
            {0xFFFDEDEC, 0xFFE74C3C},
            {0xFFE8F8F5, 0xFF27AE60},
            {0xFFEBF5FB, 0xFF2980B9},
    };

    public interface OnRgbSendListener {
        void onSend(int[] rgb);
    }

    public SensorPageAdapter(Context context) {
        this.context = context;
        this.materialIconFont = Typeface.createFromAsset(context.getAssets(), "fonts/material.otf");
    }

    public void setOnRgbSendListener(OnRgbSendListener listener) {
        this.rgbSendListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        this.recyclerView = rv;
    }

    public void setGroups(List<SectionGroup> groupList) {
        Map<String, Boolean> expandState = new HashMap<>();
        for (SectionGroup g : groups) {
            expandState.put(g.title, g.expanded);
        }
        groups.clear();
        groups.addAll(groupList);
        for (SectionGroup g : groups) {
            Boolean was = expandState.get(g.title);
            if (was != null) g.expanded = was;
        }
        rebuildDisplayList();
    }

    private void rebuildDisplayList() {
        displayList.clear();
        for (SectionGroup group : groups) {
            displayList.add(group);
            if (group.expanded) {
                displayList.addAll(group.items);
                if (group.type == TYPE_RGB) {
                    displayList.add("RGB_SEND");
                }
            }
        }
//        lastAnimatedPosition = -1;
        if (recyclerView != null && recyclerView.isComputingLayout()) {
            recyclerView.post(this::notifyDataSetChanged);
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayList.get(position);
        if (item instanceof SectionGroup) return TYPE_HEADER;
        if (item instanceof String && "RGB_SEND".equals(item)) return TYPE_RGB_SEND;
        if (item instanceof MqttDataModel) {
            SectionGroup group = findGroupForPosition(position);
            if (group != null) return group.type;
        }
        return TYPE_SENSOR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderVH(inflater.inflate(R.layout.list_device_group_header, parent, false));
            case TYPE_ACTUATOR:
                return new ActuatorVH(inflater.inflate(R.layout.list_actuator_data_show_layout, parent, false));
            case TYPE_RGB:
                return new RgbVH(inflater.inflate(R.layout.list_rgb_data_show_layout, parent, false));
            case TYPE_RGB_SEND:
                View sendBtn = new ImageButton(context);
                sendBtn.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 100));
                ((ImageButton) sendBtn).setImageResource(R.drawable.send);
                ((ImageButton) sendBtn).setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                sendBtn.setBackgroundResource(android.R.color.transparent);
                return new RgbSendVH(sendBtn);
            default:
                return new SensorVH(inflater.inflate(R.layout.list_sensor_data_show_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Object item = displayList.get(position);

        if (holder instanceof HeaderVH) {
            bindHeader((HeaderVH) holder, (SectionGroup) item);
        } else if (holder instanceof ActuatorVH) {
            bindActuator((ActuatorVH) holder, (MqttDataModel) item, position);
        } else if (holder instanceof SensorVH) {
            bindSensor((SensorVH) holder, (MqttDataModel) item, position);
        } else if (holder instanceof RgbVH) {
            bindRgb((RgbVH) holder, (MqttDataModel) item, position);
        } else if (holder instanceof RgbSendVH) {
            holder.itemView.setOnClickListener(v -> {
                if (rgbSendListener != null) {
                    showRgbInputDialog();
                }
            });
        }
        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(context, holder.itemView, position);
            lastAnimatedPosition = position;
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindHeader(HeaderVH holder, SectionGroup group) {
        holder.tvName.setText(group.title);
        holder.tvCount.setText(group.items.size() + "项");
        holder.ivArrow.setRotation(group.expanded ? 180f : 0f);
        holder.itemView.setOnClickListener(v -> {
            group.expanded = !group.expanded;
            rebuildDisplayList();
        });
    }

    private void bindActuator(ActuatorVH holder, MqttDataModel model, int position) {
        holder.tvName.setText(model.getRelayName());
        holder.tvTag.setText(model.getRelayTag());
        holder.tvIcon.setTypeface(materialIconFont);
        holder.tvIcon.setText(model.getRelayIcon());

        int ci = getChildIndex(position) % ICON_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ICON_COLORS[ci][0]);
        holder.viewIconBg.setBackground(bg);
        holder.tvIcon.setTextColor(ICON_COLORS[ci][1]);

        holder.scSwitch.setOnCheckedChangeListener(null);
        holder.scSwitch.setChecked("1".equals(model.getRelayValue()));
        holder.scSwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;
            model.setRelayValue(checked ? "1" : "0");

            SectionGroup group = findGroupForPosition(adapterPos);
            if (group == null) return;

            String deviceId = MqttRepository.getCurrentDeviceId();
            if (deviceId == null) return;

            Map<String, Object> relayState = new HashMap<>();
            for (MqttDataModel m : group.items) {
                relayState.put(m.getRelayTag(), "1".equals(m.getRelayValue()) ? 1 : 0);
            }
            Map<String, Object> body = new HashMap<>();
            body.put("relay", relayState);
            MqttCommandSender.sendCommand(deviceId, 0, body);
        });
        holder.itemView.setOnClickListener(v -> {
            int ci2 = getChildIndex(holder.getAdapterPosition()) % ICON_COLORS.length;
            Intent intent = new Intent(context, DeviceDetailActivity.class);
            intent.putExtra("device_id", MqttRepository.getCurrentDeviceId());
            intent.putExtra("item_tag", model.getRelayTag());
            intent.putExtra("item_type", "actuator");
            intent.putExtra("item_name", model.getRelayName());
            intent.putExtra("item_icon", model.getRelayIcon());
            intent.putExtra("item_value", "1".equals(model.getRelayValue()) ? "已开启" : "已关闭");
            intent.putExtra("icon_color", ICON_COLORS[ci2][1]);
            intent.putExtra("icon_bg_color", ICON_COLORS[ci2][0]);
            context.startActivity(intent);
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void bindSensor(SensorVH holder, MqttDataModel model, int position) {
        holder.tvName.setText(model.getSensorName());
        holder.tvData.setText(model.getSensorValue());
        holder.tvTag.setText(model.getSensorTag());
        holder.tvIcon.setTypeface(materialIconFont);
        holder.tvIcon.setText(model.getSensorIcon());

        int ci = getChildIndex(position) % ICON_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ICON_COLORS[ci][0]);
        holder.viewIconBg.setBackground(bg);
        holder.tvIcon.setTextColor(ICON_COLORS[ci][1]);

        // ★ 点击卡片进入详情页
        holder.itemView.setOnClickListener(v -> {
            int ci2 = getChildIndex(holder.getAdapterPosition()) % ICON_COLORS.length;
            Intent intent = new Intent(context, DeviceDetailActivity.class);
            intent.putExtra("device_id", MqttRepository.getCurrentDeviceId());
            intent.putExtra("item_tag", model.getSensorTag());
            intent.putExtra("item_type", "sensor");
            intent.putExtra("item_name", model.getSensorName());
            intent.putExtra("item_icon", model.getSensorIcon());
            intent.putExtra("item_value", model.getSensorValue());
            intent.putExtra("icon_color", ICON_COLORS[ci2][1]);
            intent.putExtra("icon_bg_color", ICON_COLORS[ci2][0]);
            context.startActivity(intent);
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void bindRgb(RgbVH holder, MqttDataModel model, int position) {
        holder.tvName.setText(model.getSensorName());
        holder.tvIcon.setTypeface(materialIconFont);
        holder.tvIcon.setText(model.getSensorIcon());

        int ci = getChildIndex(position) % RGB_COLORS.length;
        GradientDrawable bg = new GradientDrawable();

        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(RGB_COLORS[ci][0]);
        holder.viewIconBg.setBackground(bg);
        holder.tvIcon.setTextColor(RGB_COLORS[ci][1]);

        holder.tvValue.setText(model.getSensorValue());
    }
    private void showRgbInputDialog() {
        if (!(context instanceof FragmentActivity)) return;
        FragmentActivity activity = (FragmentActivity) context;

        int[] currentRgb = getCurrentRgbValues();
        RgbInputDialog dialog = RgbInputDialog.newInstance(
                currentRgb[0], currentRgb[1], currentRgb[2]);

        dialog.setOnRgbConfirmListener((r, g, b) -> {
            if (rgbSendListener != null) {
                rgbSendListener.onSend(new int[]{r, g, b});
            }
        });

        dialog.show(activity.getSupportFragmentManager(), "rgb_input");
    }
    public int[] getCurrentRgbValues() {
        int[] rgb = new int[3];
        int idx = 0;
        for (SectionGroup g : groups) {
            if (g.type == TYPE_RGB) {
                for (int i = 0; i < g.items.size() && i < 3; i++) {
                    try { rgb[i] = Integer.parseInt(g.items.get(i).getSensorValue()); }
                    catch (NumberFormatException e) { rgb[i] = 0; }
                }
                break;
            }
        }
        return rgb;
    }

    private int getChildIndex(int position) {
        int index = 0;
        for (int i = position - 1; i >= 0; i--) {
            if (displayList.get(i) instanceof SectionGroup) break;
            if (displayList.get(i) instanceof String) break;
            index++;
        }
        return index;
    }

    private SectionGroup findGroupForPosition(int position) {
        for (int i = position - 1; i >= 0; i--) {
            if (displayList.get(i) instanceof SectionGroup) return (SectionGroup) displayList.get(i);
        }
        return null;
    }

    @Override
    public int getItemCount() { return displayList.size(); }

    // ===== 数据模型 =====
    public static class SectionGroup {
        public String title;
        public int type;
        public List<MqttDataModel> items;
        public boolean expanded = true;

        public SectionGroup(String title, int type, List<MqttDataModel> items) {
            this.title = title;
            this.type = type;
            this.items = items;
        }
    }

    // ===== ViewHolders =====
    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCount; ImageView ivArrow;
        HeaderVH(View v) { super(v); tvName = v.findViewById(R.id.tv_device_name); tvCount = v.findViewById(R.id.tv_item_count); ivArrow = v.findViewById(R.id.iv_expand_arrow); }
    }
    static class ActuatorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvTag, tvIcon; View viewIconBg; Switch scSwitch;
        ActuatorVH(View v) { super(v); tvName = v.findViewById(R.id.tv_relay_name); tvTag = v.findViewById(R.id.tv_relay_tag); tvIcon = v.findViewById(R.id.tv_relay_icon); viewIconBg = v.findViewById(R.id.view_relay_icon_bg); scSwitch = v.findViewById(R.id.sc_relay_switch); }
    }
    static class SensorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvData, tvTag, tvIcon; View viewIconBg;
        SensorVH(View v) { super(v); tvName = v.findViewById(R.id.tv_sensor_name); tvData = v.findViewById(R.id.tv_sensor_data); tvTag = v.findViewById(R.id.tv_sensor_tag); tvIcon = v.findViewById(R.id.tv_sensor_icon); viewIconBg = v.findViewById(R.id.view_sensor_icon_bg); }
    }
    static class RgbVH extends RecyclerView.ViewHolder {
        TextView tvName, tvIcon, tvValue;
        View viewIconBg;
        RgbVH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_rgb_name);
            tvIcon = v.findViewById(R.id.tv_rgb_icon);
            tvValue = v.findViewById(R.id.tv_rgb_value);   // ★ 改用 TextView
            viewIconBg = v.findViewById(R.id.view_rgb_color_preview);
        }
    }
    static class RgbSendVH extends RecyclerView.ViewHolder {
        RgbSendVH(View v) { super(v); }
    }
}