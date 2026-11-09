package com.mbr.devicedoctor.domain.snapshot

import kotlin.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** These strings are the format the LLM receives: changing one is a change to the agent's input. */
class SnapshotSerializationTest {
    private val json = Json

    @Test
    fun `battery snapshot with a drain rate`() = assertFormat(
        BatterySnapshot.serializer(),
        BatterySnapshot(23, false, 38.5f, "GOOD", 12.5f, 6f),
        """{"levelPercent":23,"isCharging":false,"temperatureC":38.5,"health":"GOOD",""" +
            """"drainPercentPerHour":12.5,"historyHours":6.0}""",
    )

    @Test
    fun `battery snapshot without enough history keeps an explicit null`() = assertFormat(
        BatterySnapshot.serializer(),
        BatterySnapshot(80, true, 30f, "GOOD", null, 0.25f),
        """{"levelPercent":80,"isCharging":true,"temperatureC":30.0,"health":"GOOD",""" +
            """"drainPercentPerHour":null,"historyHours":0.25}""",
    )

    @Test
    fun `storage snapshot`() = assertFormat(
        StorageSnapshot.serializer(),
        StorageSnapshot(128f, 6.5f, 5),
        """{"totalGb":128.0,"freeGb":6.5,"freePercent":5}""",
    )

    @Test
    fun `app usage writes lastUsed in ISO-8601`() = assertFormat(
        AppUsage.serializer(),
        AppUsage("com.weatherpro", "WeatherPro", 5, 360, Instant.parse("2026-11-09T10:15:30Z")),
        """{"packageName":"com.weatherpro","label":"WeatherPro","foregroundMinutes":5,""" +
            """"foregroundServiceMinutes":360,"lastUsed":"2026-11-09T10:15:30Z"}""",
    )

    @Test
    fun `app usage with unknown values keeps explicit nulls`() = assertFormat(
        AppUsage.serializer(),
        AppUsage("com.example", "Example", 0, null, null),
        """{"packageName":"com.example","label":"Example","foregroundMinutes":0,""" +
            """"foregroundServiceMinutes":null,"lastUsed":null}""",
    )

    @Test
    fun `app details`() = assertFormat(
        AppDetails.serializer(),
        AppDetails(
            packageName = "com.weatherpro",
            label = "WeatherPro",
            versionName = "4.2",
            lastUpdated = Instant.parse("2026-11-08T14:00:00Z"),
            installedFrom = "com.android.vending",
            sensitivePermissionsGranted = listOf("ACCESS_BACKGROUND_LOCATION"),
            appSizeMb = 80.5f,
            dataSizeMb = 12f,
            cacheSizeMb = null,
        ),
        """{"packageName":"com.weatherpro","label":"WeatherPro","versionName":"4.2",""" +
            """"lastUpdated":"2026-11-08T14:00:00Z","installedFrom":"com.android.vending",""" +
            """"sensitivePermissionsGranted":["ACCESS_BACKGROUND_LOCATION"],""" +
            """"appSizeMb":80.5,"dataSizeMb":12.0,"cacheSizeMb":null}""",
    )

    @Test
    fun `app size`() = assertFormat(
        AppSize.serializer(),
        AppSize("com.example", "Example", 512.5f),
        """{"packageName":"com.example","label":"Example","totalMb":512.5}""",
    )

    @Test
    fun `app data usage`() = assertFormat(
        AppDataUsage.serializer(),
        AppDataUsage("com.example", "Example", 150.5f, 1024f),
        """{"packageName":"com.example","label":"Example","mobileMb":150.5,"wifiMb":1024.0}""",
    )

    @Test
    fun `network snapshot without bandwidth`() = assertFormat(
        NetworkSnapshot.serializer(),
        NetworkSnapshot("NONE", false, false, null, null),
        """{"type":"NONE","isMetered":false,"isValidated":false,"downstreamKbps":null,"upstreamKbps":null}""",
    )

    @Test
    fun `network snapshot with bandwidth`() = assertFormat(
        NetworkSnapshot.serializer(),
        NetworkSnapshot("WIFI", false, true, 50000, 10000),
        """{"type":"WIFI","isMetered":false,"isValidated":true,"downstreamKbps":50000,"upstreamKbps":10000}""",
    )

    private fun <T> assertFormat(serializer: KSerializer<T>, value: T, expected: String) {
        assertEquals(expected, json.encodeToString(serializer, value))
        assertEquals(value, json.decodeFromString(serializer, expected))
    }
}
