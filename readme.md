# LAN Messenger (Android)

一个简单的局域网消息 APK：

- 输入对方 IP，发送 UDP 消息（端口 `50001`）。
- 后台持续监听 `50001` 端口接收消息。
- 当接收消息中包含本机 IP（或发送者 IP 等于本机 IP）时触发声音提示。

## 构建

```bash
gradle wrapper
./gradlew assembleDebug
```

生成 APK 路径：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

1. 确保两台安卓设备在同一局域网。
2. 安装 APK 后打开应用，会显示本机 IP。
3. 在「对方 IP」输入另一台设备 IP，输入消息并发送。
4. 对方会在日志中看到消息；如果消息命中本机 IP 条件，会播放提示音。
