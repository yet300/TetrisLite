import SwiftUI
import Shared

/// Extension for drawing styled tetromino blocks with various visual effects
/// Matches the Compose implementation with support for multiple piece styles
extension GraphicsContext {
    /// Draw a styled tetromino block with the specified style
    mutating func drawStyledBlock(
        type: TetrominoType,
        settings: GameSettings,
        topLeft: CGPoint,
        cellSize: CGFloat,
        alpha: CGFloat = 1.0
    ) {
        let baseColor = getTetrominoColor(type: type, settings: settings).opacity(alpha)
        let lightColor = getTetrominoLightColor(type: type, settings: settings).opacity(alpha)
        let darkColor = getTetrominoDarkColor(type: type, settings: settings).opacity(alpha)
        let blockSize = CGSize(width: cellSize - 1, height: cellSize - 1)
        
        switch settings.themeConfig.pieceStyle {
        case .solid:
            drawSolidBlock(topLeft: topLeft, size: blockSize, color: baseColor)
            
        case .bordered:
            drawBorderedBlock(topLeft: topLeft, size: blockSize, baseColor: baseColor, lightColor: lightColor, darkColor: darkColor)
            
        case .gradient:
            drawGradientBlock(topLeft: topLeft, size: blockSize, baseColor: baseColor, lightColor: lightColor, darkColor: darkColor, alpha: alpha)
            
        case .retroPixel:
            drawRetroPixelBlock(topLeft: topLeft, size: blockSize, cellSize: cellSize, baseColor: baseColor, darkColor: darkColor)
            
        case .glass:
            drawGlassBlock(topLeft: topLeft, size: blockSize, baseColor: baseColor, lightColor: lightColor, alpha: alpha)
            
        default:
            drawSolidBlock(topLeft: topLeft, size: blockSize, color: baseColor)
        }
    }
    
    // MARK: - Solid Style
    
    private mutating func drawSolidBlock(topLeft: CGPoint, size: CGSize, color: Color) {
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(color))
    }
    
    // MARK: - Bordered Style
    
    private mutating func drawBorderedBlock(topLeft: CGPoint, size: CGSize, baseColor: Color, lightColor: Color, darkColor: Color) {
        // Base block
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(baseColor))
        
        // Top border (light)
        let topBorder = CGRect(x: topLeft.x, y: topLeft.y, width: size.width, height: 2)
        fill(Path(topBorder), with: .color(lightColor))
        
        // Left border (light)
        let leftBorder = CGRect(x: topLeft.x, y: topLeft.y, width: 2, height: size.height)
        fill(Path(leftBorder), with: .color(lightColor))
        
        // Bottom border (dark)
        let bottomBorder = CGRect(x: topLeft.x, y: topLeft.y + size.height - 2, width: size.width, height: 2)
        fill(Path(bottomBorder), with: .color(darkColor))
        
        // Right border (dark)
        let rightBorder = CGRect(x: topLeft.x + size.width - 2, y: topLeft.y, width: 2, height: size.height)
        fill(Path(rightBorder), with: .color(darkColor))
    }
    
    // MARK: - Gradient Style
    
    private mutating func drawGradientBlock(topLeft: CGPoint, size: CGSize, baseColor: Color, lightColor: Color, darkColor: Color, alpha: CGFloat) {
        // Base block
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(baseColor))
        
        // Top-left highlight
        let highlightRect = CGRect(
            x: topLeft.x,
            y: topLeft.y,
            width: size.width * 0.5,
            height: size.height * 0.5
        )
        fill(Path(highlightRect), with: .color(lightColor.opacity(alpha * 0.5)))
        
        // Bottom-right shadow
        let shadowRect = CGRect(
            x: topLeft.x + size.width * 0.5,
            y: topLeft.y + size.height * 0.5,
            width: size.width * 0.5,
            height: size.height * 0.5
        )
        fill(Path(shadowRect), with: .color(darkColor.opacity(alpha * 0.3)))
    }
    
    // MARK: - Retro Pixel Style
    
    private mutating func drawRetroPixelBlock(topLeft: CGPoint, size: CGSize, cellSize: CGFloat, baseColor: Color, darkColor: Color) {
        let pixelSize = cellSize / 4.0
        
        for py in 0..<4 {
            for px in 0..<4 {
                // Create checkerboard pattern
                let isLight = (px + py) % 2 == 0
                let pixelRect = CGRect(
                    x: topLeft.x + CGFloat(px) * pixelSize,
                    y: topLeft.y + CGFloat(py) * pixelSize,
                    width: pixelSize - 0.5,
                    height: pixelSize - 0.5
                )
                fill(Path(pixelRect), with: .color(isLight ? baseColor : darkColor))
            }
        }
    }
    
    // MARK: - Glass Style (Liquid Glass Effect)
    
    private mutating func drawGlassBlock(topLeft: CGPoint, size: CGSize, baseColor: Color, lightColor: Color, alpha: CGFloat) {
        let rect = CGRect(origin: topLeft, size: size)
        
        fill(Path(rect), with: .color(baseColor.opacity(alpha * 0.65)))
        
        let highlightGradient = Gradient(colors: [
            .white.opacity(alpha * 0.4),
            lightColor.opacity(alpha * 0.1),
            .clear
        ])
        
        let gradientCenter = CGPoint(x: topLeft.x + size.width * 0.3, y: topLeft.y + size.height * 0.3)
        
        fill(
            Path(rect),
            with: .radialGradient(
                highlightGradient,
                center: gradientCenter,
                startRadius: 0,
                endRadius: size.width * 0.8
            )
        )
        
        let borderPath = Path(roundedRect: rect, cornerSize: .zero)
        stroke(borderPath, with: .color(.white.opacity(alpha * 0.4)), lineWidth: 1)
        

        var shadowPath = Path()
        shadowPath.move(to: CGPoint(x: topLeft.x + size.width, y: topLeft.y + 1))
        shadowPath.addLine(to: CGPoint(x: topLeft.x + size.width, y: topLeft.y + size.height))
        shadowPath.addLine(to: CGPoint(x: topLeft.x + 1, y: topLeft.y + size.height))
        
        stroke(shadowPath, with: .color(.black.opacity(alpha * 0.15)), lineWidth: 1)
    }
    
    // MARK: - Color Helpers
    
    private func getTetrominoColor(type: TetrominoType, settings: GameSettings) -> Color {
        // Base colors for each tetromino type
        switch type {
        case .i: return Color(red: 0, green: 0.94, blue: 0.94)      // Cyan
        case .o: return Color(red: 0.94, green: 0.94, blue: 0)      // Yellow
        case .t: return Color(red: 0.63, green: 0, blue: 0.94)      // Purple
        case .s: return Color(red: 0, green: 0.94, blue: 0)         // Green
        case .z: return Color(red: 0.94, green: 0, blue: 0)         // Red
        case .j: return Color(red: 0, green: 0, blue: 0.94)         // Blue
        case .l: return Color(red: 0.94, green: 0.63, blue: 0)      // Orange
        default: return .gray
        }
    }
    
    private func getTetrominoLightColor(type: TetrominoType, settings: GameSettings) -> Color {
        // Lighter version for highlights
        let base = getTetrominoColor(type: type, settings: settings)
        return base.opacity(1.0)  // Could be adjusted for lighter shade
    }
    
    private func getTetrominoDarkColor(type: TetrominoType, settings: GameSettings) -> Color {
        // Darker version for shadows
        let base = getTetrominoColor(type: type, settings: settings)
        return base.opacity(0.6)  // Darker shade
    }
}
