package com.mbr.devicedoctor.domain.snapshot

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BatterySnapshot(
    val levelPercent: Int,
    val isCharging: Boolean,
    val temperatureC: Float,
    /** GOOD, OVERHEAT, ... */
    val health: String,
    /** Null when there is not enough history to compute a drain rate. */
    val drainPercentPerHour: Float?,
    /** How much history [drainPercentPerHour] is based on. */
    val historyHours: Float,
)

@Serializable
data class StorageSnapshot(val totalGb: Float, val freeGb: Float, val freePercent: Int)

@Serializable
data class AppUsage(
    val packageName: String,
    val label: String,
    val foregroundMinutes: Int,
    /** Null below API 29. */
    val foregroundServiceMinutes: Int?,
    val lastUsed: Instant?,
)

@Serializable
data class AppDetails(
    val packageName: String,
    val label: String,
    val versionName: String?,
    val lastUpdated: Instant,
    val installedFrom: String?,
    /** Location (including background), camera, microphone, etc. */
    val sensitivePermissionsGranted: List<String>,
    val appSizeMb: Float?,
    val dataSizeMb: Float?,
    val cacheSizeMb: Float?,
)

@Serializable
data class AppSize(val packageName: String, val label: String, val totalMb: Float)

@Serializable
data class AppDataUsage(val packageName: String, val label: String, val mobileMb: Float, val wifiMb: Float)

@Serializable
data class NetworkSnapshot(
    /** WIFI, CELLULAR or NONE. */
    val type: String,
    val isMetered: Boolean,
    /** True when internet is actually reachable. */
    val isValidated: Boolean,
    val downstreamKbps: Int?,
    val upstreamKbps: Int?,
)
