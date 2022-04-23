# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.muhibbin.memes.model.** {*;}
-keep class com.muhibbin.memes.adapters.** {*;}
-keep class com.muhibbin.memes.database.** {*;}
-keep class com.muhibbin.memes.listener.** {*;}
-keep class com.muhibbin.memes.photoEditorMainClasses.** {*;}
-keep class com.muhibbin.memes.photoEditorSDK.** {*;}
-keep class com.muhibbin.memes.SDK.** {*;}
-keep class com.muhibbin.memes.broadcastReceiver.** {*;}
