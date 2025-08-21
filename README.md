# Send Money Application 💸

This is a Kotlin-based Android application developed using **MVVM
architecture** and **Jetpack Compose** for the UI.\
The app simulates a money transfer system with login functionality,
transfer history, and a send money workflow.

------------------------------------------------------------------------

## 📂 Project Structure

The project follows a **clean architecture pattern** divided into
layers: `data`, `repository`, and `ui`.
```
com.assignment.sendmoneyapplication
│
├── data
│   ├── dao
│   │   └── TransferDao.kt         # DAO for handling DB operations
│   ├── db
│   │   ├── AppDatabase.kt         # Room database setup
│   │   └── PreferenceManager.kt   # SharedPreferences wrapper
│   └── entity
│       └── TransferEntity.kt      # Entity for money transfers
│
├── repository
│   └── TransferRepository.kt      # Repository for business logic
│
├── ui
│   ├── navigation
│   │   └── NavGraph.kt            # Jetpack Compose navigation graph
│   ├── screens
│   │   ├── history
│   │   │   └── TransferHistoryScreen.kt  # Transaction history screen
│   │   ├── login
│   │   │   ├── LoginScreen.kt     # Login UI
│   │   │   └── LoginViewModel.kt  # Login business logic
│   │   └── sendMoney
│   │       ├── FormSchema.kt      # Form validation schema
│   │       ├── SendMoney.kt       # Send money screen
│   │       └── SendMoneyViewModel.kt # Send money logic
│   ├── theme                      # Compose theme setup
│   └── MainActivity.kt             # Entry point of the application
│
├── assets
│   └── send_money.json            # JSON data for mock/test usage
```
------------------------------------------------------------------------

## 📁 Detailed File Overview

### 1️⃣ Data Layer (`data/`)

-   **dao/TransferDao.kt** → Defines database access methods for
    performing CRUD operations on transfers. Uses Room DAO.\
-   **db/AppDatabase.kt** → Singleton Room database that provides DAO
    instances.\
-   **db/PreferenceManager.kt** → Manages user preferences (e.g., login
    state).\
-   **entity/TransferEntity.kt** → Defines database schema for a
    transfer object (id, amount, gender, name, timestamp).

### 2️⃣ Repository Layer (`repository/`)

-   **TransferRepository.kt** → Acts as a bridge between the `data`
    layer and the `ui`. It fetches and stores data from Room DB and
    preferences.

### 3️⃣ UI Layer (`ui/`)

#### Navigation

-   **NavGraph.kt** → Defines navigation routes between screens (Login,
    SendMoney, History).

#### Screens

-   **login/LoginScreen.kt** → UI for login functionality. Uses Jetpack
    Compose UI components.\
-   **login/LoginViewModel.kt** → Handles login logic, communicates with
    `PreferenceManager`.\
-   **sendMoney/FormSchema.kt** → Defines form schema (fields,
    validation) for sending money.\
-   **sendMoney/SendMoney.kt** → Composable UI for sending money,
    validates form and interacts with `SendMoneyViewModel`.\
-   **sendMoney/SendMoneyViewModel.kt** → Business logic for sending
    money (validations, storing transfer record).\
-   **history/TransferHistoryScreen.kt** → Displays a list of past
    transactions from Room DB.

#### Theme

-   **theme/** → Contains app-wide styles and themes (colors,
    typography, shapes).

#### Root

-   **MainActivity.kt** → Entry point of the app. Initializes Compose UI
    and sets up the navigation graph.

### 4️⃣ Assets (`assets/`)

-   **send_money.json** → Sample mock data for transfers. Used to
    prepopulate or simulate server responses.

------------------------------------------------------------------------

## 🛠️ Technologies & Libraries Used

-   **Kotlin** -- Main programming language.\
-   **Jetpack Compose** -- Modern UI toolkit for building declarative
    UIs.\
-   **MVVM (Model-View-ViewModel)** -- Architecture pattern.\
-   **Room Database** -- Local persistence.\
-   **SharedPreferences / PreferenceManager** -- Local key-value
    storage.\
-   **Navigation Component** -- Navigation between screens.\
-   **Coroutines & Flow** -- Asynchronous programming and reactive data
    streams.

------------------------------------------------------------------------

## 🚀 How to Run the Project

### ✅ Prerequisites

-   Install **Android Studio (Arctic Fox or later)**\
-   JDK **11 or above**\
-   Gradle (comes bundled with Android Studio)\
-   Android device/emulator running **API 24+**

### ▶️ Steps

1.  Clone the repository:

    ``` bash
    git clone https://github.com/Ahmadhussain0335/sendmoneyapplication.git
    ```

2.  Open the project in **Android Studio**.

3.  Sync Gradle dependencies (`File > Sync Project with Gradle Files`).

4.  Run the project on an emulator or physical device:

    ``` bash
    ./gradlew assembleDebug
    ```

5.  The app will launch with the **Login screen**.

------------------------------------------------------------------------

## 📌 Features

-   User authentication (mocked login).\
-   Send money with input validation.\
-   View transaction history stored in Room DB.\
-   Persistent login state using SharedPreferences.\
-   Clean architecture with separation of concerns.

------------------------------------------------------------------------

## 📄 Future Improvements

-   Integrate with real backend API.\
-   Add unit & UI testing with JUnit, Espresso.\
-   Implement biometric authentication.\
-   Enhance UI with animations & Material 3 components.

------------------------------------------------------------------------

## 👨‍💻 Author

Developed by **Ahmad Hussain**\
(Senior Software Engineer, Mobile Development Specialist)
