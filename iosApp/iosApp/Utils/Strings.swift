import Foundation
import Shared

enum Strings {
    // Common
    static let save = NSLocalizedString("save", comment: "")
    static let discard = NSLocalizedString("discard", comment: "")
    static let cancel = NSLocalizedString("cancel", comment: "")
    static let resume = NSLocalizedString("resume", comment: "")
    static let quit = NSLocalizedString("quit", comment: "")
    static let retry = NSLocalizedString("retry", comment: "")
    static let ok = NSLocalizedString("ok", comment: "")
    static let errorTitle = NSLocalizedString("error_title", comment: "")
    static let loading = NSLocalizedString("loading", comment: "")
    static let done = NSLocalizedString("done", comment: "")
    static let game = NSLocalizedString("game", comment: "")
    static let audio = NSLocalizedString("audio", comment: "")
    static let music = NSLocalizedString("music", comment: "")
    static let sfx = NSLocalizedString("sfx", comment: "")
    static let complication = NSLocalizedString("complication", comment: "")
    static let glance = NSLocalizedString("glance", comment: "")
    static let refresh = NSLocalizedString("refresh", comment: "")
    static let filter = NSLocalizedString("filter", comment: "")

    // Home Screen
    static let appTitle = NSLocalizedString("app_title", comment: "")
    static let startNewGame = NSLocalizedString("start_new_game", comment: "")
    static let resumeGame = NSLocalizedString("resume_game", comment: "")
    static let difficulty = NSLocalizedString("difficulty", comment: "")
    static let keyboardHint = NSLocalizedString("keyboard_hint", comment: "")
    static let newGame = NSLocalizedString("new_game", comment: "")
    static let progressTitle = NSLocalizedString("progress_title", comment: "")
    static let careerSummaryTitle = NSLocalizedString("career_summary_title", comment: "")
    static let historyEmptyFilter = NSLocalizedString("history_empty_filter", comment: "")

    // Game Screen
    static let hold = NSLocalizedString("hold", comment: "")
    static let next = NSLocalizedString("next", comment: "")
    static let score = NSLocalizedString("score", comment: "")
    static let lines = NSLocalizedString("lines", comment: "")
    static let level = NSLocalizedString("level", comment: "")
    static let time = NSLocalizedString("time", comment: "")
    static let gameOver = NSLocalizedString("game_over", comment: "")
    static let backToHome = NSLocalizedString("back_to_home", comment: "")
    static let gamePaused = NSLocalizedString("game_paused", comment: "")
    static let paused = NSLocalizedString("paused", comment: "")

    // Settings Screen
    static let settings = NSLocalizedString("settings_volume", comment: "")
    static let musicVolume = NSLocalizedString("music_volume", comment: "")
    static let sfxVolume = NSLocalizedString("sfx_volume", comment: "")
    static let visualTheme = NSLocalizedString("visual_theme", comment: "")
    static let pieceStyle = NSLocalizedString("piece_style", comment: "")
    static let musicTheme = NSLocalizedString("music_theme", comment: "")

    // History Screen
    static let history = NSLocalizedString("history_value", comment: "")
    static let noGamesYet = NSLocalizedString("no_games_yet", comment: "")
    static let startGamePrompt = NSLocalizedString("start_game_prompt", comment: "")
    static let loadingHistory = NSLocalizedString("loading_history", comment: "")
    static let historyTitle = NSLocalizedString("history_title", comment: "")

    // Formatted strings
    static func finalScore(_ score: Int) -> String {
        String(format: NSLocalizedString("final_score", comment: ""), score)
    }

    static func linesCleared(_ lines: Int) -> String {
        String(format: NSLocalizedString("lines_cleared", comment: ""), lines)
    }

    static func scoreLabel(_ score: Int) -> String {
        String(format: NSLocalizedString("score_label", comment: ""), score)
    }

    static func linesLabel(_ lines: Int) -> String {
        String(format: NSLocalizedString("lines_label", comment: ""), lines)
    }

    static func difficultyLabel(_ difficulty: String) -> String {
        String(format: NSLocalizedString("difficulty_label", comment: ""), difficulty)
    }

    static func bestScoreValue(_ score: Int) -> String {
        String(format: NSLocalizedString("best_score_value", comment: ""), score)
    }

    static func highestLevelValue(_ level: Int) -> String {
        String(format: NSLocalizedString("highest_level_value", comment: ""), level)
    }

    static func gamesPlayedValue(_ count: Int) -> String {
        String(format: NSLocalizedString("games_played_value", comment: ""), count)
    }

    static func achievementsUnlockedValue(_ unlocked: Int, _ total: Int) -> String {
        String(format: NSLocalizedString("achievements_unlocked_value", comment: ""), unlocked, total)
    }

    static func difficultyBestScoreValue(_ difficulty: String, _ score: Int) -> String {
        String(format: NSLocalizedString("difficulty_best_score_value", comment: ""), difficulty, score)
    }

    static func difficultyBestLevelValue(_ difficulty: String, _ level: Int) -> String {
        String(format: NSLocalizedString("difficulty_best_level_value", comment: ""), difficulty, level)
    }

    static func totalLinesValue(_ lines: Int) -> String {
        String(format: NSLocalizedString("total_lines_value", comment: ""), lines)
    }

    static func totalTetrisesValue(_ tetrises: Int) -> String {
        String(format: NSLocalizedString("total_tetrises_value", comment: ""), tetrises)
    }

    static func totalTSpinsValue(_ tspins: Int) -> String {
        String(format: NSLocalizedString("total_tspins_value", comment: ""), tspins)
    }

    static func achievementTitle(_ achievement: ProgressAchievementId) -> String {
        switch achievement {
        case .firstGame:
            return NSLocalizedString("achievement_first_game", comment: "")
        case .score5000:
            return NSLocalizedString("achievement_score_5000", comment: "")
        case .score20000:
            return NSLocalizedString("achievement_score_20000", comment: "")
        case .firstTetris:
            return NSLocalizedString("achievement_first_tetris", comment: "")
        case .firstTspin:
            return NSLocalizedString("achievement_first_tspin", comment: "")
        case .combo5:
            return NSLocalizedString("achievement_combo_5", comment: "")
        case .perfectClear:
            return NSLocalizedString("achievement_perfect_clear", comment: "")
        case .tenGames:
            return NSLocalizedString("achievement_ten_games", comment: "")
        default:
            return ""
        }
    }
}
