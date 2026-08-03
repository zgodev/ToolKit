# 腾讯 X5 在部分 Android 版本上通过反射访问该运行时内部类。
# 编译 SDK 不公开此类，但设备运行时由系统提供，因此仅抑制 R8 的静态缺失警告。
-dontwarn dalvik.system.VMStack
