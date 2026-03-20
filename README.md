# Cloud-based Ticket Reservation Application
**SOEN 345: Software Testing, Verification and Quality Assurance** **Concordia University | Winter 2026**

## Project Overview
This project is a professional ticket booking solution for events such as movies, concerts, travel, and sports. The application allows customers to browse and reserve tickets in a cloud-based environment while providing administrators with tools to manage the event catalog.

## Team Members
| Name | Student ID |
| :--- | :--- |
| Minh Huy Tran | 40263743 |
| Bhaskar Das | 40325270 |
| Bhumika Bhumika | 40223877 |

## Core Features
* **User Authentication**: Secure registration and login using email or phone numbers via Firebase Auth.
* **Event Discovery**: Real-time browsing of available events with multi-criteria filtering (Date, Location, Category).
* **Reservation System**: High-concurrency booking engine using Firestore transactions to prevent overbooking.
* **Admin Suite**: Full CRUD capabilities for event organizers to add, edit, or cancel events.
* **Digital Notifications**: Automated confirmations sent via Email or SMS.

## Technical Stack
* **Language**: Java
* **IDE**: Android Studio
* **Backend/Database**: Firebase Firestore (NoSQL)
* **Testing**: JUnit 5/6 & AndroidX Test
* **CI/CD**: GitHub Actions

## Continuous Integration
To ensure high software quality and maintain a stable `main` branch, this project utilizes GitHub Actions. Every Pull Request triggers an automated workflow that:
1. Builds the Android project.
2. Executes all Unit and Instrumented tests.
3. Reports status checks to prevent merging failing code.

## Documentation
The complete project report, including requirements analysis, system design, test plans, and results, is hosted in the project Wiki.

**[Project Index / Home](https://github.com/ValvatorezBraveheart/SOEN345/wiki)**


## Installation & Setup
1. Clone the repository: `git clone https://github.com/YourRepo/SOEN345.git`
2. Open the project in **Android Studio**.
3. Ensure the `google-services.json` file is present in the `app/` directory.
4. Sync Gradle and run the application on an emulator or physical device.
