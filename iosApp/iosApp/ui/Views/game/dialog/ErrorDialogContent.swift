import SwiftUI
import Shared

struct ErrorDialogContent: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        Image(systemName: "exclamationmark.triangle.fill")
            .font(.system(size: 48))
            .foregroundColor(.systemRed)

        VStack {
            Text(Strings.errorTitle)
                .font(.title.bold())
                .foregroundColor(.errorLabel)
            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondaryLabel)
        }

        GlassDialogButton(title: Strings.ok, isPrimary: true, action: onDismiss)
    }
}
