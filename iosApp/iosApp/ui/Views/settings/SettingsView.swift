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
        NavigationView {
            Form {
                visualThemeSection
                pieceStyleSection
                controlsSection
                audioSection
            }
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
    
    private var controlsSection: some View {
        Section("Controls") {
            Picker("Swipe Layout", selection: Binding(
                get: { model.settings.swipeLayout },
                set: { component.onSwipeLayoutChanged(layout: $0) }
            )) {
                ForEach(SwipeLayout.entries, id: \.self) { layout in
                    Text(layout.name).tag(layout)
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
                    Text(theme.name).tag(theme)
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
}
