plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // <-- 我们只加这一行
}

android {
    namespace = "com.mmcleige.petapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mmcleige.petapplication"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // 1. 基础 UI 与导航 (Fragment 跳转)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // 2. 本地数据库 Room (存宠物档案)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    add("kapt", "androidx.room:room-compiler:$room_version")

    // 3. 后台定时任务 WorkManager (提醒打疫苗/驱虫)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // 4. 网络请求 Retrofit (连你的 Python 后端)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 5. 图表库 (画体重折线图)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 6. 图片加载神器 Coil (负责把高清大图压缩并切成圆角)
    implementation("io.coil-kt:coil:2.6.0")

}