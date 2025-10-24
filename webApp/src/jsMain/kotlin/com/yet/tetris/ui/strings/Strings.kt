package com.yet.tetris.ui.strings

/**
 * Localized strings for the web application
 * Based on composeApp/src/commonMain/composeResources/values/strings.xml
 *
 * TODO: Add support for multiple languages
 */
object Strings {
    // Common
    const val SAVE = "Save"
    const val DISCARD = "Discard"
    const val CANCEL = "Cancel"
    const val RESUME = "Resume"
    const val QUIT = "Quit"
    const val RETRY = "Retry"
    const val OK = "Ok"
    const val ERROR_TITLE = "An Error Occurred"

    // Home Screen
    const val APP_TITLE = "Tetris Lite"
    const val START_NEW_GAME = "Start New Game"
    const val RESUME_GAME = "Resume Game"
    const val DIFFICULTY = "Difficulty"

    // Game Screen
    const val NEXT = "Next"
    const val SCORE = "Score"
    const val LINES = "Lines"
    const val TIME = "Time"
    const val GAME_OVER = "Game Over"
    const val BACK_TO_HOME = "Back to Home"
    const val GAME_PAUSED = "Game Paused"
    const val PAUSE_MESSAGE = "What would you like to do?"

    // Settings Screen
    const val GAME_SETTINGS = "Game Settings"
    const val VISUAL_THEME = "Visual Theme"
    const val PIECE_STYLE = "Piece Style"
    const val KEYBOARD_LAYOUT = "Keyboard Layout"
    const val SWIPE_LAYOUT = "Swipe Layout"
    const val MUSIC = "Music"
    const val MUSIC_THEME = "Music Theme"
    const val SOUND_EFFECTS = "Sound Effects"
    const val MUSIC_VOLUME = "Music Volume"
    const val SFX_VOLUME = "SFX Volume"
    const val AUDIO = "Audio"

    // History Screen
    const val GAME_HISTORY = "Game History"
    const val NO_GAMES_YET = "No games played yet"
    const val START_GAME_PROMPT = "Start a new game to see your history"

    // Formatted strings
    fun finalScore(score: Long) = "Final Score: $score"

    fun linesCleared(lines: Long) = "Lines Cleared: $lines"

    fun scoreLabel(score: Long) = "Score: $score"

    fun linesLabel(lines: Long) = "Lines: $lines"

    fun difficultyLabel(difficulty: String) = "Difficulty: $difficulty"
}
