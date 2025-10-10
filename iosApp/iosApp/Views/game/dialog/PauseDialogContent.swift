import SwiftUI
import Shared

 struct PauseDialogContent: View {
    let component: GameComponent

    var body: some View {
        Text(Strings.gamePaused)
            .font(.largeTitle.bold())
            .foregroundColor(.white)

        VStack(spacing: 16) {
            GlassDialogButton(title: Strings.resume, isPrimary: true, action: component.onResume)
            GlassDialogButton(title: Strings.quit, action: component.onQuit)
        }
    }
}


