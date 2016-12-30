# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#常用混淆标记
-ignorewarnings
-keepattributes Signature
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}

#andframe混淆标记
-keep class com.andframe.entity.** {<fields>;}
-keep class com.andframe.model.** {<fields>;}
-keep class com.andframe.bean.** {<fields>;}

-keepclassmembers class * {
    @com.andframe.annotation.view.BindAfterViews *;
    @com.andframe.annotation.view.BindCheckedChange *;
    @com.andframe.annotation.view.BindClick *;
    @com.andframe.annotation.view.BindItemClick *;
    @com.andframe.annotation.view.BindItemLongClick *;
    @com.andframe.annotation.view.BindLayout *;
    @com.andframe.annotation.view.BindLongClick *;
    @com.andframe.annotation.view.BindView *;
    @com.andframe.annotation.view.BindViewModule *;

    @com.andframe.annotation.inject.Inject *;
    @com.andframe.annotation.inject.InjectInit *;
    @com.andframe.annotation.inject.InjectExtra *;
    @com.andframe.annotation.inject.InjectDelayed *;
    @com.andframe.annotation.inject.InjectLayout *;
    @com.andframe.annotation.inject.InjectQueryChanged *;
}
