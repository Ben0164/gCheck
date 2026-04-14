// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

// Define properties in the extra block for both Groovy and Kotlin DSL plugins
val extra = rootProject.extra
extra.set("compileSdkVersion", 34)
extra.set("minSdkVersion", 24)
extra.set("targetSdkVersion", 34)

// Also define them in the 'ext' block specifically for Groovy-based plugins/scripts
allprojects {
    project.extensions.extraProperties.set("compileSdkVersion", 34)
    project.extensions.extraProperties.set("minSdkVersion", 24)
    project.extensions.extraProperties.set("targetSdkVersion", 34)
}
