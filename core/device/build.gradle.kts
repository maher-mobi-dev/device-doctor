plugins {
    id("devicedoctor.android.library")
    id("devicedoctor.koin")
}

android {
    namespace = "com.mbr.devicedoctor.device"
}

dependencies {
    implementation(projects.core.domain)
}
