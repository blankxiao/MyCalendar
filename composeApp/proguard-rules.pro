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

# ============ Ktor Client ============
-keepattributes Signature
-keepattributes Exceptions
-keep class io.ktor.client.** { *; }
-keep interface okhttp3.** { *; }

# ============ Room KMP ============
# Room 需要保留无参构造以便找到生成的实现
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
