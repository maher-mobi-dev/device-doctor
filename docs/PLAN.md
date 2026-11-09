# Plan — Device Doctor v0 (~3 semaines)

Règle : une phase = une ou plusieurs sessions Claude Code, **toujours en plan mode d'abord**.
Fin de phase : build + tests verts, `docs/PROGRESS.md` à jour, commits propres, tu relis le code clé.

---

## Phase 0 — Fondations (1–2 jours)
- Multi-modules (`:app`, `:core:domain`, `:core:device`, `:agent`, `:feature:chat`, `:evals`), version catalog, `build-logic/`
- ktlint, detekt, GitHub Actions (build, test, lint)
- `:core:domain` complet : snapshots, interfaces de readers, `ReaderResult`, `ConfirmationGate`, `AgentEvent`, `DiagnosticAgent`
- Tests : `ConfirmationGate` (attente, acceptation, refus, timeout, annulation) avec Turbine

**Fait quand :** `./gradlew build test` vert en local et en CI.
→ Prompt : `PROMPT_KICKOFF.md`

## Phase 1 — Lire le téléphone, sans IA (3–4 jours)
- Readers Android dans `:core:device` (tableau §5 de l'architecture)
- `BatterySamplerWorker` + Room `battery_samples` + calcul de la perte par heure
- Onboarding des permissions : accès aux données d'utilisation (écran d'explication + bouton vers les réglages + vérification `AppOpsManager`)
- `<queries>` pour la visibilité des packages (pas de `QUERY_ALL_PACKAGES`)
- **Écran debug « Snapshots »** qui affiche le JSON brut de chaque reader

**Fait quand :** sur ton téléphone, l'écran debug montre batterie, stockage, top apps, réseau, data.
**Ton travail :** vérifier les chiffres face aux réglages Android.

> Prompt : « Lis CLAUDE.md, docs/ARCHITECTURE.md (§4, §5) et docs/PROGRESS.md. Plan mode pour la Phase 1 de docs/PLAN.md. Détaille, pour chaque reader, l'API Android, la permission, la version d'API minimale et les cas limites (permission refusée, historique vide). Attends mon go. Aucun code IA dans cette phase. »

## Phase 2 — Premier agent avec Koog (4–5 jours)
- Dépendance Koog 1.x, exécuteur Google **avec clé API en debug uniquement** (`local.properties` → `BuildConfig` du build debug)
- `DeviceTools` (lecture d'abord, puis actions via `ConfirmationGate`)
- Prompt système versionné `prompts/diagnostic.md`
- `KoogDiagnosticAgent` + mapping des événements Koog vers `Flow<AgentEvent>`
- `:feature:chat` : chat, panneau de trace en direct, bottom sheet de confirmation, prompts suggérés, bannière de permission manquante

**Fait quand :** « ma batterie se vide vite » produit une enquête en plusieurs outils visible dans la trace, avec une action confirmée ou refusée.
**Ton travail :** écrire toi-même le prompt système final.

> Prompt : « Phase 2 de docs/PLAN.md. AVANT de coder : consulte docs.koog.ai et api.koog.ai (version 1.x) et montre-moi les API exactes pour : AIAgent, ToolSet/@Tool/@LLMDescription, ToolRegistry, exécuteur Google, limite d'itérations, gestionnaire d'événements. Écris d'abord un test JVM minimal qui fait tourner un agent avec un outil factice. Puis plan mode pour le reste. »

## Phase 3 — App sans clé : adaptateur Firebase AI Logic (3 jours)
- Projet Firebase, `google-services.json` (gitignoré), App Check + debug provider
- **Spike** : interface `LLMClient` de Koog 1.x et types Kotlin de Firebase AI Logic (`FunctionDeclaration`, `Schema`, `FunctionCallPart`, `FunctionResponsePart`)
- `FirebaseAiLLMClient` + fonctions de mapping pures, testées unitairement
- `LlmExecutorProvider` : Firebase par défaut, clé API en debug seulement

**Fait quand :** la build release fonctionne sans aucune clé, et les tests de mapping sont verts.

> Prompt : « Phase 3 de docs/PLAN.md, §6 "FirebaseAiLLMClient" de l'architecture. Étape 1 seulement : spike. Récupère les signatures exactes actuelles de l'interface LLMClient de Koog et des types de function calling de Firebase AI Logic Kotlin, puis propose le design précis du mapping avant d'écrire du code. Si une lib communautaire existe, évalue-la (maintenance, licence, couverture) et donne ton avis. »

## Phase 4 — Évals (3 jours)
- Module `:evals` : format YAML, `FakeReaders`, `RecordingConfirmationGate`, runner, rapport markdown
- Vérification « pas de chiffres inventés » (chaque nombre de la réponse doit apparaître dans un résultat d'outil)
- 15 à 20 scénarios (familles listées au §8)
- CI avec LLM scripté ; en local `-Pprovider=gemini`, 3 runs par scénario

**Fait quand :** `build/evals/report.md` généré, taux de réussite mesuré, échecs analysés et prompt ajusté.
**Ton travail :** écrire toi-même les scénarios et leurs attentes. C'est là que se trouve l'expertise.

## Phase 5 — Vitrine (3 jours)
- Optionnel : fournisseur Anthropic (BYOK + clé chiffrée via Keystore), évals sur les 2 modèles, tableau comparatif
- README en anglais : pitch, GIF de la trace, schéma, décisions, résultats d'évals, limites honnêtes, « How this was built with Claude Code »
- ADR 001→006 finalisés
- Vidéo de démo de 2 min + post LinkedIn

**Fait quand :** un recruteur comprend le projet en 2 minutes en lisant le README.
