package com.salesfg.rickmorty

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

expect val mainDispatcher: CoroutineDispatcher

abstract class BaseViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(block = block)
    }

    fun clear() {
        viewModelScope.cancel()
    }
}

fun <T> Flow<T>.asCFlow(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Job {
        return CoroutineScope(mainDispatcher).launch {
            origin.collect {
                block(it)
            }
        }
    }
}