package com.yet.tetris.ui.theme

import js.objects.unsafeJso
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import react.FC
import react.PropsWithChildren
import react.RequiredContext
import react.StateInstance
import react.createRequiredContext
import react.useState

/**
 * MUI theme matching the Compose theme colors
 * Using Kotlin Wrappers 2025.10.11+ API
 */
object AppThemes {
    val Light =
        createTheme(
            unsafeJso {
                palette =
                    unsafeJso {
                        mode = PaletteMode.light
                        primary =
                            unsafeJso {
                                main = AppColors.Light.primary
                                contrastText = AppColors.Light.onPrimary
                            }
                        secondary =
                            unsafeJso {
                                main = AppColors.Light.secondary
                                contrastText = AppColors.Light.onSecondary
                            }
                        error =
                            unsafeJso {
                                main = AppColors.Light.error
                                contrastText = AppColors.Light.onError
                            }
                        background =
                            unsafeJso {
                                default = AppColors.Light.background.toString()
                                paper = AppColors.Light.surface.toString()
                            }

                        text =
                            unsafeJso {
                                primary = AppColors.Light.onBackground
                                secondary = AppColors.Light.onSurface
                            }
                    }
                typography =
                    unsafeJso {
                        fontFamily =
                            "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif"
                    }
            },
        )

    val Dark =
        createTheme(
            unsafeJso {
                palette =
                    unsafeJso {
                        mode = PaletteMode.dark
                        primary =
                            unsafeJso {
                                main = AppColors.Dark.primary
                                contrastText = AppColors.Dark.onPrimary
                            }
                        secondary =
                            unsafeJso {
                                main = AppColors.Dark.secondary
                                contrastText = AppColors.Dark.onSecondary
                            }
                        error =
                            unsafeJso {
                                main = AppColors.Dark.error
                                contrastText = AppColors.Dark.onError
                            }

                        background =
                            unsafeJso {
                                default = AppColors.Dark.background.toString()
                                paper = AppColors.Dark.surface.toString()
                            }
                        text =
                            unsafeJso {
                                primary = AppColors.Dark.onBackground
                                secondary = AppColors.Dark.onSurface
                            }
                    }
                typography =
                    unsafeJso {
                        fontFamily =
                            "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif"
                    }
            },
        )
}

val ThemeModule =
    FC<PropsWithChildren> { props ->
        val state = useState(AppThemes.Light)
        val (theme) = state

        ThemeContext(state) {
            ThemeProvider {
                this.theme = theme

                CssBaseline()
                +props.children
            }
        }
    }

val ThemeContext: RequiredContext<StateInstance<Theme>> =
    createRequiredContext()
