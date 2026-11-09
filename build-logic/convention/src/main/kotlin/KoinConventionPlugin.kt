import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        dependencies {
            add("implementation", platform(libs.lib("koin-bom")))
            add("implementation", libs.lib("koin-core"))
        }
    }
}
