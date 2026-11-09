# Progress

## Phase 0 — Foundations

- [x] Multi-module skeleton, version catalog, `build-logic`
- [x] ktlint, detekt, GitHub Actions workflow
- [x] `:core:domain`: snapshots, readers, `ConfirmationGate`, `AgentEvent`, `DiagnosticAgent`
- [x] `ConfirmationGate` and JSON format tests
- [x] Koin modules assembled in `:app`, Compose home screen
- [ ] CI run on GitHub: the workflow exists but has never run (no remote yet)

## Phase 1 — Read the phone, no AI

- [ ] Android readers in `:core:device`
- [ ] `BatterySamplerWorker`, Room `battery_samples`, drain per hour
- [ ] Usage access onboarding
- [ ] `<queries>` package visibility
- [ ] Debug "Snapshots" screen

## Phase 2 — First agent with Koog

- [ ] Koog dependency, Google executor in debug only
- [ ] `DeviceTools`, versioned system prompt
- [ ] `KoogDiagnosticAgent`, events mapped to `Flow<AgentEvent>`
- [ ] `:feature:chat`: chat, live trace, confirmation sheet

## Phase 3 — Keyless app with Firebase AI Logic

- [ ] Firebase project, App Check
- [ ] Spike, then `FirebaseAiLLMClient` in `:agent:firebase`
- [ ] `LlmExecutorProvider`

## Phase 4 — Evals

- [ ] YAML scenarios, fake readers, runner, markdown report
- [ ] No-fabricated-numbers check
- [ ] 15 to 20 scenarios, measured pass rate

## Phase 5 — Showcase

- [ ] Optional Anthropic provider (BYOK), model comparison
- [ ] README, ADRs 001 to 006, demo

## Open points

| Phase | Point |
|---|---|
| 1 | Room needs KSP, which is not in the dependency list yet |
| 2 | The agent timeout (45 s) is shorter than the confirmation timeout (60 s): waiting for the user must not consume the agent's time budget |
| 2 | `koog-agents` bundles every provider: prefer targeted modules, and check the Android variant in a spike |
| 2–3 | The API-key executor must stay out of release builds (rule 7), so it cannot be a plain dependency of `:agent` |
| Later | Bump AGP and Gradle once Kotlin officially supports AGP above 9.3; move to detekt 2.0 stable when released |

## Toolchain decisions

AGP 9.3.3 and Gradle 9.6.1 are deliberately not the latest: Kotlin 2.4.21 is validated up to AGP 9.3 and Gradle 9.7.
detekt 2.0.0-alpha.6 is the only pre-release, because 1.23.8 stops at Kotlin 2.0 and AGP 8.8.
