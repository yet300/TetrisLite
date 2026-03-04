import SwiftUI
import Shared

enum GameStatDensity {
    case compact
    case regular
    case spacious
}

enum GameHudPalette {
    static let label = Color(red: 0, green: 0.55, blue: 0.08)
}

struct GameStatsView: View {
    let score: Int64
    let lines: Int32
    let level: Int32
    let time: Int64
    let density: GameStatDensity

    var body: some View {
        HStack(spacing: horizontalSpacing) {
            StatItem(label: Strings.score, value: "\(score)", density: density)
            StatItem(label: Strings.lines, value: "\(lines)", density: density)
            StatItem(label: Strings.level, value: "\(level)", density: density)
            StatItem(label: Strings.time, value: formattedTime, density: density)
        }
        .padding(.horizontal, horizontalPadding)
        .padding(.vertical, verticalPadding)
        .frame(maxWidth: .infinity)
        .glassPanelStyle(cornerRadius: cornerRadius)
        .clipShape(.rect(cornerRadius: cornerRadius))
    }

    private var formattedTime: String {
        let totalSeconds = max(time, 0) / 1_000
        let minutes = totalSeconds / 60
        let seconds = totalSeconds % 60
        let paddedSeconds = seconds < 10 ? "0\(seconds)" : "\(seconds)"
        return "\(minutes):\(paddedSeconds)"
    }

    private var horizontalSpacing: CGFloat {
        switch density {
        case .compact:
            return 8
        case .regular:
            return 12
        case .spacious:
            return 16
        }
    }

    private var horizontalPadding: CGFloat {
        switch density {
        case .compact:
            return 10
        case .regular:
            return 12
        case .spacious:
            return 16
        }
    }

    private var verticalPadding: CGFloat {
        switch density {
        case .compact:
            return 8
        case .regular:
            return 10
        case .spacious:
            return 12
        }
    }

    private var cornerRadius: CGFloat {
        switch density {
        case .compact:
            return 14
        case .regular:
            return 16
        case .spacious:
            return 18
        }
    }
}

private struct StatItem: View {
    let label: String
    let value: String
    let density: GameStatDensity

    var body: some View {
        VStack(spacing: labelSpacing) {
            Text(label)
                .font(labelFont)
                .foregroundStyle(GameHudPalette.label)
                .lineLimit(1)
                .minimumScaleFactor(0.75)

            Text(value)
                .font(valueFont)
                .bold()
                .monospacedDigit()
                .foregroundStyle(.primary)
                .lineLimit(1)
                .minimumScaleFactor(0.75)
        }
        .frame(maxWidth: .infinity)
    }

    private var labelSpacing: CGFloat {
        switch density {
        case .compact:
            return 2
        case .regular:
            return 3
        case .spacious:
            return 4
        }
    }

    private var labelFont: Font {
        switch density {
        case .compact:
            return .caption
        case .regular:
            return .caption
        case .spacious:
            return .footnote
        }
    }

    private var valueFont: Font {
        switch density {
        case .compact:
            return .headline
        case .regular:
            return .title3
        case .spacious:
            return .title2
        }
    }
}

struct PiecePreviewView: View {
    let title: String
    let piece: Tetromino?
    let previewSize: CGFloat
    let density: GameStatDensity

    var body: some View {
        VStack(spacing: density.previewTitleSpacing) {
            Text(title)
                .font(density.previewTitleFont)
                .foregroundStyle(GameHudPalette.label)
                .lineLimit(1)
                .minimumScaleFactor(0.8)

            if let piece {
                TetrominoPreviewCanvas(piece: piece, previewSize: previewSize)
            } else {
                PiecePreviewPlaceholder(previewSize: previewSize)
            }
        }
        .padding(density.previewPanelPadding)
        .frame(maxWidth: .infinity, alignment: .leading)
        .glassPanelStyle(cornerRadius: density.previewCornerRadius)
        .clipShape(.rect(cornerRadius: density.previewCornerRadius))
        .gameHoverEffect()
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(title)")
    }
}

struct NextQueuePreviewView: View {
    let title: String
    let pieces: [Tetromino]
    let previewSize: CGFloat
    let density: GameStatDensity
    let axis: Axis

    init(
        title: String,
        pieces: [Tetromino],
        previewSize: CGFloat,
        density: GameStatDensity,
        axis: Axis = .vertical
    ) {
        self.title = title
        self.pieces = pieces
        self.previewSize = previewSize
        self.density = density
        self.axis = axis
    }

    var body: some View {
        VStack(alignment: .leading, spacing: density.previewTitleSpacing) {
            Text(title)
                .font(density.previewTitleFont)
                .foregroundStyle(GameHudPalette.label)
                .lineLimit(1)
                .minimumScaleFactor(0.8)

            Group {
                if pieces.isEmpty {
                    PiecePreviewPlaceholder(previewSize: queueItemSize)
                } else {
                    if axis == .horizontal {
                        HStack(spacing: density.queueItemSpacing) {
                            ForEach(pieces.indices, id: \.self) { index in
                                TetrominoPreviewCanvas(piece: pieces[index], previewSize: queueItemSize)
                            }
                        }
                    } else {
                        VStack(spacing: density.queueItemSpacing) {
                            ForEach(pieces.indices, id: \.self) { index in
                                TetrominoPreviewCanvas(piece: pieces[index], previewSize: queueItemSize)
                            }
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(density.previewPanelPadding)
        .frame(maxWidth: .infinity, alignment: .leading)
        .glassPanelStyle(cornerRadius: density.previewCornerRadius)
        .clipShape(.rect(cornerRadius: density.previewCornerRadius))
        .gameHoverEffect()
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(title)")
    }

    private var queueItemSize: CGFloat {
        if axis == .horizontal {
            return max(22, previewSize * 0.54)
        }
        return previewSize
    }
}

struct HoldAndNextPreviewView: View {
    let holdPiece: Tetromino?
    let nextPieces: [Tetromino]
    let holdPreviewSize: CGFloat
    let queuePreviewSize: CGFloat
    let density: GameStatDensity

    var body: some View {
        HStack(alignment: .top, spacing: density.previewGroupSpacing) {
            VStack(alignment: .leading, spacing: density.previewTitleSpacing) {
                Text(Strings.hold)
                    .font(density.previewTitleFont)
                    .foregroundStyle(GameHudPalette.label)
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)

                if let holdPiece {
                    TetrominoPreviewCanvas(piece: holdPiece, previewSize: holdPreviewSize)
                } else {
                    PiecePreviewPlaceholder(previewSize: holdPreviewSize)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            VStack(alignment: .leading, spacing: density.previewTitleSpacing) {
                Text(Strings.next)
                    .font(density.previewTitleFont)
                    .foregroundStyle(GameHudPalette.label)
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)

                if nextPieces.isEmpty {
                    PiecePreviewPlaceholder(previewSize: queuePreviewSize)
                } else {
                    VStack(spacing: density.queueItemSpacing) {
                        ForEach(nextPieces.indices, id: \.self) { index in
                            TetrominoPreviewCanvas(piece: nextPieces[index], previewSize: queuePreviewSize)
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(density.previewPanelPadding)
        .frame(maxWidth: .infinity, alignment: .leading)
        .glassPanelStyle(cornerRadius: density.previewCornerRadius)
        .clipShape(.rect(cornerRadius: density.previewCornerRadius))
        .gameHoverEffect()
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(Strings.hold), \(Strings.next)")
    }
}

private struct TetrominoPreviewCanvas: View {
    let piece: Tetromino
    let previewSize: CGFloat

    var body: some View {
        Canvas { context, canvasSize in
            let blocks = piece.blocks
            let minX = blocks.map { $0.x }.min() ?? 0
            let maxX = blocks.map { $0.x }.max() ?? 0
            let minY = blocks.map { $0.y }.min() ?? 0
            let maxY = blocks.map { $0.y }.max() ?? 0

            let pieceWidthCells = CGFloat(maxX - minX + 1)
            let pieceHeightCells = CGFloat(maxY - minY + 1)
            let maxCellByWidth = canvasSize.width / max(pieceWidthCells + 1, 1)
            let maxCellByHeight = canvasSize.height / max(pieceHeightCells + 1, 1)
            let cellSize = max(4, min(maxCellByWidth, maxCellByHeight))

            let pieceWidth = pieceWidthCells * cellSize
            let pieceHeight = pieceHeightCells * cellSize
            let offsetX = (canvasSize.width - pieceWidth) / 2 - CGFloat(minX) * cellSize
            let offsetY = (canvasSize.height - pieceHeight) / 2 - CGFloat(minY) * cellSize

            for block in blocks {
                let x = CGFloat(block.x) * cellSize + offsetX
                let y = CGFloat(block.y) * cellSize + offsetY

                let rect = CGRect(
                    x: x,
                    y: y,
                    width: max(cellSize - 1, 1),
                    height: max(cellSize - 1, 1)
                )

                context.fill(Path(rect), with: .color(tetrominoColor(for: piece.type)))
            }
        }
        .frame(width: previewSize, height: previewSize)
    }
}

private struct PiecePreviewPlaceholder: View {
    let previewSize: CGFloat

    var body: some View {
        Text("—")
            .font(.headline)
            .foregroundStyle(.secondary)
            .frame(width: previewSize, height: previewSize)
    }
}

private extension GameStatDensity {
    var previewPanelPadding: CGFloat {
        switch self {
        case .compact:
            return 6
        case .regular:
            return 8
        case .spacious:
            return 10
        }
    }

    var previewTitleSpacing: CGFloat {
        switch self {
        case .compact:
            return 4
        case .regular:
            return 6
        case .spacious:
            return 8
        }
    }

    var previewCornerRadius: CGFloat {
        switch self {
        case .compact:
            return 14
        case .regular:
            return 16
        case .spacious:
            return 18
        }
    }

    var previewTitleFont: Font {
        switch self {
        case .compact:
            return .caption
        case .regular:
            return .callout
        case .spacious:
            return .headline
        }
    }

    var queueItemSpacing: CGFloat {
        switch self {
        case .compact:
            return 2
        case .regular:
            return 4
        case .spacious:
            return 6
        }
    }

    var previewGroupSpacing: CGFloat {
        switch self {
        case .compact:
            return 8
        case .regular:
            return 10
        case .spacious:
            return 12
        }
    }
}

private func tetrominoColor(for type: TetrominoType) -> Color {
    switch type {
    case .i:
        return Color(red: 0, green: 0.94, blue: 0.94)
    case .o:
        return Color(red: 0.94, green: 0.94, blue: 0)
    case .t:
        return Color(red: 0.63, green: 0, blue: 0.94)
    case .s:
        return Color(red: 0, green: 0.94, blue: 0)
    case .z:
        return Color(red: 0.94, green: 0, blue: 0)
    case .j:
        return Color(red: 0, green: 0, blue: 0.94)
    case .l:
        return Color(red: 0.94, green: 0.63, blue: 0)
    default:
        return .gray
    }
}
