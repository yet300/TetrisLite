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

    func updateNSView(_ nsView: NSView, context: Context) {
        (nsView as? KeyCatcherView)?.onKey = onKey
    }

    private class KeyCatcherView: NSView {
        var onKey: ((String) -> Void)?
        private var localMonitor: Any?

        override var acceptsFirstResponder: Bool { true }

        override func keyDown(with event: NSEvent) {
            if handleKeyEvent(event) {
                return
            }
            super.keyDown(with: event)
        }

        override func viewDidMoveToWindow() {
            super.viewDidMoveToWindow()
            installLocalMonitorIfNeeded()
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                self.window?.makeFirstResponder(self)
            }
        }

        override func viewWillMove(toWindow newWindow: NSWindow?) {
            super.viewWillMove(toWindow: newWindow)
            if newWindow == nil {
                removeLocalMonitor()
            }
        }

        deinit {
            removeLocalMonitor()
        }

        private func handleKeyEvent(_ event: NSEvent) -> Bool {
            switch event.keyCode {
            case 123:
                onKey?("directionleft")
                return true
            case 124:
                onKey?("directionright")
                return true
            case 125:
                onKey?("directiondown")
                return true
            case 126:
                onKey?("directionup")
                return true
            case 36:
                onKey?("\r")
                return true
            case 53:
                onKey?("\u{1b}")
                return true
            default:
                break
            }

            if let chars = event.charactersIgnoringModifiers, !chars.isEmpty {
                onKey?(chars)
                return true
            }
            return false
        }

        private func installLocalMonitorIfNeeded() {
            guard localMonitor == nil else { return }
            localMonitor =
                NSEvent.addLocalMonitorForEvents(matching: .keyDown) { [weak self] event in
                    guard let self else { return event }
                    guard self.window === event.window else { return event }
                    return self.handleKeyEvent(event) ? nil : event
                }
        }

        private func removeLocalMonitor() {
            if let localMonitor {
                NSEvent.removeMonitor(localMonitor)
                self.localMonitor = nil
            }
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

    func makeUIView(context: Context) -> UIView {
        KeyCatcherView(onKey: onKey)
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        guard let catcherView = uiView as? KeyCatcherView else { return }
        catcherView.onKey = onKey
        catcherView.requestFocus()
    }

    private class KeyCatcherView: UIView {
        var onKey: (String) -> Void
        private var didBecomeActiveObserver: NSObjectProtocol?

        init(onKey: @escaping (String) -> Void) {
            self.onKey = onKey
            super.init(frame: .zero)
            isUserInteractionEnabled = true
            didBecomeActiveObserver =
                NotificationCenter.default.addObserver(
                    forName: UIApplication.didBecomeActiveNotification,
                    object: nil,
                    queue: .main
                ) { [weak self] _ in
                    self?.requestFocus()
                }
            requestFocus()
        }

        required init?(coder: NSCoder) { fatalError() }

        override var canBecomeFirstResponder: Bool { true }

        override var keyCommands: [UIKeyCommand]? {
            [
                keyCommand(UIKeyCommand.inputLeftArrow, action: #selector(handleLeft)),
                keyCommand(UIKeyCommand.inputRightArrow, action: #selector(handleRight)),
                keyCommand(UIKeyCommand.inputUpArrow, action: #selector(handleUp)),
                keyCommand(UIKeyCommand.inputDownArrow, action: #selector(handleDown)),
                keyCommand("\r", action: #selector(handleReturn)),
                keyCommand(UIKeyCommand.inputEscape, action: #selector(handleEscape)),
                keyCommand("a", action: #selector(handleLeft)),
                keyCommand("d", action: #selector(handleRight)),
                keyCommand("w", action: #selector(handleRotate)),
                keyCommand("s", action: #selector(handleDown)),
                keyCommand(" ", action: #selector(handleRotate)),
                keyCommand("h", action: #selector(handleHold)),
                keyCommand("c", action: #selector(handleHold)),
                keyCommand("p", action: #selector(handlePause)),
            ]
        }

        override func didMoveToWindow() {
            super.didMoveToWindow()
            requestFocus()
        }

        override func didMoveToSuperview() {
            super.didMoveToSuperview()
            requestFocus()
        }

        func requestFocus() {
            guard window != nil else { return }
            DispatchQueue.main.async { [weak self] in
                guard let self, self.window != nil else { return }
                if !self.isFirstResponder {
                    _ = self.becomeFirstResponder()
                }
            }
        }

        override func pressesBegan(_ presses: Set<UIPress>, with event: UIPressesEvent?) {
            for press in presses {
                guard let key = press.key else { continue }
                if handleKeyCode(key.keyCode) {
                    continue
                }

                let chars = key.charactersIgnoringModifiers
                if !chars.isEmpty {
                    onKey(chars)
                }
            }
            super.pressesBegan(presses, with: event)
        }

        deinit {
            if let didBecomeActiveObserver {
                NotificationCenter.default.removeObserver(didBecomeActiveObserver)
            }
        }

        private func keyCommand(_ input: String, action: Selector) -> UIKeyCommand {
            UIKeyCommand(input: input, modifierFlags: [], action: action)
        }

        private func handleKeyCode(_ keyCode: UIKeyboardHIDUsage) -> Bool {
            switch keyCode {
            case .keyboardLeftArrow:
                onKey("directionleft")
                return true
            case .keyboardRightArrow:
                onKey("directionright")
                return true
            case .keyboardUpArrow:
                onKey("directionup")
                return true
            case .keyboardDownArrow:
                onKey("directiondown")
                return true
            case .keyboardReturnOrEnter:
                onKey("\r")
                return true
            case .keyboardEscape:
                onKey("\u{1b}")
                return true
            default:
                return false
            }
        }

        @objc private func handleLeft() {
            onKey("directionleft")
        }

        @objc private func handleRight() {
            onKey("directionright")
        }

        @objc private func handleUp() {
            onKey("directionup")
        }

        @objc private func handleDown() {
            onKey("directiondown")
        }

        @objc private func handleReturn() {
            onKey("\r")
        }

        @objc private func handleEscape() {
            onKey("\u{1b}")
        }

        @objc private func handleRotate() {
            onKey("w")
        }

        @objc private func handleHold() {
            onKey("h")
        }

        @objc private func handlePause() {
            onKey("p")
        }
    }
}
#endif

extension View {
    func keyboardAware(onKey: @escaping (String) -> Void) -> some View {
        modifier(KeyboardAwareViewModifier(onKey: onKey))
    }
}
