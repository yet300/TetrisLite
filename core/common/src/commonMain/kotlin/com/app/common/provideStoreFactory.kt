package com.app.common

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.core.annotation.Single

@Single
fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
