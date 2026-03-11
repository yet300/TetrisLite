import SwiftUI
import Shared

struct HistoryView: View {
    private let component: HistoryComponent
    
    @StateValue
    private var model: HistoryComponentModel
    
    init(_ component: HistoryComponent) {
        self.component = component
        _model = StateValue(component.model)
    }
    
    var body: some View {
        NavigationStack {
            ZStack {
                switch model {
                case is HistoryComponentModelLoading:
                    ProgressView()
                case let content as HistoryComponentModelContent:
                    if content.games.isEmpty && content.totalGamesCount == 0 {
                        EmptyHistoryView()
                    } else {
                        List {
                                ProgressionSummaryCard(progression: content.progression)
                                    .listRowSeparator(.hidden)
                                    .listRowBackground(Color.clear)

                                if content.games.isEmpty {
                                    FilteredEmptyHistoryView(totalGamesCount: Int(truncatingIfNeeded: content.totalGamesCount))
                                        .listRowSeparator(.hidden)
                                        .listRowBackground(Color.clear)
                                }

                                ForEach(content.games, id: \.id) { game in
                                    GameRecordCard(game: game)
                                    .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                                        Button(role: .destructive) {
                                            component.onDeleteGame(id: game.id)
                                        } label: {
                                            Label("Delete", systemImage: "trash.fill")
                                        }
                                    }
                                    .listRowSeparator(.hidden)
                                    .listRowBackground(Color.clear)
                                }
                            }
                            .listStyle(.plain)
                    }
                default:
                    EmptyView()
                }
            }
            .navigationTitle(Strings.history)
            .toolbar {
                #if os(iOS)
                ToolbarItem(placement: .navigationBarLeading) {
                    filterMenu
                }

                ToolbarItem(placement: .navigationBarTrailing) {
                    dismissButton
                }
                #else
                ToolbarItem(placement: .cancellationAction) {
                    dismissButton
                } 
                ToolbarItem(placement: .primaryAction) {
                    filterMenu
                }
                #endif
            }
        }
    }
    
    private var filterMenu: some View {
        Menu {
            ForEach([DateFilter.all, DateFilter.today, DateFilter.thisWeek, DateFilter.thisMonth], id: \.self) { filter in
                Button(filter.name) {
                    component.onFilterChanged(filter: filter)
                }
            }
        } label: {
            Image(systemName: "line.3.horizontal.decrease.circle")
        }
    }
    
    
    private var dismissButton: some View {
        Button(action: {
            component.onDismiss()
        }) {
            Image(systemName: "xmark")
        }
    }
}

private struct ProgressionSummaryCard: View {
    let progression: ProgressionSummary

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(Strings.careerSummaryTitle)
                .font(.headline)
                .foregroundColor(.gamePrimaryLabel)

            Text(Strings.bestScoreValue(Int(truncatingIfNeeded: progression.bestScore)))
            Text(Strings.highestLevelValue(Int(truncatingIfNeeded: progression.highestLevel)))
            Text(Strings.totalLinesValue(Int(truncatingIfNeeded: progression.totalLines)))
            Text(Strings.totalTetrisesValue(Int(truncatingIfNeeded: progression.totalTetrises)))
            Text(Strings.totalTSpinsValue(Int(truncatingIfNeeded: progression.totalTSpins)))
            Text(
                Strings.achievementsUnlockedValue(
                    progression.unlockedAchievements.count,
                    Int(truncatingIfNeeded: progression.totalAchievements)
                )
            )
            .foregroundColor(.secondary)

            if !progression.unlockedAchievements.isEmpty {
                VStack(alignment: .leading, spacing: 6) {
                    ForEach(progression.unlockedAchievements, id: \.self) { achievement in
                        Label(Strings.achievementTitle(achievement), systemImage: "medal.fill")
                            .font(.caption.weight(.semibold))
                            .foregroundColor(.gamePrimaryLabel)
                    }
                }
                .padding(.top, 4)
            }
        }
        .font(.subheadline)
        .foregroundColor(.gameLabel)
        .padding()
        .background(.thinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
    }
}

struct GameRecordCard: View {
    let game: GameRecord
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Image(systemName: "gamecontroller.fill")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text(game.difficulty.name)
                    .font(.caption)
                    .fontWeight(.semibold)
                    .foregroundColor(.secondary)
                
                Spacer()
                
                Text(formatDate(game.timestamp))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding(.bottom, 4)

            Text(Strings.scoreLabel(Int(game.score)))
                .font(.headline)
                .fontWeight(.semibold)
                .foregroundColor(.primary)
            
            Text(Strings.linesLabel(Int(game.linesCleared)))
                .font(.subheadline)
                .foregroundColor(.secondary)

            Text("Level \(game.level) • \(formatDuration(game.durationMs)) • \(game.piecesPlaced) pieces")
                .font(.subheadline)
                .foregroundColor(.secondary)

            Text("Max combo \(game.maxCombo) • Tetrises \(game.tetrisesCleared) • T-Spins \(game.tSpinClears)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .background(.thinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        .shadow(color: .black.opacity(0.05), radius: 5, x: 0, y: 2)
    }
    
    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }

    private func formatDuration(_ milliseconds: Int64) -> String {
        let seconds = (milliseconds / 1000) % 60
        let minutes = (milliseconds / 1000) / 60
        return "\(minutes):" + String(format: "%02d", seconds)
    }
}

struct EmptyHistoryView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "clock")
                .font(.system(size: 64))
                .foregroundColor(.accent)
            Text(Strings.noGamesYet)
                .font(.title2)
                .foregroundColor(.gameLabel)
            Text(Strings.startGamePrompt)
                .font(.subheadline)
                .foregroundColor(.gameLabel)
        }
    }
}

struct FilteredEmptyHistoryView: View {
    let totalGamesCount: Int

    var body: some View {
        VStack(spacing: 10) {
            Image(systemName: "line.3.horizontal.decrease.circle")
                .font(.system(size: 40))
                .foregroundColor(.accent)
            Text(Strings.historyEmptyFilter)
                .font(.headline)
                .foregroundColor(.gameLabel)
            Text(Strings.gamesPlayedValue(totalGamesCount))
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(.thinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
    }
}

struct HistoryView_Previews: PreviewProvider {
    static var previews: some View {
        HistoryView(PreviewHistoryComponent())
    }
}
