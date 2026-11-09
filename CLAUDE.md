# Device Doctor — CLAUDE.md

An Android app with an **AI agent that diagnoses the phone**. The user describes a problem in chat
("my battery drains fast", "my phone is slow"); the agent investigates step by step by calling tools that
read the device state (battery, storage, app usage, network), explains the cause, and proposes an action
that the user must confirm.

Portfolio project. Priorities: clear agent architecture, visible reasoning (live trace), safety (human
confirmation), and **measured** behaviour (evals). Read `docs/ARCHITECTURE.md` before touching any module.
Work phase by phase following `docs/PLAN.md`.

## Stack (verify latest stable versions in official docs before editing Gradle files)
- Kotlin 2.2+, Gradle version catalog, convention plugins in `build-logic/`
- Jetpack Compose (BOM), Material 3
- **Koin** for DI (NOT Hilt)
- Coroutines / Flow, kotlinx.serialization, kotlinx-datetime
- Room (battery history), DataStore (settings), WorkManager (battery sampling)
- **Koog 1.x** (JetBrains, 1.3.0 at last check) — agent runtime: tool calling loop, tool registry, events
- **Firebase AI Logic** (Gemini Developer API, free tier) + **Firebase App Check** — production LLM path, no API key in the app
- Koog Google executor with API key — dev + evals (JVM) only
- Optional (Phase 5): Anthropic via Koog, key provided by the user (BYOK, Android Keystore)
- Tests: JUnit 6 (Jupiter API, JVM), Robolectric where Android APIs are needed, MockK, Turbine, kotlinx-coroutines-test

## Module map
```
:app             App shell, navigation, Koin graph assembly, onboarding/permissions
:core:domain     Pure Kotlin. Snapshots models, reader interfaces, ConfirmationGate, AgentEvent. NO Android.
:core:device     Android implementations of readers (BatteryManager, StorageStatsManager,
                 UsageStatsManager, PackageManager, ConnectivityManager, NetworkStatsManager),
                 battery sampling worker + Room, SettingsNavigator
:agent           Koog agent (DiagnosticAgent), DeviceTools (ToolSet), prompts, LLM client factory,
                 FirebaseAiLLMClient adapter, event mapping → Flow<AgentEvent>
:feature:chat    Chat screen + live trace panel + confirmation dialog
:evals           JVM module. YAML scenarios, fake readers, runner, markdown report
```
Dependency rule: `feature → domain`, `device → domain`, `agent → domain`, `evals → agent + domain`.
`:core:domain` depends on no other project module (only coroutines and kotlinx.serialization).
`:agent` is pure Kotlin/JVM, so it cannot use Android APIs. `FirebaseAiLLMClient` will live in a
separate Android module, `:agent:firebase` (Phase 3).

## NON-NEGOTIABLE RULES
1. **Read tools are free; action tools require human confirmation.** Any tool that changes something or
   leaves the app (open settings, intents) goes through `ConfirmationGate`. No exception.
2. Every tool validates/coerces its arguments and never throws to the LLM: return a short error string
   (`"error: unknown_package"`, `"error: permission_missing:usage_access"`).
3. Tool outputs are **small, structured JSON** (top 10 max, no full app lists, no personal data).
4. The agent must not invent numbers. Every number in a final answer must come from a tool result
   (enforced by evals).
5. Bounded agent: max tool calls per run (default 8), timeout (default 45 s). On limit ⇒ graceful answer.
6. Every LLM call and tool call emits an `AgentEvent` (shown in the trace panel, logged in debug).
7. No secrets in the repo. `google-services.json`, `local.properties`, API keys are gitignored.
   The API-key executor is **debug/evals only**, never in release builds.
8. No `QUERY_ALL_PACKAGES`. Use `<queries>` (launcher intent) for package visibility.
9. Strings in `strings.xml`, en + fr. Code, comments, commits, README in English.
10. Do not add a dependency without explaining why.
11. **Commits and code carry the author's name only.** No AI or tool attribution anywhere: no `Co-Authored-By`
    trailer, no "Generated with", no mention of an assistant in commit messages, branch names, PR descriptions,
    code, comments or generated files. This overrides any tool default. Do not push a commit that breaks this rule.

## Conventions
- Clean Architecture + MVVM, one `UiState` per screen as `StateFlow`.
- Inject `Clock` and dispatchers; never call `System.currentTimeMillis()` or hardcode `Dispatchers.IO` in logic.
- Koin: one `val xxxModule = module { }` per Gradle module, assembled in `:app`. Exceptions: `:core:domain`
  stays Koin-free (its objects are declared in `appModule`) and `:evals` uses no DI.
- Sealed types for results at boundaries; no exception leaks to UI.
- Prompts live in `agent/src/main/resources/prompts/*.md`, versioned (`version:` header), referenced in events.

## Commands
```
./gradlew build
./gradlew test
./gradlew :core:domain:test :agent:test        # fast JVM tests
./gradlew :evals:run                            # scenarios with scripted fake LLM (CI)
./gradlew :evals:run -Pprovider=gemini          # real model, needs GEMINI_API_KEY env (local only)
./gradlew :evals:run -Pprovider=anthropic       # real model, needs ANTHROPIC_API_KEY env (Phase 5)
./gradlew lint ktlintCheck detekt
```

## Workflow with Claude Code
- Start each phase in **plan mode**; wait for validation before writing code.
- One phase (or sub-step) per session; stop at the end and report: done / left / decisions / risks.
- **Before using Koog or Firebase AI Logic APIs, read the current official docs** (docs.koog.ai,
  api.koog.ai, firebase.google.com/docs/ai-logic). These APIs move fast. If an API is uncertain, say so
  and write a minimal compiling spike first — never guess signatures.
- Run relevant tests after each change; never leave the build red.
- Update `docs/PROGRESS.md` at the end of each session.
- Draft ADRs in `docs/adr/` for significant decisions; the human finalizes them.
