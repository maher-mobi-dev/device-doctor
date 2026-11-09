# ADR-0003: Human confirmation for every action tool

- **Status:** Draft, to be rewritten by the project owner
- **Date:** 2026-11-09

## Context

The agent's next step is chosen by a remote LLM, and part of what it reads is untrusted: an app label or version can
carry text written to manipulate it. The model's decision to act therefore cannot be treated as the user's decision.

Read tools only return data to the agent. Action tools change something or leave the app.

## Decision

Every action tool suspends on `ConfirmationGate` and proceeds only after an explicit acceptance from the user.
There is no exception and no "always allow".

The gate fails closed:

- A refusal, a timeout and a cancelled caller all mean the action does not happen.
- A request is answered by its id, so a late tap cannot accept a newer request.
- Concurrent requests reach the user one at a time, each with its full timeout.

The check lives in the tool, not in the prompt, so a model that ignores its instructions still cannot act.

## Alternatives considered

- **Rely on the system prompt.** Prompts are guidance and can be overridden by injected content.
- **Confirm once per session.** Approving one action would authorize others the user never saw.
- **Confirm only "risky" actions.** It requires a risk classification, which becomes one more thing to get wrong.

## Consequences

- An injected instruction can at worst propose an action that the user sees and can decline.
- Each action costs one user interaction, which is acceptable with at most one proposed action per answer.
- Waiting for the user must not consume the agent's time budget. The current defaults conflict (45 s for the agent,
  60 s for the gate) and must be resolved in Phase 2.
- Evals use a recording gate to verify both acceptance and refusal paths.
