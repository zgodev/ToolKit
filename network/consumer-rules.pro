########################################
# 网络层：对外暴露的数据结构必须完整保留
########################################
-keep class com.zhangyt.network.api.** { *; }
-keep class com.zhangyt.network.exception.** { *; }
-keep class com.zhangyt.network.config.NetworkConfig { *; }

# WebSocket 工具类用到反射 / 内部类
-keep class com.zhangyt.network.websocket.** { *; }
-dontwarn com.zhangyt.network.websocket.**

# Java-WebSocket 库
-keep class org.java_websocket.** { *; }
-dontwarn org.java_websocket.**
