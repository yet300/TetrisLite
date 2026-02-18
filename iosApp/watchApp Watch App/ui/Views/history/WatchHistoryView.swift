import SwiftUI
import Shared

struct WatchHistoryView: View {
    private let component: HistoryComponent

    @StateValue
    private var model: HistoryComponentModel

    @State private var isShowingFilters = false

    init(_ component: HistoryComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        List {
            switch model {
            case is HistoryComponentModelLoading:
                GlassCard {
                    HStack {
                        ProgressView()
                        Text(Strings.loadingHistory)
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }
                .listRowBackground(Color.clear)
            case let content as HistoryComponentModelContent:
                if content.games.isEmpty {
                    GlassCard {
                        Text(Strings.noGamesYet)
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                    .listRowBackground(Color.clear)
                } else {
                    ForEach(content.games.prefix(6), id: \.id) { record in
                        GlassCard {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(Strings.scoreLabel(Int(record.score)))
                                    .font(.caption)
                                Text("\(Strings.linesLabel(Int(record.linesCleared))) • \(Strings.difficultyLabel(record.difficulty.name.capitalized))")
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                                Text(formatDate(record.timestamp))
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                                    .lineLimit(1)
                            }
                        }
                        .listRowBackground(Color.clear)
                    }
                }
            default:
                EmptyView()
            }
        }
        .listStyle(.carousel)
        .scrollContentBackground(.hidden)
        .navigationTitle(Strings.historyTitle)
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button(action: { component.onDismiss() }) {
                    Image(systemName: "chevron.left")
                }
            }
            ToolbarItem(placement: .primaryAction) {
                Button {
                    isShowingFilters = true
                } label: {
                    Image(systemName: "line.3.horizontal.decrease.circle")
                }
            }
        }
        .confirmationDialog(Strings.filter, isPresented: $isShowingFilters) {
            ForEach([DateFilter.all, DateFilter.today, DateFilter.thisWeek, DateFilter.thisMonth], id: \.self) { filter in
                Button(filter.name) {
                    component.onFilterChanged(filter: filter)
                }
            }
        }
    }

    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

#Preview {
    WatchHistoryView(PreviewHistoryComponent())
}
