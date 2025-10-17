# Build Optimization Guide

## ProGuard Configuration

This project uses ProGuard for code shrinking, obfuscation, and optimization.

### Build Variants

1. **Debug** - No optimization, full debugging
2. **Release** - Standard ProGuard optimization with resource shrinking

### Build Commands

```bash
# Standard release build with ProGuard
./gradlew assembleRelease

# Install release build on device
./gradlew installRelease

```

### ProGuard Files

- `proguard-rules.pro` - Main ProGuard configuration
- `proguard-rules-aggressive.pro` - Aggressive optimization rules (optional)

### Expected Size Reduction

- **Debug APK**: ~15-20 MB (baseline)
- **Release APK**: ~8-12 MB (40-50% reduction)

### Optimization Features

#### Code Shrinking
- Removes unused classes, methods, and fields
- Eliminates dead code paths
- Removes debug logging in release builds

#### Resource Shrinking
- Removes unused resources (drawables, strings, layouts)
- Optimizes PNG files
- Removes unused alternative resources

#### Obfuscation
- Renames classes, methods, and fields to short names
- Makes reverse engineering more difficult
- Reduces APK size through shorter names

#### Optimization
- Inlines methods where beneficial
- Removes unused parameters
- Optimizes control flow
- Merges classes where possible

### Troubleshooting

#### Common Issues

1. **Reflection Issues**
   - Add `-keep` rules for classes used via reflection
   - Check crash logs for `ClassNotFoundException`

2. **Serialization Issues**
   - Keep serializable classes and their fields
   - Maintain `serialVersionUID` fields

3. **Native Library Issues**
   - Keep JNI method signatures
   - Preserve native method names

#### Debugging ProGuard

1. **Enable mapping files** in `proguard-rules.pro`:
   ```
   -printmapping mapping.txt
   -printseeds seeds.txt
   -printusage usage.txt
   ```

2. **Check what's being removed**:
   ```bash
   # View removed classes
   grep "removed" composeApp/build/outputs/mapping/release/usage.txt
   ```

3. **Analyze APK contents**:
   ```bash
   # Use Android Studio APK Analyzer
   # Or command line tools
   aapt dump badging composeApp/build/outputs/apk/release/composeApp-release.apk
   ```

### Performance Impact

- **Build time**: +30-60 seconds for ProGuard processing
- **App startup**: Slightly faster due to smaller APK
- **Runtime performance**: Minimal impact, some optimizations may improve performance

### Security Benefits

- **Code obfuscation**: Makes reverse engineering more difficult
- **String encryption**: Sensitive strings are obfuscated
- **Control flow obfuscation**: Makes code analysis harder
- **Dead code removal**: Reduces attack surface

### Maintenance

1. **Regular testing**: Test thoroughly after ProGuard changes
2. **Mapping files**: Keep mapping files for crash analysis (in `build/outputs/mapping/release/`)
3. **Rule updates**: Update rules when adding new libraries
4. **Size monitoring**: Track APK size changes over time
