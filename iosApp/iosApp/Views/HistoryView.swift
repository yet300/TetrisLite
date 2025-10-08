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
        NavigationView {
            ZStack {
                switch model {
                case is HistoryComponentModelLoading:
                    ProgressView()
                case let content as HistoryComponentModelContent:
                    if(content.games.isEmpty) {
                        EmptyHistoryView()
                    } else {
                        ScrollView {
                            LazyVStack(spacing: 12) {
                                ForEach(content.games, id: \.id) { game in
                                    GameRecordCard(game: game) {
                                        component.onDeleteGame(id: game.id)
                                    }
                                }
                            }
                            .padding()
                        }
                    }
                default:
                    EmptyView()
                }
            }
            .navigationTitle("Game History")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
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
            }
        }
    }
}

struct GameRecordCard: View {
    let game: GameRecord
    let onDelete: () -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 8) {
                Text("Score: \(game.score)")
                    .font(.headline)
                Text("Lines: \(game.linesCleared)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text("Difficulty: \(game.difficulty.name)")
                    .font(.caption)
                    .foregroundColor(.secondary)
                Text(formatDate(game.timestamp))
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Button(role: .destructive) {
                onDelete()
            } label: {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
        }
        .padding()
        .background(.regularMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
    
    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct EmptyHistoryView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "clock")
                .font(.system(size: 64))
                .foregroundColor(.secondary)
            Text("No games played yet")
                .font(.title2)
                .foregroundColor(.secondary)
            Text("Start a new game to see your history")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
    }
}
