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
                        List {
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
            .navigationTitle("Game History")
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
}

struct EmptyHistoryView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "clock")
                .font(.system(size: 64))
                .foregroundColor(.accent)
            Text(Strings.noGamesYet)
                .font(.title2)
                .foregroundColor(.label)
            Text(Strings.startGamePrompt)
                .font(.subheadline)
                .foregroundColor(.label)
        }
    }
}

#Preview {
    HistoryView(PreviewHistoryComponent())
}
