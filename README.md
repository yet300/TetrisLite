# TetrisLite
<img width="258" height="258" alt="258" src="https://github.com/user-attachments/assets/de4c43fe-ba97-48d9-a3c9-ce5a901a75b0" />


A modern, cross-platform Tetris game built with Kotlin Multiplatform and Compose Multiplatform, demonstrating true code sharing across Android, iOS, Desktop, and Web platforms.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)
![Compose](https://img.shields.io/badge/Compose-1.9.0-green.svg)
![Platforms](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-orange.svg)

## Features

### 🎨 Customization
- **Visual Themes** - 9 color schemes: Classic, Modern, Neon, Retro, Pastel, Dark, Monochrome, Cyberpunk, and Nature
- **Piece Styles** - Choose between Solid, Gradient, and Outlined piece rendering
- **Control Layouts** - Customizable keyboard layouts (Arrows, WASD, IJKL) and swipe gestures
- **Swipe Sensitivity** - Adjustable touch controls for mobile devices
- **Audio Settings** - Procedurally generated music and SFX with volume control and multiple themes

### 📱 Platform-Specific Features
- **Android** - Touch controls with swipe gestures, baseline profiles for performance
- **iOS** - Native SwiftUI integration with iOS-specific optimizations
- **Desktop** - Keyboard controls with multiple layout options (Windows, macOS, Linux)
- **Web** - Browser-based gameplay with full feature parity

### 💾 Technical Features
- **Game History** - Track all your games with detailed statistics and filtering
- **Persistent Settings** - Your preferences are saved across sessions
- **Offline Support** - Play without an internet connection
- **SQLite Database** - Local storage for game records and settings
- **Procedural Audio** - Dynamically generated music and SFX (no audio files needed!)
- **MVI Architecture** - Predictable state management with MVIKotlin
- **Dependency Injection** - Koin for clean architecture

### Tech Stack
- **Kotlin Multiplatform** 2.2.20 - Share code across all platforms
- **Compose Multiplatform** 1.9.0 - Modern declarative UI
- **MVIKotlin** - MVI architecture for predictable state management
- **Decompose** - Navigation and lifecycle management
- **Koin** - Dependency injection
- **SQLDelight** - Type-safe SQL database
- **Kotlinx Coroutines** - Asynchronous programming
- **Kotlinx Serialization** - JSON serialization

## Architecture

The project follows clean architecture principles with clear separation of concerns:

```
TetrisLite/
├── composeApp/          # Compose Multiplatform UI (Android, Desktop, iOS)
├── iosApp/              # Native iOS app entry point with SwiftUI
├── shared/              # Shared business logic and data layer
├── core/                # Core modules
│   ├── domain/          # Business logic, use cases, domain models
│   ├── data/            # Repository implementations
│   ├── database/        # SQLDelight database
│   ├── common/          # Common utilities
│   └── uikit/           # Shared UI components
└── feature/             # Feature modules
    ├── game/            # Game screen and logic
    ├── home/            # Home screen
    ├── settings/        # Settings management
    ├── history/         # Game history
    └── root/            # Navigation root
```

## Getting Started

### Prerequisites
- **JDK 17** or higher
- **Android Studio** Ladybug or later (for Android development)
- **Xcode 15+** (for iOS development, macOS only)
- **Node.js** (for web development)

### Build and Run

#### Android
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

#### Desktop (JVM)
```bash
./gradlew :composeApp:run
```

#### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run the project.

#### Web
```bash
./gradlew :composeApp:jsBrowserDevelopment
```

## Development

### Code Style
The project uses Ktlint for code formatting and Detekt for static analysis.

```bash
./gradlew ktlintCheck
./gradlew detekt
```

### Testing
```bash
./gradlew test
```

### CI/CD
The project includes GitHub Actions workflows for:
- **PR Checks** - Code quality, tests, and builds
- **Release** - Automated builds and deployments
- **Code Quality** - Static analysis and linting

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

Key areas for enhancement:

1. **UI/UX**: Improve the iOS part
2. **MAC|IPAD**: Add native tablet landscape orientation support(MacOS, IpadOS)

## License

This project is open source and available under the MIT License.

## Learn More

- [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [MVIKotlin](https://github.com/arkivanov/MVIKotlin)
- [Decompose](https://github.com/arkivanov/Decompose)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
