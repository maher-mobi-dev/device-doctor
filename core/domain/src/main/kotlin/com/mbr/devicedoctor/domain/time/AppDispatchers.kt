package com.mbr.devicedoctor.domain.time

import kotlinx.coroutines.CoroutineDispatcher

/** Injected so that logic never hardcodes a dispatcher. Time is injected as `kotlin.time.Clock`. */
interface AppDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
