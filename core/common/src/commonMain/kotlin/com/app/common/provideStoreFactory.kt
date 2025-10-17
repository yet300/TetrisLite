package com.app.common

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import org.koin.core.annotation.Single

@Single
fun provideStoreFactory(): StoreFactory = LoggingStoreFactory(TimeTravelStoreFactory())
