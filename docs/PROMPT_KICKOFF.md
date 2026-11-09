# Prompt de démarrage — 1re session Claude Code (Device Doctor)

> Pré-requis : dépôt vide avec `CLAUDE.md`, `docs/ARCHITECTURE.md`, `docs/PLAN.md` déjà copiés.
> Lance Claude Code à la racine, passe en **plan mode** (Shift+Tab), puis colle le texte ci-dessous.

---

Tu vas m'aider à construire **Device Doctor v0** : une app Android avec un agent IA qui diagnostique le téléphone (batterie, stockage, lenteur, réseau) en appelant des outils qui lisent l'état du téléphone, puis propose une action que l'utilisateur doit confirmer.

C'est un projet portfolio pour montrer une compétence en **agentic AI sur Android**. Ce qui compte : architecture claire, raisonnement visible (trace), sécurité (confirmation humaine), comportement mesuré (évals).

## Contexte
- Je suis Senior Android Engineer (10+ ans, Kotlin, Compose, Clean Architecture, Koin, Android Enterprise). Ne m'explique pas les bases Android. **Explique-moi en revanche tout ce qui concerne Koog, le function calling et l'agentique** : je veux apprendre en construisant.
- Lis intégralement, dans l'ordre : `CLAUDE.md`, `docs/ARCHITECTURE.md`, `docs/PLAN.md`. Ce sont la source de vérité. Signale toute incohérence ou tout risque avant de coder.

## Mission de cette session : Phase 0 uniquement

1. **Versions** : avant d'écrire un fichier Gradle, consulte les sources officielles et donne-moi un tableau « lib → dernière version stable → source » pour : Kotlin, AGP, Gradle wrapper, Compose BOM, Koin, Room, WorkManager, DataStore, kotlinx-coroutines, kotlinx-serialization, kotlinx-datetime, JUnit5, MockK, Turbine, Robolectric, ktlint, detekt. Note aussi (sans les ajouter) les versions actuelles de Koog et du Firebase BoM, ainsi que la version minimale de Kotlin/JDK requise par Koog 1.0, pour vérifier la compatibilité dès maintenant.

2. **Squelette** :
   - Modules : `:app`, `:core:domain`, `:core:device`, `:agent`, `:feature:chat`, `:evals`
   - `:core:domain` et `:evals` en Kotlin/JVM purs ; `:core:device` et `:feature:chat` en Android library. Pour `:agent`, propose et justifie : Android library ou JVM + source set Android (contrainte : la majorité doit rester testable en JVM, l'adaptateur Firebase est Android)
   - `build-logic/` avec convention plugins : `devicedoctor.android.library`, `devicedoctor.android.compose`, `devicedoctor.jvm.library`, `devicedoctor.koin`
   - `gradle/libs.versions.toml`, package racine `com.mbr.devicedoctor`
   - minSdk 26, compile/target SDK : dernier stable
   - `.gitignore` : `google-services.json`, `local.properties`, `build/`, `*.keystore`

3. **Qualité / CI** : ktlint + detekt, GitHub Actions `ci.yml` (build, test, lint) sur push et PR.

4. **Domaine** (`:core:domain`) : implémente exactement `ARCHITECTURE.md` §4 :
   - Snapshots `@Serializable` (`BatterySnapshot`, `StorageSnapshot`, `AppUsage`, `AppDetails`, `AppSize`, `AppDataUsage`, `NetworkSnapshot`)
   - `ReaderResult` + interfaces de readers + `SettingsNavigator`
   - `ConfirmationGate` (avec `ConfirmationRequest`)
   - `AgentEvent`, `ChatTurn`, interface `DiagnosticAgent`
   - `Clock` et dispatchers injectés

5. **Tests** :
   - `ConfirmationGateTest` (Turbine + coroutines-test) : `ask` suspend jusqu'à `respond(true/false)`, timeout ⇒ `false`, annulation de l'appelant ⇒ `pending` remis à `null`, deux demandes successives
   - Tests de sérialisation JSON des snapshots (stabilité du format envoyé au LLM)

6. **Câblage minimal** : un module Koin par module Gradle (vide si besoin), assemblés dans `:app`. `MainActivity` Compose affiche « Device Doctor v0 ».

7. **Docs** : `docs/PROGRESS.md` (checklist de toutes les phases, Phase 0 cochée à la fin), `docs/adr/0000-template.md`, et un brouillon d'ADR-003 (« confirmation humaine pour toute action ») que je réécrirai.

## Façon de travailler
- **Commence par un plan détaillé** : arborescence, classes, tests, ordre d'implémentation. Attends mon « go ».
- Petites étapes ; `./gradlew build test` après chacune ; corrige avant de continuer.
- Si une API ou une version est incertaine, dis-le plutôt que de deviner.
- Aucune dépendance non listée sans me demander.
- Commits atomiques en anglais (Conventional Commits).
- **Ne fais rien des phases 1 à 5** dans cette session. Pas encore de Koog ni de Firebase en dépendance.

## Fin de session — rapport attendu
1. Ce qui a été fait (arborescence finale)
2. Résultat de `./gradlew build test lint`
3. Tableau des versions + compatibilité Koog vérifiée
4. Décisions prises et pourquoi (notamment le type de module `:agent`)
5. Ce que je dois relire en priorité
6. Ce qui est prêt pour la Phase 1
