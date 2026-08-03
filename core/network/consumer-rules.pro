-keepattributes Signature, Exceptions, *Annotation*
-keep class com.zhangyt.network.api.** { *; }
-keep class com.zhangyt.network.exception.** { *; }
-keep class com.zhangyt.network.config.NetworkConfig { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
