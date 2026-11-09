package com.mbr.devicedoctor.domain.reader

import com.mbr.devicedoctor.domain.snapshot.AppDataUsage
import com.mbr.devicedoctor.domain.snapshot.AppDetails
import com.mbr.devicedoctor.domain.snapshot.AppSize
import com.mbr.devicedoctor.domain.snapshot.AppUsage
import com.mbr.devicedoctor.domain.snapshot.BatterySnapshot
import com.mbr.devicedoctor.domain.snapshot.NetworkSnapshot
import com.mbr.devicedoctor.domain.snapshot.StorageSnapshot

interface BatteryReader {
    suspend fun snapshot(): BatterySnapshot
}

interface StorageReader {
    suspend fun snapshot(): StorageSnapshot

    suspend fun topAppsBySize(limit: Int): ReaderResult<List<AppSize>>
}

interface UsageReader {
    suspend fun topApps(hours: Int, limit: Int): ReaderResult<List<AppUsage>>
}

interface AppInfoReader {
    suspend fun details(packageName: String): ReaderResult<AppDetails>
}

interface NetworkReader {
    suspend fun snapshot(): NetworkSnapshot

    suspend fun topDataUsage(days: Int, limit: Int): ReaderResult<List<AppDataUsage>>
}

interface SettingsNavigator {
    fun openAppSettings(packageName: String)

    fun openBatterySettings()

    fun openStorageSettings()
}
