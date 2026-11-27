plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hispanotv.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    // Coroutines / Flow (api: los casos de uso exponen Flow a otros módulos)
    api(libs.coroutines.android)

    // @Inject en los casos de uso
    implementation("javax.inject:javax.inject:1")

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
