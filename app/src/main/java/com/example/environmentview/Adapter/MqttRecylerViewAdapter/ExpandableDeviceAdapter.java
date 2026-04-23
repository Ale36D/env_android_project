package com.example.environmentview.Adapter.MqttRecylerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.environmentview.Adapter.Model.MqttDataModel.MqttDataModel;
import com.example.environmentview.Animation.AnimationHandler;
import com.example.environmentview.Event.Process.MqttRepository;
import com.example.environmentview.MqttTools.MqttCommandSender;
import com.example.environmentview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpandableDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ACTUATOR = 1;
    private static final int TYPE_SENSOR = 2;

    private final Context context;
    private final String dataType; // "actuator" 或 "sensor"
    private final Typeface materialIconFont;

    private RecyclerView recyclerView;

    // 扁平化列表
    private final List<Object> displayList = new ArrayList<>();

    // 设备分组数据
    private final List<DeviceGroup> groups = new ArrayList<>();

    // 只在加载的时候播放动画
    private int lastAnimatedPosition = -1;

    // 执行器颜色
    private static final int[][] ICON_COLORS = {
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFFDEDEC, 0xFFE74C3C},
    };

    // 传感器颜色
    private static final int[][] SENSOR_COLORS = {
            {0xFFFDF2E9, 0xFFE67E22},
            {0xFFEBF5FB, 0xFF3498DB},
            {0xFFF5EEF8, 0xFF9B59B6},
            {0xFFE8F8F5, 0xFF1ABC9C},
            {0xFFFDEDEC, 0xFFE74C3C},
    };

    public ExpandableDeviceAdapter(Context context, String dataType) {
        this.context = context;
        this.dataType = dataType;
        this.materialIconFont = Typeface.createFromAsset(
                context.getAssets(), "fonts/material.otf");
    }

    /**
     * 设置分组数据
     */
    public void setGroups(List<DeviceGroup> groupList) {
        groups.clear();
        groups.addAll(groupList);
        rebuildDisplayList();
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        this.recyclerView = rv;
    }

    private void rebuildDisplayList() {
        displayList.clear();
        for (DeviceGroup group : groups) {
            displayList.add(group); // 头部
            if (group.expanded) {
                displayList.addAll(group.items); // 子项
            }
        }
        lastAnimatedPosition = -1;
        // 安全刷新：如果 RecyclerView 正在布局，延迟到下一帧
        if (recyclerView != null && recyclerView.isComputingLayout()) {
            recyclerView.post(this::notifyDataSetChanged);
        } else {
            notifyDataSetChanged();
        }
    }
    // 更新指定设备的数据
    public void updateGroupData(String deviceId, List<MqttDataModel> newItems) {
        for (DeviceGroup group : groups) {
            if (group.deviceId.equals(deviceId)) {
                group.items = newItems;
                rebuildDisplayList();
                return;
            }
        }
    }
    // 重新设置所有分组（全量刷新）
    public void refreshAllGroups(List<DeviceGroup> groupList) {
        // 保留展开/折叠状态
        Map<String, Boolean> expandState = new HashMap<>();
        for (DeviceGroup g : groups) {
            expandState.put(g.deviceId, g.expanded);
        }

        groups.clear();
        groups.addAll(groupList);

        // 恢复展开状态
        for (DeviceGroup g : groups) {
            Boolean wasExpanded = expandState.get(g.deviceId);
            if (wasExpanded != null) {
                g.expanded = wasExpanded;
            }
        }

        rebuildDisplayList();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayList.get(position);
        if (item instanceof DeviceGroup) return TYPE_HEADER;
        return "actuator".equals(dataType) ? TYPE_ACTUATOR : TYPE_SENSOR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.list_device_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ACTUATOR) {
            View view = inflater.inflate(R.layout.list_actuator_data_show_layout, parent, false);
            return new ActuatorViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.list_sensor_data_show_layout, parent, false);
            return new SensorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Object item = displayList.get(position);

        if (holder instanceof HeaderViewHolder) {
            bindHeader((HeaderViewHolder) holder, (DeviceGroup) item);
        } else if (holder instanceof ActuatorViewHolder) {
            bindActuator((ActuatorViewHolder) holder, (MqttDataModel) item, position);
        } else if (holder instanceof SensorViewHolder) {
            bindSensor((SensorViewHolder) holder, (MqttDataModel) item, position);
        }
        // 只有首次出现的 item 才播放动画
        if (position > lastAnimatedPosition) {
            AnimationHandler.setItemAnimation(context, holder.itemView, position);
            lastAnimatedPosition = position;
        }
    }

    private void bindHeader(HeaderViewHolder holder, DeviceGroup group) {
        holder.tvDeviceName.setText(group.deviceName);
        holder.tvItemCount.setText(group.items.size() + "项");
        holder.ivExpandArrow.setRotation(group.expanded ? 180f : 0f);

        holder.itemView.setOnClickListener(v -> {
            group.expanded = !group.expanded;
            rebuildDisplayList();
        });
    }

    private void bindActuator(ActuatorViewHolder holder, MqttDataModel model, int position) {
        holder.tvRelayName.setText(model.getRelayName());
        holder.tvRelayTag.setText(model.getRelayTag());

        holder.tvRelayIcon.setTypeface(materialIconFont);
        holder.tvRelayIcon.setText(model.getRelayIcon());

        // 找到所属分组的子项索引来决定颜色
        int colorIndex = getChildIndex(position) % ICON_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ICON_COLORS[colorIndex][0]);
        holder.viewRelayIconBg.setBackground(bg);
        holder.tvRelayIcon.setTextColor(ICON_COLORS[colorIndex][1]);

        holder.scRelaySwitch.setOnCheckedChangeListener(null);
        boolean isOn = "1".equals(model.getRelayValue());
        holder.scRelaySwitch.setChecked(isOn);

        holder.scRelaySwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            // 找到所属设备
            DeviceGroup group = findGroupForPosition(holder.getAdapterPosition());
            if (group == null) return;

            model.setRelayValue(checked ? "1" : "0");

            Map<String, Object> relayState = new HashMap<>();
            for (MqttDataModel item : group.items) {
                relayState.put(item.getRelayTag(),
                        "1".equals(item.getRelayValue()) ? 1 : 0);
            }

            Map<String, Object> body = new HashMap<>();
            body.put("relay", relayState);
            MqttCommandSender.sendCommand(group.deviceId, 0, body);
        });
    }

    private void bindSensor(SensorViewHolder holder, MqttDataModel model, int position) {
        holder.tvSensorName.setText(model.getSensorName());
        holder.tvSensorData.setText(model.getSensorValue());
        holder.tvSensorTag.setText(model.getSensorTag());

        holder.tvSensorIcon.setTypeface(materialIconFont);
        holder.tvSensorIcon.setText(model.getSensorIcon());

        int colorIndex = getChildIndex(position) % SENSOR_COLORS.length;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(SENSOR_COLORS[colorIndex][0]);
        holder.viewSensorIconBg.setBackground(bg);
        holder.tvSensorIcon.setTextColor(SENSOR_COLORS[colorIndex][1]);
    }

    /**
     * 找到当前 position 在其分组内的子项索引
     */
    private int getChildIndex(int position) {
        int index = 0;
        for (int i = position - 1; i >= 0; i--) {
            if (displayList.get(i) instanceof DeviceGroup) break;
            index++;
        }
        return index;
    }

    /**
     * 找到当前 position 所属的 DeviceGroup
     */
    private DeviceGroup findGroupForPosition(int position) {
        for (int i = position - 1; i >= 0; i--) {
            if (displayList.get(i) instanceof DeviceGroup) {
                return (DeviceGroup) displayList.get(i);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ========== 数据模型 ==========

    public static class DeviceGroup {
        public String deviceId;
        public String deviceName;
        public List<MqttDataModel> items;
        public boolean expanded = true; // 默认展开

        public DeviceGroup(String deviceId, String deviceName, List<MqttDataModel> items) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.items = items;
        }
    }

    // ========== ViewHolder ==========

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName, tvItemCount;
        ImageButton ivExpandArrow;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            ivExpandArrow = itemView.findViewById(R.id.iv_expand_arrow);
        }
    }

    static class ActuatorViewHolder extends RecyclerView.ViewHolder {
        TextView tvRelayName, tvRelayTag, tvRelayIcon;
        View viewRelayIconBg;
        Switch scRelaySwitch;

        ActuatorViewHolder(View itemView) {
            super(itemView);
            tvRelayName = itemView.findViewById(R.id.tv_relay_name);
            tvRelayTag = itemView.findViewById(R.id.tv_relay_tag);
            tvRelayIcon = itemView.findViewById(R.id.tv_relay_icon);
            viewRelayIconBg = itemView.findViewById(R.id.view_relay_icon_bg);
            scRelaySwitch = itemView.findViewById(R.id.sc_relay_switch);
        }
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSensorName, tvSensorData, tvSensorTag, tvSensorIcon;
        View viewSensorIconBg;

        SensorViewHolder(View itemView) {
            super(itemView);
            tvSensorName = itemView.findViewById(R.id.tv_sensor_name);
            tvSensorData = itemView.findViewById(R.id.tv_sensor_data);
            tvSensorTag = itemView.findViewById(R.id.tv_sensor_tag);
            tvSensorIcon = itemView.findViewById(R.id.tv_sensor_icon);
            viewSensorIconBg = itemView.findViewById(R.id.view_sensor_icon_bg);
        }
    }
}