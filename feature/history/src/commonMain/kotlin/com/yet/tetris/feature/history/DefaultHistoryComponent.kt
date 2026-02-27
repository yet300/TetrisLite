package com.yet.tetris.feature.history

import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.feature.history.di.HISTORY_COMPONENT_FACTORY_QUALIFIER
import com.yet.tetris.feature.history.integration.stateToModel
import com.yet.tetris.feature.history.store.HistoryStore
import com.yet.tetris.feature.history.store.HistoryStoreFactory
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

internal class DefaultHistoryComponent(
    componentContext: ComponentContext,
    private val dismiss: () -> Unit,
    private val historyStoreFactory: HistoryStoreFactory,
) : ComponentContext by componentContext,
    HistoryComponent {
    private val store = instanceKeeper.getStore { historyStoreFactory.create() }

    init {
        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is HistoryStore.Label.ShowError -> {
                        // Handle error
                    }

                    is HistoryStore.Label.GameDeleted -> {
                        // Could show confirmation
                    }
                }
            }
        }
    }

    override val model: Value<HistoryComponent.Model> =
        store.asValue().map(stateToModel)

    override fun onDismiss() = dismiss()

    override fun onRefresh() {
        store.accept(HistoryStore.Intent.Refresh)
    }

    override fun onFilterChanged(filter: DateFilter) {
        store.accept(HistoryStore.Intent.FilterByDate(filter))
    }

    override fun onDeleteGame(id: String) {
        store.accept(HistoryStore.Intent.DeleteGame(id))
    }
}

@Factory
@Named(HISTORY_COMPONENT_FACTORY_QUALIFIER)
internal class DefaultHistoryComponentFactory
    @Inject
    constructor(
        private val historyStoreFactory: HistoryStoreFactory,
    ) : HistoryComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            dismiss: () -> Unit,
        ): HistoryComponent =
            DefaultHistoryComponent(
                componentContext = componentContext,
                dismiss = dismiss,
                historyStoreFactory = historyStoreFactory,
            )
    }
