# 🤖 Instructions for AI Agents

Welcome! If you are an AI assistant (like Antigravity, Claude Code, or any other agentic tool) collaborating on **MitiMiti**, you MUST adhere to the instructions, guidelines, and constraints defined in this document.

---

## 🚫 Critical Constraints & Commit Policy

*   **No Unapproved Pushes / Commits:** Never perform a `git push` or `git commit` unless the user explicitly instructs you to do so. Work in a feature/fix/docs branch, make changes, and leave them staged/untracked for the user to review.
*   **Branch Naming Convention:** Always use the appropriate branch prefixes matching GitFlow:
    *   `🚀 feature/` for new features.
    *   `🐛 fix/` for bug fixes.
    *   `⚙️ chore/` for configuration and tasks.
    *   `♻️ refactor/` for code restructuring.
    *   `📝 docs/` for documentation.
    *   `🧪 test/` for unit or integration testing.
*   **Conventional Commits:** If authorized to commit, use the Conventional Commits format:
    *   `feat(scope): add new feature`
    *   `fix(scope): fix bug`
    *   `chore(gradle): update dependency`

---

## 🛠️ Local Verification Workflow

Before reporting a task as complete, you MUST verify the code locally using the provided automation scripts.

1.  **Format and Quality Check:**
    Run the following script to verify format (ktlint), run unit tests, and generate lint/coverage reports:
    ```bash
    ./pr-check.sh
    ```
2.  **App Running & Installation:**
    If you've modified UI or presentation code, launch the app on the emulator using:
    ```bash
    ./run-app.sh
    ```

---

## 🧭 Architecture Alignment

MitiMiti is structured using **Hexagonal Architecture (Ports and Adapters)**.
*   **Core Domain (100% Pure Kotlin):** Must reside in `com.mitimiti.app.domain`. It must not depend on any Android library or external databases (like Firebase/Supabase/Ktor).
*   **Data/Infrastructure Layer:** Resides in `com.mitimiti.app.data`. Handles raw network calls (Firebase, Ktor, Supabase) and databases, mapping DTOs to Domain models.
*   **Presentation Layer:** Resides in `com.mitimiti.app.presentation`. Built using Jetpack Compose Multiplatform and Material Design 3.

---

## 🧰 Agent Skills Usage

This project has several agent skills installed in `~/.agents/skills/`. You are expected to read them before writing code that touches those areas:

*   **Kotlin Multiplatform & UI Patterns:** `~/.agents/skills/compose-multiplatform-patterns/SKILL.md`
*   **Clean Architecture Rules:** `~/.agents/skills/android-clean-architecture/SKILL.md`
*   **Coroutines & Reactive Flows:** `~/.agents/skills/kotlin-coroutines-flows/SKILL.md`
*   **Platformexpect/actual interop:** `~/.agents/skills/kotlin-multiplatform-expect-actual/SKILL.md`
*   **Android Native Standards & Material 3:** `~/.agents/skills/android-native-dev/SKILL.md`
*   **SwiftUI (iOS UI) Patterns:** `~/.agents/skills/swiftui-patterns/SKILL.md`

Use the `view_file` tool to inspect these skills as needed to maintain repository quality.
