# ProGuard rules for HispanoTV

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends dagger.hilt.android.HiltViewModel

# Keep Room entities and DAOs
-keep class com.hispanotv.data.local.entity.** { *; }
-keep class com.hispanotv.data.local.dao.** { *; }
-keep class com.hispanotv.data.local.AppDatabase { *; }

# Keep Moshi adapters
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}

# Keep Kotlin serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# Keep Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Keep OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep Navigation
-keep class androidx.navigation.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep TV libraries
-keep class androidx.tv.** { *; }

# Keep Paging
-keep class androidx.paging.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Lifecycle
-keep class androidx.lifecycle.** { *; }

# Keep Activity/Fragment
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# Keep ViewModel
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.viewmodel.** { *; }

# Keep Hilt Navigation Compose
-keep class androidx.hilt.navigation.compose.** { *; }

# Keep Material3
-keep class androidx.compose.material3.** { *; }

# Optimization
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Don't warn about missing classes
-dontwarn java.lang.invoke.MethodHandles
-dontwarn java.lang.invoke.MethodHandle