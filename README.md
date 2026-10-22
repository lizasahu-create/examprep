# ExamPrep — Kotlin Android Studio Demo

A native Android starter project for an exam-preparation app with a warm paper-like theme.

## Included in this demo
- Home dashboard
- Practice Set: timed MCQ flow with sample questions, answer selection and score summary
- Sectional Questions: subject list and sample book-like question sheet
- PYQ section: previous-year paper list and reader-style preview
- Admin dashboard demo screen
- Local sample questions so the demo can be explored without Firebase
- Firebase Gradle dependencies and setup notes

## Requirements
- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35
- Internet is required for the first Gradle sync/build (the app itself can use local demo content offline).

## Open and run
1. Extract the ZIP.
2. In Android Studio choose **Open** and select this `ExamPrep` folder.
3. Let Gradle sync, then run the `app` configuration on an emulator or Android device.

## Firebase setup (required for live admin/student sync)
This starter does not include credentials for your Firebase project.
1. Create a project at https://console.firebase.google.com/
2. Add an Android app with package name `com.example.examprep`.
3. Download `google-services.json` and place it in `app/`.
4. In Firebase Authentication, enable Email/Password.
5. Create a Realtime Database or Firestore and configure rules before production use.
6. Uncomment `id("com.google.gms.google-services")` in `app/build.gradle.kts`.
7. Add Firebase Auth and Database/Firestore dependencies in `app/build.gradle.kts`, then implement repository calls in `FirebaseRepository.kt`.

**Important:** The Admin screen here is a UI/demo scaffold, not a secure live admin panel. For production, use a separate web admin panel (e.g. Firebase Hosting + web framework), Firebase Authentication, custom claims or server-verified roles, and restrictive database/storage rules. Never trust a role stored only on the client.

## PDF workflow
For sectional/PYQ content, this demo shows reader-style text pages. To display actual supplied PDFs, add the PDF files under `app/src/main/assets/pdfs/` and integrate Android `PdfRenderer` or a maintained PDF viewer. For large PDF collections, Firebase Storage can host PDFs and Firestore/Realtime Database can store metadata.

## Demo content
- Practice set: 5 sample general-knowledge MCQs
- Sectional subjects: Mathematics, Odia, English, Reasoning, History, Geography, Economics, Polity, Science, Computer, Odisha GK
- PYQ examples: illustrative titles only; replace them with verified source PDFs.

## Design
- Paper/ivory background, navy primary, orange accent, green success
- Rounded paper cards, readable exam typography, simple navigation
- Designed as a clean starter, not a finished production release
