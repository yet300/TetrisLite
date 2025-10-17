package com.yet.tetris.database.datetime

import kotlinx.datetime.internal.JsModule
import kotlinx.datetime.internal.JsNonModule

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class)
@JsExport
val jsJodaTz = JsJodaTimeZoneModule
