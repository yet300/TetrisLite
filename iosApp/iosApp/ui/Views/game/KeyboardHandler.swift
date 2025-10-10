import SwiftUI
import Shared

#if os(macOS)
struct KeyboardAwareViewModifier: ViewModifier {
    let onKey: (String) -> Void
    
    func body(content: Content) -> some View {
        content
            .background(KeyboardHandler(onKey: onKey))
    }
}

private struct KeyboardHandler: NSViewRepresentable {
    let onKey: (String) -> Void
    
    func makeNSView(context: Context) -> NSView {
        let view = KeyCatcherView()
        view.onKey = onKey
        return view
    }
    func updateNSView(_ nsView: NSView, context: Context) {}
    
    private class KeyCatcherView: NSView {
        var onKey: ((String) -> Void)?
        override var acceptsFirstResponder: Bool { true }
        override func keyDown(with event: NSEvent) {
            if let chars = event.charactersIgnoringModifiers {
                onKey?(chars)
            }
        }
        override func viewDidMoveToWindow() {
            window?.makeFirstResponder(self)
        }
    }
}
#else
struct KeyboardAwareViewModifier: ViewModifier {
    let onKey: (String) -> Void
    func body(content: Content) -> some View {
        content
            .background(KeyboardHandler(onKey: onKey))
    }
}

import UIKit

private struct KeyboardHandler: UIViewRepresentable {
    let onKey: (String) -> Void
    func makeUIView(context: Context) -> UIView { KeyCatcherView(onKey: onKey) }
    func updateUIView(_ uiView: UIView, context: Context) {}
    
    private class KeyCatcherView: UIView {
        let onKey: (String) -> Void
        init(onKey: @escaping (String) -> Void) {
            self.onKey = onKey
            super.init(frame: .zero)
            self.becomeFirstResponder()
        }
        required init?(coder: NSCoder) { fatalError() }
        override var canBecomeFirstResponder: Bool { true }
        override func pressesBegan(_ presses: Set<UIPress>, with event: UIPressesEvent?) {
            for press in presses {
                if let key = press.key {
                    onKey(key.characters)
                }
            }
        }
    }
}
#endif

extension View {
    func keyboardAware(onKey: @escaping (String) -> Void) -> some View {
        modifier(KeyboardAwareViewModifier(onKey: onKey))
    }
}
