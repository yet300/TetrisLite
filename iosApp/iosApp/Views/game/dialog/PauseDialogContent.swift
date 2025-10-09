import SwiftUI
import Shared

 struct PauseDialogContent: View {
    let component: GameComponent

    var body: some View {
        Text("Game Paused")
            .font(.largeTitle.bold())
            .foregroundColor(.white)

        VStack(spacing: 16) {
            GlassDialogButton(title: "Resume", isPrimary: true, action: component.onResume)
            GlassDialogButton(title: "Quit", action: component.onQuit)
        }
    }
}


