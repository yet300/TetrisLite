import SwiftUI

struct GlassDialogButton: View {
    let title: String
    var isPrimary: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.headline)
                .fontWeight(.bold)
                .frame(maxWidth: .infinity)
                .padding()
                .background(
                    isPrimary ?
                    AnyShapeStyle(
                        LinearGradient(colors: [.accentColor, .secondaryAccent], startPoint: .leading, endPoint: .trailing)
                    ) :
                    AnyShapeStyle(.ultraThinMaterial)
                )
                .foregroundColor(.primaryLabel)
                .cornerRadius(16)
        }
        .buttonStyle(.plain)
    }
}


struct GlassDialogContainer<Content: View>: View {
    @ViewBuilder let content: Content

    var body: some View {
        ZStack {
            // Full-screen translucent background with blur
            Rectangle()
                .fill(.black.opacity(0.3))
                .background(.ultraThinMaterial)
                .ignoresSafeArea()
            
            // The content panel
            VStack(spacing: 24) {
                content
            }
            .padding(32)
            .glassPanelStyle(cornerRadius: 24) // Re-using our existing style!
            .padding(40)
        }
    }
}
