package com.example.environmentview.Event.Process;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.environmentview.Info.DeviceManager;
import com.example.environmentview.Notification.InAppNotification;
import com.example.environmentview.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttDeviceStatusManager {

    private static final String TAG = "DEVICE_STATUS";
    private static final long CHECK_INTERVAL = 15_000;  // 每15秒检查一次
    private static final long TIMEOUT = 60_000;          // 60秒无消息判定离线

    // deviceId -> 最后一次收到消息的时间戳
    private static final Map<String, Long> lastResponseTime = new HashMap<>();

    // deviceId -> 在线状态
    private static final Map<String, Boolean> onlineStatus = new HashMap<>();

    // UI 观察这个
    private static final MutableLiveData<Map<String, Boolean>> statusLiveData =
            new MutableLiveData<>();

    private static Handler handler;
    private static boolean isRunning = false;

    // 持有 Activity 弱引用，用于弹出应用内通知
    private static WeakReference<Activity> activityRef;

    /**
     * 绑定 Activity，用于显示应用内通知
     * 在 MainActivity.onCreate 中调用
     */
    public static void bindActivity(Activity activity) {
        activityRef = new WeakReference<>(activity);
    }

    /**
     * 启动定时检查（纯被动，不发送任何命令）
     */
    public static void startPolling() {
        if (isRunning) return;
        isRunning = true;
        handler = new Handler(Looper.getMainLooper());
        handler.post(checkRunnable);
        Log.w(TAG, "在线检测已启动（被动模式，60秒超时）");
    }

    /**
     * 停止检查
     */
    public static void stopPolling() {
        isRunning = false;
        if (handler != null) {
            handler.removeCallbacks(checkRunnable);
        }
        Log.w(TAG, "在线检测已停止");
    }

    private static final Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            // 只做超时检查，不发送任何命令
            checkTimeout();

            handler.postDelayed(this, CHECK_INTERVAL);
        }
    };

    /**
     * 检查每个设备是否超时（1分钟无消息 → 离线）
     */
    private static void checkTimeout() {
        long now = System.currentTimeMillis();
        boolean changed = false;

        List<DeviceManager.DeviceEntry> deviceList = DeviceManager.getDeviceList();
        for (DeviceManager.DeviceEntry device : deviceList) {
            String deviceId = device.id;

            Long lastTime = lastResponseTime.get(deviceId);
            boolean wasOnline = Boolean.TRUE.equals(onlineStatus.get(deviceId));

            // 从来没收到过消息 → 离线
            // 超过 TIMEOUT 没收到消息 → 离线
            boolean isOnline = (lastTime != null) && (now - lastTime < TIMEOUT);

            if (wasOnline != isOnline) {
                onlineStatus.put(deviceId, isOnline);
                changed = true;
                Log.w(TAG, deviceId + (isOnline ? " 在线" : " 离线（超过60秒未收到消息）"));

                // ★ 设备离线时弹出通知 ★
                if (!isOnline) {
                    showOfflineNotification(deviceId);
                }
            }
        }

        if (changed) {
            statusLiveData.postValue(new HashMap<>(onlineStatus));
        }
    }

    /**
     * 收到设备任意消息时调用，刷新该设备的最后活跃时间
     */
    public static void onDeviceResponse(String deviceId) {
        lastResponseTime.put(deviceId, System.currentTimeMillis());

        boolean wasOnline = Boolean.TRUE.equals(onlineStatus.get(deviceId));
        if (!wasOnline) {
            onlineStatus.put(deviceId, true);
            statusLiveData.postValue(new HashMap<>(onlineStatus));
            Log.w(TAG, deviceId + " 上线");

            // 弹出应用内通知
            showOnlineNotification(deviceId);
        }
    }

    /**
     * 显示设备上线的应用内通知横幅
     */
    private static void showOnlineNotification(String deviceId) {
        if (activityRef == null) return;
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) return;

        String displayName = DeviceManager.getDisplayName(deviceId);
        String title = displayName + " 已上线";
        String subtitle = "设备ID: " + deviceId + " 开始上传数据";

        InAppNotification.show(activity, R.drawable.online, title, subtitle, 3000);
    }

    /**
     * 显示设备离线的应用内通知横幅
     */
    private static void showOfflineNotification(String deviceId) {
        if (activityRef == null) return;
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing()) return;

        String displayName = DeviceManager.getDisplayName(deviceId);
        String title = displayName + " 已离线";
        String subtitle = "设备ID: " + deviceId + " 超过60秒未响应";

        InAppNotification.show(activity, R.drawable.offline, title, subtitle, 3000, "#E74C3C");
    }

    /**
     * 注册一个新设备（首次收到数据时调用）
     */
    public static void registerDevice(String deviceId) {
        if (!lastResponseTime.containsKey(deviceId)) {
            lastResponseTime.put(deviceId, System.currentTimeMillis());
            onlineStatus.put(deviceId, true);
            statusLiveData.postValue(new HashMap<>(onlineStatus));
        }
    }

    public static LiveData<Map<String, Boolean>> getStatusLiveData() {
        return statusLiveData;
    }

    public static boolean isDeviceOnline(String deviceId) {
        return Boolean.TRUE.equals(onlineStatus.get(deviceId));
    }

    /**
     * 设备被删除时清理
     */
    public static void removeDevice(String deviceId) {
        lastResponseTime.remove(deviceId);
        onlineStatus.remove(deviceId);
        statusLiveData.postValue(new HashMap<>(onlineStatus));
    }
}