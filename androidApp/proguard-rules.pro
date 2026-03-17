# ============ 基础配置 ============
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*

# ============ Kotlinx Serialization ============
-keepattributes InnerClasses
-keep,includedescriptorclasses class cn.szu.blankxiao.mycalendar.**$$serializer { *; }
-keepclassmembers class cn.szu.blankxiao.mycalendar.** {
    *** Companion;
}
-keepclasseswithmembers class cn.szu.blankxiao.mycalendar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============ Ktor Client ============
-keepattributes Signature
-keepattributes Exceptions
-keep class io.ktor.client.** { *; }
-keep interface okhttp3.** { *; }

# ============ Room KMP ============
-keep class * extends androidx.room.RoomDatabase { (); }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============ Koin ============
-keep class org.koin.** { *; }
-keep class cn.szu.blankxiao.mycalendar.di.** { *; }

# ============ Compose ============
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============ 农历库 ============
-keep class cn.6tail.lunar.** { *; }
