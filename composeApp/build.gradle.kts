import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidx.room)
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}

kotlin {
    androidLibrary {
        namespace = "cn.szu.blankxiao.mycalendar.library"
        compileSdk = 36
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources {
            enable = true
        }
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.datetime)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("io.insert-koin:koin-core:4.1.1")
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
                implementation("io.ktor:ktor-client-core:3.4.1")
                implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                // Compose Multiplatform
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation("cn.6tail:lunar:1.7.0")
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.activity.compose)
                implementation("io.insert-koin:koin-android:4.1.1")
                implementation("io.insert-koin:koin-androidx-compose:4.1.1")
                implementation("io.ktor:ktor-client-android:3.4.1")
                implementation("io.ktor:ktor-client-logging:3.4.1")
                implementation("androidx.navigation:navigation-compose:2.7.6")
                implementation("androidx.datastore:datastore-preferences:1.1.1")
                implementation("androidx.compose.material:material-icons-extended:1.5.4")
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.datetime.jvm)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
                implementation("io.ktor:ktor-client-cio:3.4.1")
                implementation("io.ktor:ktor-client-logging:3.4.1")
                implementation("io.insert-koin:koin-compose:4.1.1")
                implementation("io.insert-koin:koin-compose-viewmodel:4.1.1")
                implementation("io.insert-koin:koin-core-viewmodel:4.1.1")
            }
        }

    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
    add("androidRuntimeClasspath", libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "cn.szu.blankxiao.mycalendar.MainKt"
        // 多目标时需指定 JVM，确保 run 任务使用正确的 classpath
        from(kotlin.targets.getByName("jvm"))
        // 解决 Windows 上 Skiko RenderNodeContext_nMake 等 GPU 相关错误，强制软件渲染
        jvmArgs += "-Dskiko.renderApi=SOFTWARE"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "cn.szu.blankxiao.mycalendar"
            packageVersion = "1.0.0"
        }
    }
}
