package com.mbr.devicedoctor.domain.confirmation

import kotlin.concurrent.Volatile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

data class ConfirmationRequest(val id: String, val title: String, val reason: String)

/**
 * The human-in-the-loop checkpoint: every action tool suspends here until the user answers.
 *
 * Concurrent [ask] calls are served one at a time, and each one's [timeout] starts when it reaches the user.
 * No answer, for any reason, counts as a refusal.
 */
class ConfirmationGate(private val timeout: Duration = 60.seconds) {
    private class ActiveRequest(val id: String, val answer: CompletableDeferred<Boolean>)

    private val queue = Mutex()
    private val _pending = MutableStateFlow<ConfirmationRequest?>(null)

    @Volatile
    private var active: ActiveRequest? = null

    /** The request currently shown to the user, or null. */
    val pending: StateFlow<ConfirmationRequest?> = _pending.asStateFlow()

    /** Suspends until the user answers. Returns false on refusal or timeout. */
    suspend fun ask(request: ConfirmationRequest): Boolean = queue.withLock {
        val answer = CompletableDeferred<Boolean>()
        active = ActiveRequest(request.id, answer)
        _pending.value = request
        try {
            withTimeoutOrNull(timeout) { answer.await() } ?: false
        } finally {
            active = null
            _pending.value = null
        }
    }

    /** Ignored unless [requestId] is the pending request, so a late tap cannot answer a newer one. */
    fun respond(requestId: String, accepted: Boolean) {
        active?.takeIf { it.id == requestId }?.answer?.complete(accepted)
    }
}
