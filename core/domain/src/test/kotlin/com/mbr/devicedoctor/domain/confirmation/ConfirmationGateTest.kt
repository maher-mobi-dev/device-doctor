package com.mbr.devicedoctor.domain.confirmation

import app.cash.turbine.test
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmationGateTest {
    private val timeout = 60.seconds
    private val gate = ConfirmationGate(timeout)
    private val first = ConfirmationRequest(id = "first", title = "WeatherPro", reason = "High background use")
    private val second = ConfirmationRequest(id = "second", title = "Battery", reason = "Check saver mode")

    @Test
    fun `ask suspends until the user accepts`() = runTest {
        gate.pending.test {
            assertNull(awaitItem())

            val answer = async { gate.ask(first) }
            assertEquals(first, awaitItem())
            assertFalse(answer.isCompleted)

            gate.respond(first.id, accepted = true)

            assertTrue(answer.await())
            assertNull(awaitItem())
        }
    }

    @Test
    fun `ask returns false when the user declines`() = runTest {
        gate.pending.test {
            assertNull(awaitItem())

            val answer = async { gate.ask(first) }
            assertEquals(first, awaitItem())

            gate.respond(first.id, accepted = false)

            assertFalse(answer.await())
            assertNull(awaitItem())
        }
    }

    @Test
    fun `timeout counts as a refusal and clears pending`() = runTest {
        gate.pending.test {
            assertNull(awaitItem())

            val answer = async { gate.ask(first) }
            assertEquals(first, awaitItem())

            advanceTimeBy(timeout - 1.seconds)
            runCurrent()
            assertFalse(answer.isCompleted)

            advanceTimeBy(1.seconds)
            runCurrent()

            assertFalse(answer.await())
            assertNull(awaitItem())
        }
    }

    @Test
    fun `cancelling the caller clears pending`() = runTest {
        gate.pending.test {
            assertNull(awaitItem())

            val answer = async { gate.ask(first) }
            assertEquals(first, awaitItem())

            answer.cancelAndJoin()

            assertTrue(answer.isCancelled)
            assertNull(awaitItem())
        }
    }

    @Test
    fun `successive requests are answered independently`() = runTest {
        gate.pending.test {
            assertNull(awaitItem())

            val firstAnswer = async { gate.ask(first) }
            assertEquals(first, awaitItem())
            gate.respond(first.id, accepted = true)
            assertTrue(firstAnswer.await())
            assertNull(awaitItem())

            val secondAnswer = async { gate.ask(second) }
            assertEquals(second, awaitItem())
            gate.respond(second.id, accepted = false)
            assertFalse(secondAnswer.await())
            assertNull(awaitItem())
        }
    }

    @Test
    fun `concurrent requests reach the user one at a time`() = runTest {
        val firstAnswer = async { gate.ask(first) }
        val secondAnswer = async { gate.ask(second) }
        runCurrent()
        assertEquals(first, gate.pending.value)

        gate.respond(second.id, accepted = true)
        runCurrent()
        assertFalse(secondAnswer.isCompleted)

        gate.respond(first.id, accepted = true)
        runCurrent()
        assertTrue(firstAnswer.await())
        assertEquals(second, gate.pending.value)

        gate.respond(second.id, accepted = false)
        assertFalse(secondAnswer.await())
        assertNull(gate.pending.value)
    }

    @Test
    fun `a queued request gets its full timeout once it reaches the user`() = runTest {
        val firstAnswer = async { gate.ask(first) }
        val secondAnswer = async { gate.ask(second) }
        runCurrent()

        advanceTimeBy(timeout - 1.seconds)
        gate.respond(first.id, accepted = true)
        runCurrent()
        assertTrue(firstAnswer.await())

        advanceTimeBy(timeout - 1.seconds)
        runCurrent()
        assertEquals(second, gate.pending.value)
        assertFalse(secondAnswer.isCompleted)

        advanceTimeBy(1.seconds)
        runCurrent()
        assertFalse(secondAnswer.await())
    }

    @Test
    fun `a late answer cannot accept a newer request`() = runTest {
        val expired = async { gate.ask(first) }
        runCurrent()
        advanceTimeBy(timeout)
        runCurrent()
        assertFalse(expired.await())

        val current = async { gate.ask(second) }
        runCurrent()

        gate.respond(first.id, accepted = true)
        runCurrent()
        assertFalse(current.isCompleted)
        assertEquals(second, gate.pending.value)

        gate.respond(second.id, accepted = false)
        assertFalse(current.await())
    }

    @Test
    fun `an answer with nothing pending has no effect on the next request`() = runTest {
        gate.respond(first.id, accepted = true)
        assertNull(gate.pending.value)

        val answer = async { gate.ask(first) }
        runCurrent()
        assertFalse(answer.isCompleted)

        gate.respond(first.id, accepted = false)
        assertFalse(answer.await())
    }
}
