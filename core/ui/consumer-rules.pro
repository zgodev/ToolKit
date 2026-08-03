-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
}
-keep class me.jessyan.autosize.** { *; }
-dontwarn me.jessyan.autosize.**
-dontwarn kotlinx.coroutines.**
