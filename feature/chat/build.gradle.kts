plugins {
    id("devicedoctor.android.library")
    id("devicedoctor.android.compose")
    id("devicedoctor.koin")
}

android {
    namespace = "com.mbr.devicedoctor.feature.chat"
}

dependencies {
    implementation(projects.core.domain)
}
