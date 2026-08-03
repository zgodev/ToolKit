-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}
-keep class com.zhangyt.core.model.** { *; }
-keep class com.zhangyt.common.user.** { *; }
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**
