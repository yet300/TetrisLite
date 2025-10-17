# Tetris Lite ProGuard Configuration
# Optimized for Kotlin Multiplatform + Compose Multiplatform

# ================================
# BASIC CONFIGURATION
# ================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signatures for reflection
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*

# ================================
# KOTLIN MULTIPLATFORM
# ================================

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keep class kotlinx.coroutines.** { *; }

# Keep Kotlin serialization
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations
-keep,includedescriptorclasses class com.yet.tetris.**$$serializer { *; }
-keepclassmembers class com.yet.tetris.** {
    *** Companion;
}
-keepclasseswithmembers class com.yet.tetris.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ================================
# COMPOSE MULTIPLATFORM
# ================================

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }

# Keep Compose compiler generated classes
-keep class **.*ComposableSingletons* { *; }
-keep class **.*LiveLiterals* { *; }

# Keep @Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# ================================
# KOIN DEPENDENCY INJECTION
# ================================

# Keep Koin modules and definitions
-keep class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module
-keepclassmembers class * {
    org.koin.core.module.Module* *;
}

# Keep classes used in Koin modules
-keep class com.yet.tetris.di.** { *; }
-keep class **.*Module* { *; }

# ================================
# SQLDELIGHT DATABASE
# ================================

# Keep SQLDelight generated classes
-keep class com.yet.tetris.database.** { *; }
-keep class app.cash.sqldelight.** { *; }

# Keep database queries and adapters
-keepclassmembers class * extends app.cash.sqldelight.Query {
    <init>(...);
}

# ================================
# MULTIPLATFORM SETTINGS
# ================================

# Keep settings classes
-keep class com.russhwolf.settings.** { *; }

# ================================
# DECOMPOSE NAVIGATION
# ================================

# Keep Decompose components
-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.essenty.** { *; }

# Keep component interfaces and implementations
-keep interface com.yet.tetris.feature.**.component.** { *; }
-keep class com.yet.tetris.feature.**.component.**Impl { *; }
-keep class com.yet.tetris.feature.**.component.**Component { *; }

# ================================
# MVIKOTLIN STORE
# ================================

# Keep MviKotlin stores and executors
-keep class com.arkivanov.mvikotlin.** { *; }
-keep class com.yet.tetris.feature.**.store.** { *; }

# ================================
# DOMAIN MODELS
# ================================

# Keep all domain models (they're used for serialization)
-keep class com.yet.tetris.domain.model.** { *; }
-keep class com.yet.tetris.data.model.** { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================
# GAME LOGIC
# ================================

# Keep use cases (might be used via reflection)
-keep class com.yet.tetris.domain.usecase.** { *; }

# Keep repositories
-keep interface com.yet.tetris.domain.repository.** { *; }
-keep class com.yet.tetris.data.repository.**Impl { *; }

# ================================
# ANDROID SPECIFIC
# ================================

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ================================
# OPTIMIZATION SETTINGS
# ================================

# Enable aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove println statements
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static void println(...);
    public static void print(...);
}

# ================================
# SUPPRESS WARNINGS
# ================================

# SQLite JDBC driver references classes not available on Android
-dontwarn java.sql.JDBCType
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory

# Other common warnings
-dontwarn kotlinx.coroutines.debug.**
-dontwarn kotlinx.coroutines.test.**
-dontwarn org.jetbrains.annotations.**
