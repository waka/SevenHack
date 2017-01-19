# ----------------------------------------
# Android Support Library
# ----------------------------------------
-dontwarn android.support.**
-keep class android.support.** { *; }

# ----------------------------------------
# retrolambda
# ----------------------------------------
-dontwarn java.lang.invoke.*

# ----------------------------------------
# Parceler library
# ----------------------------------------
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# ----------------------------------------
# RxJava
# ----------------------------------------
-dontwarn rx.internal.util.unsafe.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

# ----------------------------------------
# Retrofit and OkHttp
# ----------------------------------------
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# ----------------------------------------
# Picasso
# ----------------------------------------
-dontwarn com.squareup.picasso.**

# ----------------------------------------
# Simple XML
# ----------------------------------------
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

# ----------------------------------------
# 7hack
# ----------------------------------------
-keep class io.github.waka.sevenhack.** { *; }
-keepnames class ** { *; }

# ----------------------------------------
# Android
# ----------------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepnames class * extends java.lang.Throwable
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.Fragment

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-dontwarn android.databinding.**