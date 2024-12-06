plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.ryusw.ipc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ryusw.ipc"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // [D-1] flavor 배포를 위한 productFlavor 설정
    flavorDimensions += "CMC_IPC"
    productFlavors {
        register("server") {
            dimension = "CMC_IPC"
        }
        register("android") {
            dimension = "CMC_IPC"
        }
        register("ios"){
            dimension = "CMC_IPC"
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // [D-2] buildConfig, viewBinding 설정
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    // [D-3] apk 파일 이름 자동 변경
    applicationVariants.forEach { variant ->
        val mergedFlavor = variant.mergedFlavor
        variant.outputs.forEach { output ->
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                val versionName = mergedFlavor.versionName
                val flavorName = variant.flavorName
                val buildType = variant.buildType.name

                output.outputFileName = "CMC_${versionName}-${flavorName}-${buildType}.apk"
            }
        }
    }
}

dependencies {
    implementation(project(":cmc_ipc"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}