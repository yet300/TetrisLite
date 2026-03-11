import SwiftUI
import Shared

extension GraphicsContext {
    mutating func drawStyledBlock(
        type: TetrominoType,
        settings: GameSettings,
        topLeft: CGPoint,
        cellSize: CGFloat,
        alpha: CGFloat = 1.0
    ) {
        let theme = settings.themeConfig.visualTheme
        let baseColor = themeTetrominoColor(type: type, theme: theme).opacity(alpha)
        let lightColor = themeTetrominoLightColor(type: type, theme: theme).opacity(alpha)
        let darkColor = themeTetrominoDarkColor(type: type, theme: theme).opacity(alpha)
        let inset = max(1, cellSize * 0.06)
        let blockSize = CGSize(width: cellSize - inset, height: cellSize - inset)
        let shadowRect = CGRect(
            x: topLeft.x,
            y: topLeft.y + max(1, cellSize * 0.08),
            width: blockSize.width,
            height: blockSize.height
        )

        fill(Path(shadowRect), with: .color(.black.opacity(alpha * 0.14)))

        switch settings.themeConfig.pieceStyle {
        case .solid:
            drawSolidBlock(
                topLeft: topLeft,
                size: blockSize,
                color: baseColor,
                highlightColor: lightColor.opacity(alpha * 0.18)
            )

        case .bordered:
            drawBorderedBlock(
                topLeft: topLeft,
                size: blockSize,
                baseColor: baseColor,
                lightColor: lightColor,
                darkColor: darkColor
            )

        case .gradient:
            drawGradientBlock(
                topLeft: topLeft,
                size: blockSize,
                baseColor: baseColor,
                lightColor: lightColor,
                darkColor: darkColor,
                alpha: alpha
            )

        case .retroPixel:
            drawRetroPixelBlock(
                topLeft: topLeft,
                size: blockSize,
                cellSize: cellSize,
                baseColor: baseColor,
                darkColor: darkColor
            )

        case .glass:
            drawGlassBlock(
                topLeft: topLeft,
                size: blockSize,
                baseColor: baseColor,
                lightColor: lightColor,
                alpha: alpha
            )

        default:
            drawSolidBlock(
                topLeft: topLeft,
                size: blockSize,
                color: baseColor,
                highlightColor: lightColor.opacity(alpha * 0.18)
            )
        }
    }

    private mutating func drawSolidBlock(
        topLeft: CGPoint,
        size: CGSize,
        color: Color,
        highlightColor: Color
    ) {
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(color))

        let highlightRect = CGRect(
            x: topLeft.x,
            y: topLeft.y,
            width: size.width,
            height: size.height * 0.22
        )
        fill(Path(highlightRect), with: .color(highlightColor))
    }

    private mutating func drawBorderedBlock(
        topLeft: CGPoint,
        size: CGSize,
        baseColor: Color,
        lightColor: Color,
        darkColor: Color
    ) {
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(baseColor))

        let borderWidth = max(1, size.width * 0.1)
        let topBorder = CGRect(x: topLeft.x, y: topLeft.y, width: size.width, height: borderWidth)
        let leftBorder = CGRect(x: topLeft.x, y: topLeft.y, width: borderWidth, height: size.height)
        let bottomBorder = CGRect(
            x: topLeft.x,
            y: topLeft.y + size.height - borderWidth,
            width: size.width,
            height: borderWidth
        )
        let rightBorder = CGRect(
            x: topLeft.x + size.width - borderWidth,
            y: topLeft.y,
            width: borderWidth,
            height: size.height
        )

        fill(Path(topBorder), with: .color(lightColor))
        fill(Path(leftBorder), with: .color(lightColor))
        fill(Path(bottomBorder), with: .color(darkColor))
        fill(Path(rightBorder), with: .color(darkColor))

        let highlightInset = borderWidth
        let highlightRect = CGRect(
            x: topLeft.x + highlightInset,
            y: topLeft.y + highlightInset,
            width: max(size.width - (highlightInset * 2), 1),
            height: size.height * 0.18
        )
        fill(Path(highlightRect), with: .color(.white.opacity(0.1)))
    }

    private mutating func drawGradientBlock(
        topLeft: CGPoint,
        size: CGSize,
        baseColor: Color,
        lightColor: Color,
        darkColor: Color,
        alpha: CGFloat
    ) {
        let rect = CGRect(origin: topLeft, size: size)
        fill(Path(rect), with: .color(baseColor))

        let highlightRect = CGRect(
            x: topLeft.x,
            y: topLeft.y,
            width: size.width * 0.5,
            height: size.height * 0.5
        )
        fill(Path(highlightRect), with: .color(lightColor.opacity(alpha * 0.5)))

        let shadowRect = CGRect(
            x: topLeft.x + size.width * 0.5,
            y: topLeft.y + size.height * 0.5,
            width: size.width * 0.5,
            height: size.height * 0.5
        )
        fill(Path(shadowRect), with: .color(darkColor.opacity(alpha * 0.3)))

        let stripInset = max(1, size.width * 0.06)
        let stripRect = CGRect(
            x: topLeft.x + stripInset,
            y: topLeft.y + stripInset,
            width: max(size.width - max(2, size.width * 0.12), 1),
            height: size.height * 0.14
        )
        fill(Path(stripRect), with: .color(.white.opacity(alpha * 0.12)))
    }

    private mutating func drawRetroPixelBlock(
        topLeft: CGPoint,
        size: CGSize,
        cellSize: CGFloat,
        baseColor: Color,
        darkColor: Color
    ) {
        let pixelSize = cellSize / 4.0

        for py in 0..<4 {
            for px in 0..<4 {
                let isLight = (px + py).isMultiple(of: 2)
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

    private mutating func drawGlassBlock(
        topLeft: CGPoint,
        size: CGSize,
        baseColor: Color,
        lightColor: Color,
        alpha: CGFloat
    ) {
        let rect = CGRect(origin: topLeft, size: size)

        fill(Path(rect), with: .color(baseColor.opacity(alpha * 0.65)))

        let highlightGradient = Gradient(colors: [
            .white.opacity(alpha * 0.4),
            lightColor.opacity(alpha * 0.1),
            .clear,
        ])

        let gradientCenter = CGPoint(
            x: topLeft.x + size.width * 0.3,
            y: topLeft.y + size.height * 0.3
        )

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

        let stripRect = CGRect(
            x: topLeft.x + max(1, size.width * 0.06),
            y: topLeft.y + max(1, size.height * 0.06),
            width: max(size.width - max(2, size.width * 0.12), 1),
            height: size.height * 0.12
        )
        fill(Path(stripRect), with: .color(.white.opacity(alpha * 0.12)))

        var shadowPath = Path()
        shadowPath.move(to: CGPoint(x: topLeft.x + size.width, y: topLeft.y + 1))
        shadowPath.addLine(to: CGPoint(x: topLeft.x + size.width, y: topLeft.y + size.height))
        shadowPath.addLine(to: CGPoint(x: topLeft.x + 1, y: topLeft.y + size.height))

        stroke(shadowPath, with: .color(.black.opacity(alpha * 0.15)), lineWidth: 1)
    }
}

func themeAccentColor(theme: VisualTheme) -> Color {
    themeTetrominoLightColor(type: .i, theme: theme, factor: 0.22)
}
