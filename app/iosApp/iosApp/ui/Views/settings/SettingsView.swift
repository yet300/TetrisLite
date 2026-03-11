import SwiftUI
import Shared

struct SettingsView: View {
    private let component: SettingsComponent
        
    @StateValue
    private var model: SettingsComponentModel
    
    init(_ component: SettingsComponent) {
        self.component = component
        _model = StateValue(component.model)
    }
    
    var body: some View {
        NavigationStack {
            Form {
                visualThemeSection
                pieceStyleSection
                audioSection
                controlsSection
            }
            .formStyle(.grouped)
            .navigationTitle(Strings.settings)
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar {
                #if os(iOS)
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { component.onClose() }) {
                        Image(systemName: "xmark")
                            .imageScale(.large)
                    }
                }
                #else
                ToolbarItem(placement: .cancellationAction) {
                    Button(action: { component.onClose() }) {
                        Image(systemName: "xmark")
                            .imageScale(.large)
                    }
                }
                #endif
            }
        }
    }
    // MARK: - Subviews

    private var visualThemeSection: some View {
        Section("Visual Theme") {
            Picker("Theme", selection: Binding(
                get: { model.settings.themeConfig.visualTheme },
                set: { component.onVisualThemeChanged(theme: $0) }
            )) {
                ForEach(VisualTheme.entries, id: \.self) { theme in
                    Text(theme.name).tag(theme)
                }
            }
        }
    }
    
    private var pieceStyleSection: some View {
        Section("Piece Style") {
            Picker("Style", selection: Binding(
                get: { model.settings.themeConfig.pieceStyle },
                set: { component.onPieceStyleChanged(style: $0) }
            )) {
                ForEach(PieceStyle.entries, id: \.self) { style in
                    Text(style.name).tag(style)
                }
            }
        }
    }
    
    private var audioSection: some View {
        Section("Audio") {
            Toggle("Music", isOn: Binding(
                get: { model.settings.audioSettings.musicEnabled },
                set: { component.onMusicToggled(enabled: $0) }
            ))
            
            if model.settings.audioSettings.musicEnabled {
                musicSettings
            }
            
            Toggle("Sound Effects", isOn: Binding(
                get: { model.settings.audioSettings.soundEffectsEnabled },
                set: { component.onSoundEffectsToggled(enabled: $0) }
            ))
            
            if model.settings.audioSettings.soundEffectsEnabled {
                soundEffectsSettings
            }
        }
    }

    private var controlsSection: some View {
        Section("Controls") {
            Picker("Primary Rotation", selection: Binding(
                get: { model.settings.controlSettings.primaryRotateDirection },
                set: { component.onPrimaryRotateDirectionChanged(direction: $0) }
            )) {
                ForEach(RotationDirection.entries, id: \.self) { direction in
                    Text(rotationLabel(direction)).tag(direction)
                }
            }

            Toggle("Enable 180 Rotation", isOn: Binding(
                get: { model.settings.controlSettings.enable180Rotation },
                set: { component.on180RotationToggled(enabled: $0) }
            ))

            Picker("Gesture Sensitivity", selection: Binding(
                get: { model.settings.controlSettings.gestureSensitivity },
                set: { component.onGestureSensitivityChanged(sensitivity: $0) }
            )) {
                ForEach(GestureSensitivity.entries, id: \.self) { sensitivity in
                    Text(gestureLabel(sensitivity)).tag(sensitivity)
                }
            }
        }
    }

    @ViewBuilder
    private var musicSettings: some View {
            VStack(alignment: .leading) {
                Text(Strings.musicVolume)
                Slider(value: Binding(
                    get: { Double(model.settings.audioSettings.musicVolume) },
                    set: { component.onMusicVolumeChanged(volume: Float($0)) }
                ), in: 0...1)
            }
            
            Picker("Music Theme", selection: Binding(
                get: { model.settings.audioSettings.selectedMusicTheme },
                set: { component.onMusicThemeChanged(theme: $0) }
            )) {
                ForEach(MusicTheme.entries, id: \.self) { theme in
                    Text(musicThemeLabel(theme)).tag(theme)
                }
            }
    }

    private var soundEffectsSettings: some View {
        VStack(alignment: .leading) {
            Text(Strings.sfxVolume)
            Slider(value: Binding(
                get: { Double(model.settings.audioSettings.sfxVolume) },
                set: { component.onSFXVolumeChanged(volume: Float($0)) }
            ), in: 0...1)
        }
    }

    private func rotationLabel(_ direction: RotationDirection) -> String {
        switch direction {
        case .clockwise:
            return "CW"
        case .counterclockwise:
            return "CCW"
        case .oneEighty:
            return "180"
        default:
            return direction.name
        }
    }

    private func gestureLabel(_ sensitivity: GestureSensitivity) -> String {
        switch sensitivity {
        case .relaxed:
            return "Relaxed"
        case .normal:
            return "Normal"
        case .competitive:
            return "Competitive"
        default:
            return sensitivity.name
        }
    }

    private func musicThemeLabel(_ theme: MusicTheme) -> String {
        switch theme {
        case .classic:
            return "Classic"
        case .modern:
            return "Modern"
        case .minimal:
            return "Minimal"
        case .arcade:
            return "Arcade"
        case .dusk:
            return "Dusk"
        case .battle:
            return "Battle"
        case .none:
            return "Off"
        default:
            return theme.name
        }
    }
}
