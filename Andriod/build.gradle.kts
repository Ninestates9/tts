plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}





android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.pytorch.android)
    implementation(libs.onnxruntime.android)
    implementation(libs.kotlin.stdlib)
    implementation(libs.commons.io)
    implementation(libs.compose.preview.detector)
    implementation(libs.gson)
    implementation(libs.components.resources.android)
    implementation(libs.belerweb.pinyin4j)
    implementation(libs.ext.junit)
    implementation(libs.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
}