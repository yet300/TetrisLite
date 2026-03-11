import SwiftUI

extension View {
    @ViewBuilder
    func gameHoverEffect() -> some View {
        #if os(iOS) || os(visionOS)
        self.hoverEffect(.highlight)
        #else
        self
        #endif
    }
}
