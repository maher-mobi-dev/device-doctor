package com.mbr.devicedoctor.domain.agent

import com.mbr.devicedoctor.domain.confirmation.ConfirmationRequest
import kotlin.time.Instant

/** What happened during an agent run. Feeds the trace panel, debug logs and evals. */
sealed interface AgentEvent {
    val at: Instant

    data class RunStarted(override val at: Instant, val runId: String, val model: String, val promptVersion: String) :
        AgentEvent

    data class LlmCallFinished(
        override val at: Instant,
        val durationMs: Long,
        val inputTokens: Int?,
        val outputTokens: Int?,
    ) : AgentEvent

    data class ToolCallStarted(override val at: Instant, val tool: String, val argsJson: String) : AgentEvent

    data class ToolCallFinished(
        override val at: Instant,
        val tool: String,
        val resultJson: String,
        val durationMs: Long,
    ) : AgentEvent

    data class ToolCallFailed(override val at: Instant, val tool: String, val error: String) : AgentEvent

    data class ConfirmationAsked(override val at: Instant, val request: ConfirmationRequest) : AgentEvent

    data class FinalAnswer(override val at: Instant, val text: String) : AgentEvent

    /** The run stopped without an answer: limit reached, timeout, network... */
    data class RunFailed(override val at: Instant, val reason: String) : AgentEvent
}
