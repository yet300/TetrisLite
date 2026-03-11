//
//  GlassPanelStyle.swift
//  iosApp
//
//  Created by Ruslan Gadzhiev on 09.10.25.
//
import SwiftUI


struct GlassPanelStyle: ViewModifier {
    var cornerRadius: CGFloat = 20
    
    func body(content: Content) -> some View {
        content
            .background(
                ZStack {
                    // Глянцевый блик для иллюзии отражения
                    LinearGradient(
                        colors: [.white.opacity(0.25), .white.opacity(0.05)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                }
            )
            // Обводка для подчеркивания краев
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(
                        LinearGradient(
                            colors: [.white.opacity(0.4), .white.opacity(0.2)],
                            startPoint: .top,
                            endPoint: .bottom
                        ),
                        lineWidth: 1.5
                    )
            )
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 5)
    }
}

extension View {
    func glassPanelStyle(cornerRadius: CGFloat = 20) -> some View {
        self.modifier(GlassPanelStyle(cornerRadius: cornerRadius))
    }
}
