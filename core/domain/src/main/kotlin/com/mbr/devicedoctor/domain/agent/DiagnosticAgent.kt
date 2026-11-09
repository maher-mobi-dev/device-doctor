package com.mbr.devicedoctor.domain.agent

import kotlinx.coroutines.flow.Flow

enum class ChatRole { USER, AGENT }

/** One message of the conversation, without tool traces. */
data class ChatTurn(val role: ChatRole, val text: String)

interface DiagnosticAgent {
    /** Cold flow: one run per collection. Emits events, ends with FinalAnswer or RunFailed. */
    fun run(userMessage: String, history: List<ChatTurn>): Flow<AgentEvent>
}
