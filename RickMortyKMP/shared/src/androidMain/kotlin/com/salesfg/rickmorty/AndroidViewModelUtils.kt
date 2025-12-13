package com.salesfg.rickmorty

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// A implementação actual para o target Android
actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main