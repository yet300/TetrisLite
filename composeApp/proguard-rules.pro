-keepattributes SourceFile,LineNumberTable,Signature,*Annotation*
-renamesourcefileattribute SourceFile

-optimizationpasses 5

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends androidx.core.app.ComponentActivity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.yet.tetris.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory {
    private static final kotlinx.coroutines.MainCoroutineDispatcher INSTANCE;
}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory

-keep class androidx.compose.runtime.** { *; }
-keep class **.*ComposableSingletons* { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

-keep class org.koin.** { *; }

-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.essenty.** { *; }

-keep class com.arkivanov.mvikotlin.** { *; }

-keep class app.cash.sqldelight.** { *; }
-keepclassmembers class * extends app.cash.sqldelight.Query { <init>(...); }

-keep class com.russhwolf.settings.** { *; }

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontwarn org.jetbrains.annotations.**
-dontwarn java.sql.**
-dontwarn org.slf4j.**