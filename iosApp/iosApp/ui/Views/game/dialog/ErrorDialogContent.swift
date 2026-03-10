import SwiftUI
import Shared

struct ErrorDialogContent: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        Image(systemName: "exclamationmark.triangle.fill")
            .font(.system(size: 48))
            .foregroundColor(.gameSystemRed)

        VStack {
            Text(Strings.errorTitle)
                .font(.title.bold())
                .foregroundColor(.gameErrorLabel)
            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(.gameSecondaryLabel)
        }

        GlassDialogButton(title: Strings.ok, isPrimary: true, action: onDismiss)
    }
}
