plugins {
    id("devicedoctor.jvm.library")
    id("devicedoctor.koin")
}

dependencies {
    implementation(projects.core.domain)
}
