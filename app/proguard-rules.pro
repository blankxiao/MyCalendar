# ============ 基础配置 ============
# 保留行号信息，便于崩溃日志调试
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 保留注解
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

# 保留所有 @Serializable 数据类
-keep class cn.szu.blankxiao.mycalendar.remote.**.model.** { *; }
-keep class cn.szu.blankxiao.mycalendar.data.**.model.** { *; }

# ============ Retrofit ============
-keepattributes Signature
-keepattributes Exceptions

# Retrofit API 接口
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# 保留 API 接口
-keep interface cn.szu.blankxiao.mycalendar.remote.**.api.** { *; }

# ============ OkHttp ============
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============ Room ============
-keep class * extends androidx.room.RoomDatabase
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