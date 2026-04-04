# Triggerly 🔔⚡

Triggerly is a premium, high-performance Android application designed to revolutionize how you manage reminders. Built with modern architectural patterns and a focus on reliability, Triggerly ensures you never miss a beat by combining power, flexibility, and a stunning user interface.

## 🚀 Key Features

- **Intuitive Reminder Management**: Easily create, edit, and delete reminders with a single tap.
- **Randomized Alerts**: Generate random trigger timestamps for periodic check-ins or habits.
- **Background Reliability**: Integrated with `AlarmManager` and `BootReceiver` to ensure reminders persist through device restarts.
- **Offline First**: All data is stored locally using Room Database, providing instant access even without connectivity.

## 🏗️ Architecture

Triggerly follows a robust **MVVM (Model-View-ViewModel)** architecture combined with **Domain-Driven Design (DDD)** principles to ensure code maintainability and scalability.

- **Presentation Layer**: Uses ViewModels and LiveData to provide a reactive UI that responds to data changes.
- **Domain Layer**: Contains the core business logic, including Use Cases that orchestrate operations like saving reminders or rescheduling alarms.
- **Data Layer (Infrastructure)**: Handles persistent storage via Room and low-level system services like `AlarmScheduler`.

## 🛠️ Tech Stack

- **Language**: Java 17
- **Database**: Room Persistence Library
- **Lifecycle Management**: ViewModel & LiveData
- **UI Components**: Material Components for Android
- **Scheduling**: AlarmManager
- **Build System**: Gradle (KTS/Groovy)

## 📥 Installation

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- Android SDK 34+

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/CantarinoG/Triggerly.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Build and run the `app` module on an emulator or physical device.

## 📖 Usage

1. **Create a Reminder**: Tap the "Add" button and fill in the reminder details.
2. **Set Triggers**: Choose time window and frequency.
3. **Manage**: Long-press on a reminder to delete it.
4. **Notifications**: Stay informed with timely alerts that pop up even when the app is closed.

---

*Made with ❤️ for a better productivity experience.*
