plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.hispanotv.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        buildConfigField("String", "TMDB_API_KEY", "\"${System.getenv("TMDB_API_KEY") ?: ""}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=androidx.room.ExperimentalRoomApi"
        )
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit + OkHttp + Moshi
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Coroutines
    implementation(libs.coroutines.android)

    // DataStore
    implementation(libs.preferences.datastore)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}