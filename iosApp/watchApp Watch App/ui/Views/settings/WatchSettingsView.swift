import SwiftUI
import Shared

struct WatchSettingsView: View {
    private let component: SettingsComponent

    @StateValue
    private var model: SettingsComponentModel

    init(_ component: SettingsComponent) {
        self.component = component
        _model = StateValue(component.model)
    }

    var body: some View {
        List {
            Section(Strings.game) {

                Picker(Strings.visualTheme, selection: Binding(
                    get: { model.settings.themeConfig.visualTheme },
                    set: { component.onVisualThemeChanged(theme: $0) }
                )) {
                    ForEach(VisualTheme.entries, id: \.self) { theme in
                        Text(theme.name).tag(theme)
                    }
                }
                .pickerStyle(.navigationLink)

                Picker(Strings.pieceStyle, selection: Binding(
                    get: { model.settings.themeConfig.pieceStyle },
                    set: { component.onPieceStyleChanged(style: $0) }
                )) {
                    ForEach(PieceStyle.entries, id: \.self) { style in
                        Text(style.name).tag(style)
                    }
                }
                .pickerStyle(.navigationLink)
            }

            Section(Strings.audio) {
                Toggle(Strings.music, isOn: Binding(
                    get: { model.settings.audioSettings.musicEnabled },
                    set: { component.onMusicToggled(enabled: $0) }
                ))

                if model.settings.audioSettings.musicEnabled {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(Strings.musicVolume)
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                        Slider(value: Binding(
                            get: { Double(model.settings.audioSettings.musicVolume) },
                            set: { component.onMusicVolumeChanged(volume: Float($0)) }
                        ), in: 0...1)
                    }

                    Picker(Strings.musicTheme, selection: Binding(
                        get: { model.settings.audioSettings.selectedMusicTheme },
                        set: { component.onMusicThemeChanged(theme: $0) }
                    )) {
                        ForEach(MusicTheme.entries, id: \.self) { theme in
                            Text(theme.name).tag(theme)
                        }
                    }
                    .pickerStyle(.navigationLink)
                }

                Toggle(Strings.sfx, isOn: Binding(
                    get: { model.settings.audioSettings.soundEffectsEnabled },
                    set: { component.onSoundEffectsToggled(enabled: $0) }
                ))

                if model.settings.audioSettings.soundEffectsEnabled {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(Strings.sfxVolume)
                            .font(.caption2)
                            .foregroundStyle(.secondary)
                        Slider(value: Binding(
                            get: { Double(model.settings.audioSettings.sfxVolume) },
                            set: { component.onSFXVolumeChanged(volume: Float($0)) }
                        ), in: 0...1)
                    }
                }
            }

        }
        .listStyle(.carousel)
        .scrollContentBackground(.hidden)
        .navigationTitle(Strings.settings)
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button(action: { component.onClose() }) {
                    Image(systemName: "chevron.left")
                }
            }
        }
    }
}

#Preview {
    WatchSettingsView(PreviewSettingsComponent())
}
