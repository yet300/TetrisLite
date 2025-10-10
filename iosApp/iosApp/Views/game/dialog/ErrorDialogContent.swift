import SwiftUI
import Shared

struct ErrorDialogContent: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        Image(systemName: "exclamationmark.triangle.fill")
            .font(.system(size: 48))
            .foregroundColor(.yellow)

        VStack {
            Text(Strings.errorTitle).font(.title.bold())
            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(.white.opacity(0.8))
        }
        .foregroundColor(.white)

        GlassDialogButton(title: Strings.ok, isPrimary: true, action: onDismiss)
    }
}
