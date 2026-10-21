# NumberGap 🧮

**NumberGap** is a modern, high-precision Android application designed for calculating numerical gaps, differences, averages, sums, and percentage comparisons between numbers with step-by-step breakdown and visual comparisons.

Built using **100% Jetpack Compose**, **Material 3**, and modern Android development best practices.

---

## ✨ Features

- **🧮 High-Precision Math Engine**:
  - Uses `BigDecimal` with up to 50 decimal precision (`MathContext`).
  - Calculates **Absolute Gap**, **Signed Difference**, **Sum**, **Average**, and **Percentage Difference**.
  - Displays larger vs. smaller number comparisons.
  - Step-by-step calculation breakdown for full transparency.

- **📊 Visual Comparison**:
  - Interactive proportional bar chart visualizing relative values and difference gaps.

- **📜 Calculation History**:
  - Automatically saves previous calculations.
  - Quick action to copy, share, bookmark, or delete entries.
  - Search and filter history entries.

- **⚙️ Deep Customization & Settings**:
  - Customizable decimal precision (0 to 8 decimal places).
  - Multiple rounding modes (`HALF_UP`, `HALF_EVEN`, `UP`, `DOWN`, `CEILING`, `FLOOR`).
  - Dynamic color support (Android 12+) and dark/light/system theme options.
  - Haptic feedback and smooth Compose animations toggle.

- **🎨 Modern UI/UX**:
  - Modern Material Design 3 interface with intuitive navigation.
  - Seamless input handling with quick swap, clear, and paste actions.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://developer.android.com/jetpack/compose/designsystems/material3)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Domain Layer
- **State Management**: Kotlin `StateFlow` and `Coroutines`
- **Unit Testing**: JUnit 4 & `kotlinx-coroutines-test`

---

## 📂 Project Structure

```
com.example.numbergap
├── data/
│   ├── model/          # Data classes (AppSettings, CalculationStep, NumberGapResult, etc.)
│   └── repository/     # HistoryRepository & SettingsRepository
├── domain/
│   └── math/           # Core Math Engine (Calculator, Formatter, Parser, Validator)
├── ui/
│   ├── about/          # About screen & app info
│   ├── history/        # History screen & list components
│   ├── home/           # Main calculator screen & visualizer
│   ├── navigation/     # NavHost & Screen definitions
│   ├── settings/       # App preferences & settings screen
│   ├── splash/         # Splash screen
│   ├── theme/          # Material 3 colors, typography, and theme definitions
│   └── viewmodel/      # NumberGapViewModel & UiState
└── util/               # Clipboard, Haptic, and Share utilities
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK**: 11 or higher
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 37

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/NumberGap.git
   cd NumberGap
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 📄 License

```text
Copyright 2025 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations carefully.
```
