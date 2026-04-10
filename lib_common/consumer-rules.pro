# lib_common consumer rules
-keep class com.alibaba.android.arouter.routes.** { *; }
-keep class com.alibaba.android.arouter.facade.** { *; }
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe { *; }
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
