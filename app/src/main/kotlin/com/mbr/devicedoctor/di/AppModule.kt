package com.mbr.devicedoctor.di

import com.mbr.devicedoctor.domain.confirmation.ConfirmationGate
import com.mbr.devicedoctor.domain.time.AppDispatchers
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/** Also hosts the objects of `:core:domain`, which stays free of Koin. */
val appModule = module {
    single<Clock> { Clock.System }
    single<AppDispatchers> { DefaultAppDispatchers }
    single { ConfirmationGate() }
}

private object DefaultAppDispatchers : AppDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
