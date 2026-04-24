########################################
# 通用：保留调试信息 / 泛型 / 注解
########################################
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeVisibleTypeAnnotations
-keepattributes AnnotationDefault
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

########################################
# Kotlin metadata / Coroutines / Reflection
########################################
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.**

-keepclassmembernames class kotlinx.** { volatile <fields>; }
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

########################################
# App 自身：App / SplashActivity / BuildConfig
########################################
-keep class com.zhangyt.toolkit.App { *; }
-keep class com.zhangyt.toolkit.SplashActivity { *; }
-keep class com.zhangyt.toolkit.BuildConfig { *; }

########################################
# WebView JS Bridge（如未使用可删除）
########################################
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

########################################
# R8 Missing classes（编译期引用，运行时不存在，dontwarn 即可）
########################################
# ARouter 注解处理器里 RouteMeta 保留了 javax.lang.model 元素引用，仅注解处理阶段使用
-dontwarn javax.lang.model.element.**
-dontwarn javax.lang.model.**

# android_extend（guo.android_extend.HListView）在 utils/libs 的 aar 中引用了自身 R，被 shrink 时找不到
-dontwarn com.guo.android_extend.**
-keep class com.guo.android_extend.** { *; }

# JXL / 其他库可能带的 SLF4J 绑定，运行时无此实现
-dontwarn org.slf4j.**
