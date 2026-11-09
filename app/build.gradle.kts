plugins {
    alias(libs.plugins.android.application)
    id("devicedoctor.android.compose")
    id("devicedoctor.koin")
    id("devicedoctor.quality")
}

android {
    namespace = "com.mbr.devicedoctor"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mbr.devicedoctor"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(projects.agent)
    implementation(projects.core.device)
    implementation(projects.core.domain)
    implementation(projects.feature.chat)

    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
}
