# DTO / Worker / OTA
-keep class com.zhangyt.test.** { *; }

# WorkManager：ListenableWorker 子类要保留公开构造器
-keep public class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
