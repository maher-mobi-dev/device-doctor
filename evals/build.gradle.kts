plugins {
    id("devicedoctor.jvm.library")
}

dependencies {
    implementation(projects.agent)
    implementation(projects.core.domain)
}
