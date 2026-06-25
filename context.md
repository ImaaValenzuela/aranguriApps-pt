# 📝 Project Context: MitiMiti

This document maintains the technical context, state, and libraries used in **MitiMiti**. It should be updated whenever major architectural shifts or dependency updates occur.

---

## 🛠️ Technology Stack & Tech Details

- **Framework:** Kotlin Multiplatform (KMP) with Compose Multiplatform.
- **Targets:** Android (minSdk: 24, targetSdk: 34), iOS (Cocoapods / Xcode integration).
- **UI System:** Jetpack Compose Multiplatform (shared UI) with Material Design 3 guidelines.
- **Database & Real-time:** Intended Firebase Realtime Database integration for Mesa synchronization.
- **Dependency Injection:** Planned Koin or manual ports wiring.
- **Quality & CI/CD tools:**
    - **ktlint:** Code formatting and style rules.
    - **Kover (0.9.8):** Code coverage aggregation (temporarily set to `minValue = 0` to pass builds without dummy coverage blockers).
    - **Android Lint:** Lint rules checker.
    - **GitHub Actions:** Continuous Integration validating PR titles, syntax style, unit tests, and build status.

---

## 📂 Directories & Structural Map

The project is structured under the `shared` module for common code sharing:

- `shared/src/commonMain/kotlin/com/mitimiti/app/`
    - `domain/`: Contains models, interface ports, and use cases.
    - `data/`: Handles network sources (Firebase/Supabase), local storage, and repository implementations.
    - `presentation/`: Contains screens, navigation route configurations, viewmodels, and M3 themes.
- `androidApp/`: Android-specific application container launching the shared `App` composable inside `MainActivity`.
- `iosApp/`: iOS-specific Swift application launching the shared UI via a UIViewController interop wrapper.

---
