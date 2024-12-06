import java.text.SimpleDateFormat
import java.util.TimeZone

// [D-9] flavor file 생성
apply(from = "flavor-android.gradle")
apply(from = "flavor-ios.gradle")
apply(from = "flavor-server.gradle")

// [D-4] 라이브러리 모듈 생성
plugins {
    alias(libs.plugins.android.library)
}

val simpleDateFormat = SimpleDateFormat("yyyyMMdd").apply {
    timeZone = TimeZone.getTimeZone("Asia/Seoul")
}

android {
    // [D-5] 지원 SDK 설정
    namespace = "com.ryusw.ipc"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    // [D-6] 빌드 옵션 설정
    buildFeatures {
        buildConfig = true
    }
    flavorDimensions += "CMC_IPC"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // [D-7] 라이브러리 output file 이름 설정
    libraryVariants.forEach { variant ->
        val mergedFlavor = variant.getMergedFlavor()
        variant.outputs.forEach { output ->
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                val currentTime = System.currentTimeMillis()
                val fileName =
                    "cmc_ipc_lib-${variant.flavorName}-${mergedFlavor.versionName}-${variant.buildType.name}-${
                        simpleDateFormat.format(currentTime)
                    }.aar"
                println("library file name = $fileName")
                output.outputFileName = fileName
            } else {
                println("output is not com.android.build.gradle.internal.api.BaseVariantOutputImpl")
            }
        }
    }
}

// [D-8] task 작성
tasks.register("exportAAR") {
    val currentTime = System.currentTimeMillis()
    doLast {
        copy {
            from("build/outputs/aar")
            into("${rootProject.rootDir}/CMC_IPC_LIB_${simpleDateFormat.format(currentTime)}")
        }
    }
    tasks.getByName("preBuild")
        .dependsOn("clean")
}

tasks.register("cleanProject") {
    val currentTime = System.currentTimeMillis()
    delete("${rootProject.rootDir}/CMC_IPC_LIB_${simpleDateFormat.format(currentTime)}")
}

tasks.getByName("exportAAR")
    .dependsOn("cleanProject")
    .dependsOn("assemble")

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}