########################################
# ARouter
########################################
-keep class com.alibaba.android.arouter.routes.** { *; }
-keep class com.alibaba.android.arouter.facade.** { *; }
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe { *; }
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
-keep @com.alibaba.android.arouter.facade.annotation.Route class * { *; }
-keepclasseswithmembers class * {
    @com.alibaba.android.arouter.facade.annotation.Autowired <fields>;
}

########################################
# MMKV
########################################
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

########################################
# Gson / SerializedName
########################################
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# TypeToken 泛型签名
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter
-keep class * extends com.google.gson.TypeAdapterFactory

########################################
# 项目 DTO / 用户对象（避免被 R8 改字段名）
########################################
-keep class com.zhangyt.common.user.** { *; }
-keep class com.zhangyt.network.api.** { *; }
-keep class com.zhangyt.network.exception.** { *; }

########################################
# Glide
########################################
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** { **[] $VALUES; public *; }
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

########################################
# EventBus
########################################
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

########################################
# XLog
########################################
-keep class com.elvishew.xlog.** { *; }
-dontwarn com.elvishew.xlog.**

########################################
# ViewBinding（BaseActivity / BaseFragment 反射调用 inflate）
########################################
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
}

########################################
# AutoSize
########################################
-keep class me.jessyan.autosize.** { *; }
-dontwarn me.jessyan.autosize.**

########################################
# Retrofit / OkHttp / Okio
########################################
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

########################################
# Kotlin / Coroutines（lib_common 为所有消费者保底）
########################################
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
