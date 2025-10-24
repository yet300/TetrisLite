package com.yet.tetris.ui.strings

/**
 * Localized strings for the web application
 * Based on composeApp/src/commonMain/composeResources/values/strings.xml
 *
 * TODO: Add support for multiple languages
 */
object Strings {
    // Common
    const val save = "Save"
    const val discard = "Discard"
    const val cancel = "Cancel"
    const val resume = "Resume"
    const val quit = "Quit"
    const val retry = "Retry"
    const val ok = "Ok"
    const val errorTitle = "An Error Occurred"

    // Home Screen
    const val appTitle = "Tetris Lite"
    const val startNewGame = "Start New Game"
    const val resumeGame = "Resume Game"
    const val difficulty = "Difficulty"

    // Game Screen
    const val next = "Next"
    const val score = "Score"
    const val lines = "Lines"
    const val time = "Time"
    const val gameOver = "Game Over"
    const val backToHome = "Back to Home"
    const val gamePaused = "Game Paused"
    const val pauseMessage = "What would you like to do?"

    // Settings Screen
    const val gameSettings = "Game Settings"
    const val visualTheme = "Visual Theme"
    const val pieceStyle = "Piece Style"
    const val keyboardLayout = "Keyboard Layout"
    const val swipeLayout = "Swipe Layout"
    const val music = "Music"
    const val musicTheme = "Music Theme"
    const val soundEffects = "Sound Effects"
    const val musicVolume = "Music Volume"
    const val sfxVolume = "SFX Volume"
    const val audio = "Audio"

    // History Screen
    const val gameHistory = "Game History"
    const val noGamesYet = "No games played yet"
    const val startGamePrompt = "Start a new game to see your history"

    // Formatted strings
    fun finalScore(score: Long) = "Final Score: $score"
    fun linesCleared(lines: Long) = "Lines Cleared: $lines"
    fun scoreLabel(score: Long) = "Score: $score"
    fun linesLabel(lines: Long) = "Lines: $lines"
    fun difficultyLabel(difficulty: String) = "Difficulty: $difficulty"
}
