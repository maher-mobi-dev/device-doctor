package com.mbr.devicedoctor

import android.app.Application
import com.mbr.devicedoctor.agent.di.agentModule
import com.mbr.devicedoctor.device.di.deviceModule
import com.mbr.devicedoctor.di.appModule
import com.mbr.devicedoctor.feature.chat.di.chatModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DeviceDoctorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DeviceDoctorApp)
            modules(appModule, deviceModule, agentModule, chatModule)
        }
    }
}
