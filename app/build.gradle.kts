plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	// KSP 插件（Room 编译需要）
	id("com.google.devtools.ksp")
}

android {
	namespace = "cn.szu.blankxiao.mycalendar"
	compileSdk = 36

	defaultConfig {
		applicationId = "cn.szu.blankxiao.mycalendar"
		minSdk = 26
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.compose.material3)

	// 日历组件依赖
	// implementation(libs.calendar)
	// implementation(libs.kalendar)
	// implementation(libs.kalendar.foundation)
	// 时间
	// implementation(libs.kotlinx.datetime)
	
	// 农历库
	implementation("cn.6tail:lunar:1.7.0")
	
	// ============ Room 数据库 ============
	val roomVersion = "2.8.0"
	implementation("androidx.room:room-runtime:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")  // Kotlin 协程支持
	ksp("androidx.room:room-compiler:$roomVersion")        // KSP 编译器
	
	// ============ Koin 依赖注入 ============
	val koinVersion = "4.1.1"
	implementation("io.insert-koin:koin-android:$koinVersion")           // Android 核心
	implementation("io.insert-koin:koin-androidx-compose:$koinVersion")  // Compose 支持
	
	// ============ ViewModel ============
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	debugImplementation(libs.androidx.compose.ui.tooling)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
}