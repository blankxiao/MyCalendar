plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	// KSP 插件（Room 编译需要）
	id("com.google.devtools.ksp")
	// Kotlin 序列化插件
	id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
	// OpenAPI Generator 插件
	id("org.openapi.generator") version "7.2.0"
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
		debug {
			// Debug 环境配置
			buildConfigField("String", "BASE_URL", "\"https://api.blankxiao.online/\"")
			buildConfigField("Boolean", "ENABLE_LOGGING", "true")
		}
		release {
			// 代码混淆
			isMinifyEnabled = true
			// 资源压缩
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			// Release 环境配置
			buildConfigField("String", "BASE_URL", "\"https://api.blankxiao.online/\"")
			buildConfigField("Boolean", "ENABLE_LOGGING", "false")
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
		buildConfig = true  // 启用 BuildConfig 生成
	}
	
	// 添加生成代码的kotlin目录为源码目录
	sourceSets {
		getByName("main") {
			java.srcDirs("src/main/java", "src/main/kotlin")
		}
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
	
	// ============ Kotlin 序列化 ============
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
	
	// ============ Material Icons Extended ============
	implementation("androidx.compose.material:material-icons-extended:1.5.4")
	
	// ============ Navigation Compose ============
	implementation("androidx.navigation:navigation-compose:2.7.6")
	
	// ============ DataStore Preferences ============
	implementation("androidx.datastore:datastore-preferences:1.1.1")
	
	// ============ Retrofit (OpenAPI生成代码依赖) ============
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
	
	// ============ Kotlinx Serialization for Retrofit ============
	implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	debugImplementation(libs.androidx.compose.ui.tooling)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	
	// ============ 性能分析工具 ============
	// LeakCanary - 内存泄漏检测
	// debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
	
	// Compose 性能分析
	debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
	debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
}

// ============ OpenAPI Generator 多服务配置 ============

// 服务配置数据类
data class ApiServiceConfig(
	val name: String,           // 服务名称
	val port: Int,              // 服务端口
	val packageSuffix: String   // 包名后缀
)

// 服务列表 - 添加新服务只需在此处添加配置
val apiServices = listOf(
	ApiServiceConfig("Calendar", 8094, "calendar"),
	ApiServiceConfig("Auth", 8093, "auth")
)

// 公共配置函数
fun org.openapitools.generator.gradle.plugin.tasks.GenerateTask.applyCommonConfig(
	serviceName: String,
	port: Int,
	packageSuffix: String
) {
	group = "openapi"
	description = "生成 $serviceName 服务的 API 代码"
	
	generatorName.set("kotlin")
	library.set("jvm-retrofit2")
	remoteInputSpec.set("http://localhost:$port/v3/api-docs.yaml")
	outputDir.set("$projectDir")
	
	val basePackage = "cn.szu.blankxiao.mycalendar.remote.$packageSuffix"
	packageName.set(basePackage)
	modelPackage.set("$basePackage.model")
	apiPackage.set("$basePackage.api")
	
	configOptions.set(mapOf(
		"useCoroutines" to "true",
		"serializationLibrary" to "kotlinx_serialization",
		"dateLibrary" to "java8",
		"enumPropertyNaming" to "UPPERCASE"
	))
	
	skipValidateSpec.set(true)
	generateApiTests.set(false)
	generateApiDocumentation.set(false)
	generateModelTests.set(false)
	generateModelDocumentation.set(false)
	
	additionalProperties.set(mapOf(
		"hideGenerationTimestamp" to "true"
	))
	
	globalProperties.set(mapOf(
		"apis" to "",
		"models" to ""
	))
}

// 动态注册所有服务的生成任务
apiServices.forEach { config ->
	tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generate${config.name}Api") {
		applyCommonConfig(config.name, config.port, config.packageSuffix)
	}
}

// 一键生成所有服务的 API
tasks.register("generateAllApis") {
	group = "openapi"
	description = "生成所有服务的 API 代码"
	dependsOn(apiServices.map { "generate${it.name}Api" })
}
