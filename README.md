# 🏋️ FitPulse — Gym & Fitness Management Android App

**FitPulse** is a modern, full-featured Android application built with **Jetpack Compose**, **Kotlin**, **Clean Architecture**, and **Material 3**. It offers gym members and administrators an all-in-one platform for workout tracking, digital QR membership passes, attendance scanning, class bookings, schedule management, and administrative control.

---

## ✨ Features

### 👤 Member Features
- **Dashboard & Home Overview**: Live stats on active streaks, workouts completed, upcoming sessions, and gym announcements.
- **Digital QR Membership Pass**: Instant dynamic QR code generation (using ZXing) for seamless touchless gym check-ins.
- **Workout Tracking & Routines**: Catalog of workout routines, sets, reps, weight tracking, and progress visualization.
- **Class Booking & Schedules**: Browse upcoming fitness sessions (HIIT, Yoga, Strength, Spin) and book spots in real time.
- **Membership Plans**: View active membership tier, renewal status, and explore available upgrade plans.
- **Biometric & Security**: Secure member authentication with biometric sign-in support.

### 🛡️ Admin & Trainer Features
- **QR Attendance Scanner**: High-speed QR scanner powered by **CameraX** and **Google ML Kit Barcode Scanning** for live gym floor check-ins.
- **Member Management**: View, filter, and manage registered gym members and their subscription statuses.
- **Attendance History & Logs**: Real-time auditing of check-ins and gym traffic.
- **Class & Schedule Management**: Create, update, and manage trainers, classes, and booking capacities.

---

## 🛠️ Architecture & Tech Stack

This project strictly adheres to **Clean Architecture** principles and Android modern development guidelines:

```
app/
├── data/              # Data Layer (Room DB, DAOs, Entities, Remote Data Source, Seed Data)
├── domain/            # Domain Layer (Business models, Repositories interfaces, Use Cases)
├── theme/             # Material 3 Theme, Typography, Color Palette
└── ui/                # Presentation Layer (Jetpack Compose, ViewModels, Navigation, Screens)
    ├── components/    # Reusable Compose UI components
    ├── navigation/    # Navigation graphs and route destinations
    ├── screens/       # Compose UI screens (Home, Pass, Workouts, Bookings, Admin, etc.)
    └── viewmodel/     # StateFlow & SharedFlow powered ViewModels
```

### 🧰 Libraries & Tools
- **UI & Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture**: Clean Architecture (MVI / MVVM pattern)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) + KSP
- **Database / Local Cache**: [Room Database](https://developer.android.com/training/data-storage/room) with Coroutines & Kotlin Flows
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & Kotlinx Serialization
- **Barcode & Camera**: [CameraX](https://developer.android.com/training/camerax) + [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- **QR Generation**: [ZXing](https://github.com/zxing/zxing)
- **Language**: Kotlin 2.x (Toolchain JDK 17)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- JDK 17
- Android SDK 36 (minSdk 24)

### Clone & Build
```bash
git clone <repository-url>
cd FitPulse
```

Open the project in Android Studio, allow Gradle sync to complete, and run the app on an Android device or emulator (API 24+).

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
