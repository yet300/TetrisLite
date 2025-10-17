# Aggressive ProGuard Configuration for Maximum Size Reduction
# Use with caution - test thoroughly before release

# Include base rules
-include proguard-rules.pro

# ================================
# AGGRESSIVE OPTIMIZATIONS
# ================================

# More aggressive optimization passes
-optimizationpasses 10

# Enable all optimizations except problematic ones
-optimizations !code/simplification/variable,!code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# More aggressive obfuscation
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively

# ================================
# REMOVE DEBUG CODE
# ================================

# Remove all debug and verbose logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
    public static boolean isLoggable(java.lang.String, int);
}

# Remove Kotlin debug assertions
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(java.lang.Object);
    public static void checkNotNull(java.lang.Object, java.lang.String);
    public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullParameter(java.lang.Object, java.lang.String);
    public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
}

# Remove println and print statements
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static void println(...);
    public static void print(...);
}

# Remove System.out.println
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# ================================
# REMOVE UNUSED FEATURES
# ================================

# Remove unused Compose debugging
-assumenosideeffects class androidx.compose.runtime.ComposerKt {
    boolean isTraceInProgress();
    void traceEventStart(int, int, int, java.lang.String);
    void traceEventEnd();
}
