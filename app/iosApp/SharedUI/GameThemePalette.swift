import SwiftUI
import Shared
#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

func themeTetrominoColor(type: TetrominoType, theme: VisualTheme) -> Color {
    switch theme {
    case .classic:
        switch type {
        case .i: return colorFromARGB(0xFF00F0F0)
        case .o: return colorFromARGB(0xFFF0F000)
        case .t: return colorFromARGB(0xFFA000F0)
        case .s: return colorFromARGB(0xFF00F000)
        case .z: return colorFromARGB(0xFFF00000)
        case .j: return colorFromARGB(0xFF0000F0)
        case .l: return colorFromARGB(0xFFF0A000)
        default: return .gray
        }

    case .retroGameboy:
        switch type {
        case .i, .t, .z, .l: return colorFromARGB(0xFF0F380F)
        case .o, .s, .j: return colorFromARGB(0xFF306230)
        default: return .gray
        }

    case .retroNes:
        switch type {
        case .i: return colorFromARGB(0xFF00D8F8)
        case .o: return colorFromARGB(0xFFF8D800)
        case .t: return colorFromARGB(0xFFB800F8)
        case .s: return colorFromARGB(0xFF00F800)
        case .z: return colorFromARGB(0xFFF80000)
        case .j: return colorFromARGB(0xFF0000F8)
        case .l: return colorFromARGB(0xFFF87800)
        default: return .gray
        }

    case .neon:
        switch type {
        case .i: return colorFromARGB(0xFF00FFFF)
        case .o: return colorFromARGB(0xFFFFFF00)
        case .t: return colorFromARGB(0xFFFF00FF)
        case .s: return colorFromARGB(0xFF00FF00)
        case .z: return colorFromARGB(0xFFFF0066)
        case .j: return colorFromARGB(0xFF0066FF)
        case .l: return colorFromARGB(0xFFFF6600)
        default: return .gray
        }

    case .pastel:
        switch type {
        case .i: return colorFromARGB(0xFFB4E7F5)
        case .o: return colorFromARGB(0xFFFFF4B4)
        case .t: return colorFromARGB(0xFFE5B4F5)
        case .s: return colorFromARGB(0xFFB4F5B4)
        case .z: return colorFromARGB(0xFFF5B4B4)
        case .j: return colorFromARGB(0xFFB4B4F5)
        case .l: return colorFromARGB(0xFFF5D4B4)
        default: return .gray
        }

    case .monochrome:
        switch type {
        case .i: return colorFromARGB(0xFFFFFFFF)
        case .o: return colorFromARGB(0xFFE0E0E0)
        case .t: return colorFromARGB(0xFFC0C0C0)
        case .s: return colorFromARGB(0xFFA0A0A0)
        case .z: return colorFromARGB(0xFF808080)
        case .j: return colorFromARGB(0xFF606060)
        case .l: return colorFromARGB(0xFF404040)
        default: return .gray
        }

    case .ocean:
        switch type {
        case .i: return colorFromARGB(0xFF00CED1)
        case .o: return colorFromARGB(0xFF20B2AA)
        case .t: return colorFromARGB(0xFF4682B4)
        case .s: return colorFromARGB(0xFF5F9EA0)
        case .z: return colorFromARGB(0xFF1E90FF)
        case .j: return colorFromARGB(0xFF0000CD)
        case .l: return colorFromARGB(0xFF000080)
        default: return .gray
        }

    case .sunset:
        switch type {
        case .i: return colorFromARGB(0xFFFF6B6B)
        case .o: return colorFromARGB(0xFFFFD93D)
        case .t: return colorFromARGB(0xFFFF8C42)
        case .s: return colorFromARGB(0xFFFFA07A)
        case .z: return colorFromARGB(0xFFFF69B4)
        case .j: return colorFromARGB(0xFFFF4500)
        case .l: return colorFromARGB(0xFFFF1493)
        default: return .gray
        }

    case .forest:
        switch type {
        case .i: return colorFromARGB(0xFF228B22)
        case .o: return colorFromARGB(0xFF32CD32)
        case .t: return colorFromARGB(0xFF006400)
        case .s: return colorFromARGB(0xFF90EE90)
        case .z: return colorFromARGB(0xFF2E8B57)
        case .j: return colorFromARGB(0xFF3CB371)
        case .l: return colorFromARGB(0xFF8FBC8F)
        default: return .gray
        }

    default:
        return .gray
    }
}

func themeBackgroundColor(theme: VisualTheme) -> Color {
    switch theme {
    case .classic: return colorFromARGB(0xFF000000)
    case .retroGameboy: return colorFromARGB(0xFF9BBC0F)
    case .retroNes: return colorFromARGB(0xFF000000)
    case .neon: return colorFromARGB(0xFF0A0A0A)
    case .pastel: return colorFromARGB(0xFFF5F5DC)
    case .monochrome: return colorFromARGB(0xFF000000)
    case .ocean: return colorFromARGB(0xFF001F3F)
    case .sunset: return colorFromARGB(0xFF2C1810)
    case .forest: return colorFromARGB(0xFF0D1F0D)
    default: return .black
    }
}

func themeGridColor(theme: VisualTheme) -> Color {
    switch theme {
    case .classic: return colorFromARGB(0xFF333333)
    case .retroGameboy: return colorFromARGB(0xFF8BAC0F)
    case .retroNes: return colorFromARGB(0xFF404040)
    case .neon: return colorFromARGB(0xFF00FFFF)
    case .pastel: return colorFromARGB(0xFFE0E0E0)
    case .monochrome: return colorFromARGB(0xFF404040)
    case .ocean: return colorFromARGB(0xFF004080)
    case .sunset: return colorFromARGB(0xFF804020)
    case .forest: return colorFromARGB(0xFF1A3D1A)
    default: return .gray
    }
}

func themeTetrominoLightColor(type: TetrominoType, theme: VisualTheme, factor: Double = 0.3) -> Color {
    let components = rgbaComponents(for: themeTetrominoColor(type: type, theme: theme))
    return Color(
        red: min(max(components.red + (1 - components.red) * factor, 0), 1),
        green: min(max(components.green + (1 - components.green) * factor, 0), 1),
        blue: min(max(components.blue + (1 - components.blue) * factor, 0), 1),
        opacity: components.alpha
    )
}

func themeTetrominoDarkColor(type: TetrominoType, theme: VisualTheme, factor: Double = 0.3) -> Color {
    let components = rgbaComponents(for: themeTetrominoColor(type: type, theme: theme))
    return Color(
        red: min(max(components.red * (1 - factor), 0), 1),
        green: min(max(components.green * (1 - factor), 0), 1),
        blue: min(max(components.blue * (1 - factor), 0), 1),
        opacity: components.alpha
    )
}

private func colorFromARGB(_ argb: UInt32) -> Color {
    let alpha = Double((argb >> 24) & 0xFF) / 255.0
    let red = Double((argb >> 16) & 0xFF) / 255.0
    let green = Double((argb >> 8) & 0xFF) / 255.0
    let blue = Double(argb & 0xFF) / 255.0
    return Color(red: red, green: green, blue: blue, opacity: alpha)
}

private func rgbaComponents(for color: Color) -> (red: Double, green: Double, blue: Double, alpha: Double) {
    #if canImport(UIKit)
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        guard uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha) else {
            return (0, 0, 0, 1)
        }
        return (Double(red), Double(green), Double(blue), Double(alpha))
    #elseif canImport(AppKit)
        let nsColor = NSColor(color)
        guard let converted = nsColor.usingColorSpace(.deviceRGB) else {
            return (0, 0, 0, 1)
        }
        return (
            Double(converted.redComponent),
            Double(converted.greenComponent),
            Double(converted.blueComponent),
            Double(converted.alphaComponent)
        )
    #else
        return (0, 0, 0, 1)
    #endif
}
