# EnvironmentView - 环境监测与控制 Android APP

基于无线通信的环境监测与控制系统的 Android 客户端，使用 MQTT 协议与嵌入式设备（ESP32-S3）进行实时数据通信和远程控制。

## 功能概览

- **实时数据展示**：温湿度、气压、海拔、光照、六轴姿态、可燃气体、振动、倾斜、水浸等 15 项传感器数据
- **设备远程控制**：4 路继电器开关 + 蜂鸣器控制，通过 RS-485 Modbus 协议执行
- **RGB 灯光调节**：三通道滑块实时预览颜色，一键发送到 WS2812B 灯带
- **多设备管理**：支持添加/删除多个设备，ViewPager2 分页切换
- **设备在线检测**：60 秒超时被动检测，上线/离线应用内通知横幅
- **天气信息**：集成和风天气 API（实时气象）和心知天气 API（22 项生活指数）
- **消息中心**：MQTT 原始报文实时展示，支持详情查看和一键复制
- **数据持久化**：SharedPreferences 缓存设备数据，离线也能查看上次数据

## 技术栈

| 类别 | 技术 |
|------|------|
| 开发语言 | Java |
| 开发工具 | Android Studio |
| 最低 SDK | Android 8.0 (API 26) |
| UI 框架 | Material Design (Material Components) |
| MQTT 通信 | Eclipse Paho Android Client |
| JSON 解析 | Gson |
| 架构模式 | 单 Activity + 多 Fragment |
| 数据层 | LiveData 观察者模式 + SharedPreferences 持久化 |

## 项目结构

```
com.example.environmentview/
├── AppConfig/
│   ├── AppConfig.java          # SharedPreferences 封装（MQTT配置、设备缓存等）
│   └── Keys.java               # 存储键名常量
├── Adapter/
│   ├── HomeDeviceAdapter/       # 首页设备卡片适配器
│   ├── MqttRecylerViewAdapter/
│   │   ├── SensorPageAdapter.java    # 传感器/执行器/RGB 分组折叠适配器
│   │   ├── DevicePagerAdapter.java   # ViewPager2 多设备分页适配器
│   │   └── ...
│   └── WeaterAdapter/           # 天气/生活指数列表适配器
├── Animation/
│   └── AnimationHandler.java    # 页面切换动画
├── DialogFragment/
│   ├── Base/                    # BottomSheet 基类
│   ├── RgbInput/
│   │   └── RgbInputDialog.java  # RGB 滑块调节弹窗
│   ├── ManageDevice/            # 设备添加/删除管理弹窗
│   ├── LoginMqttConfig/         # MQTT 连接配置弹窗
│   └── MainSignOut/             # 退出登录确认弹窗
├── Event/
│   ├── MainEventHandler.java         # 主界面事件（天气加载、设备状态、导航）
│   ├── LoginEventHandler.java        # 登录流程（MQTT 连接）
│   ├── WeatherEventHandler.java      # 天气数据获取与展示
│   ├── SensorDataEventHandler.java   # 传感器数据格式转换
│   └── Process/
│       ├── MqttMessageProcess.java   # MQTT 消息解析与分发
│       ├── MqttRepository.java       # 数据仓库（LiveData + 缓存）
│       └── MqttDeviceStatusManager.java  # 设备在线状态管理
├── FragmentPage/
│   ├── HomeFragment.java             # 首页（天气 + 设备列表）
│   ├── SensorDataFragment.java       # 数据页（多设备分页展示）
│   ├── DevicePageFragment.java       # 单设备数据页
│   ├── MessageCenterFragment.java    # 消息中心
│   └── UserInfoFragment.java         # 用户信息 / 设置
├── Mqtt/
│   ├── MqttClientManager.java        # MQTT 客户端管理（单例）
│   └── MqttCommandSender.java        # 控制命令发送
├── InAppNotification.java            # 应用内通知横幅（设备上下线）
├── LoginActivity.java                # 登录页
├── MainActivity.java                 # 主页（Fragment 容器 + 底部导航）
└── DeviceDetailActivity.java         # 设备详情页
```

## MQTT 通信协议

### 数据上报（设备 → APP）

设备每 10 秒发布一次，APP 通过订阅主题接收：

```json
{
  "id": "30:30:F9:16:9E:60",
  "cmd": "999",
  "updateTime": "23:50:07",
  "body": {
    "sensor": {
      "tp": 25.5, "hm": 30, "l": 85,
      "pr": 100603, "al": 60,
      "vt": 0, "dp": 0, "fd": 0,
      "madc": 10, "mdi": 0,
      "aX": 0, "aY": 0, "aZ": 0,
      "rl": -2, "pc": 3
    },
    "relay": { "r_1": 0, "r_2": 0, "r_3": 0, "r_4": 0, "bz": 0 },
    "rgb": { "r": 0, "g": 0, "b": 0 }
  }
}
```

### 控制命令（APP → 设备）

继电器控制（cmd=0）：

```json
{
  "id": "30:30:F9:16:9E:60",
  "cmd": [0],
  "body": {
    "relay": { "r_1": 1, "r_2": 0, "r_3": 0, "r_4": 0, "bz": 0 }
  }
}
```

RGB 控制（cmd=1）：

```json
{
  "id": "30:30:F9:16:9E:60",
  "cmd": [1],
  "body": {
    "rgb": [255, 128, 0]
  }
}
```

## 配置说明

### MQTT 连接参数

在 APP 登录页面配置以下参数：

| 参数 | 说明 | 示例 |
|------|------|------|
| Broker 地址 | MQTT 服务器 IP 或域名 | tcp://180.76.128.235:1883 |
| Client ID | 客户端标识（需唯一） | android_client_001 |
| 订阅主题 | 接收设备数据的主题 | iot/data |
| 发布主题 | 发送控制命令的主题 | iot/cmd |
| 用户名 | MQTT 认证用户名（可选） | — |
| 密码 | MQTT 认证密码（可选） | — |

### 天气 API

在 `WeatherEventHandler.java` 中配置 API Key：

- 和风天气：实时气象数据（温度、湿度、风速、气压等）
- 心知天气：生活指数建议（穿衣、紫外线、运动等 22 项）

## 构建与运行

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器（API 26+）
4. 点击 Run 构建并安装
5. 在登录页配置 MQTT Broker 地址和主题
6. 点击登录连接到 MQTT 服务器

## 依赖库

```groovy
// MQTT
implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5'
implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'

// Material Design
implementation 'com.google.android.material:material:1.11.0'

// JSON
implementation 'com.google.code.gson:gson:2.10.1'

// SVG 图标
implementation 'com.caverock:androidsvg-aar:1.4'
```

## 系统架构

本 APP 是环境监测与控制系统的移动端组成部分，完整系统包括：

- **嵌入式采集端**：ESP32-S3 + 多传感器 + RS-485 Modbus 继电器 + FreeRTOS
- **Android APP**：本项目，MQTT 实时通信 + Material Design UI
- **Django Web 平台**：设备管理 + 数据可视化 + 数据大屏
- **MQTT Broker**：消息中间件，支持多终端同时订阅和控制
## 运行截图
![登录界面](https://raw.githubusercontent.com/Ale36D/img-bed/main/android_4.jpg)
![主界面](https://raw.githubusercontent.com/Ale36D/img-bed/main/android_3.jpg)
![天气界面](https://raw.githubusercontent.com/Ale36D/img-bed/main/android_2.jpg)
![传感器数据界面](https://raw.githubusercontent.com/Ale36D/img-bed/main/android_1.jpg)

