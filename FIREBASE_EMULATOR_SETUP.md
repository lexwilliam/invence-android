# Firebase Emulator Setup

This project is configured to use Firebase Local Emulator for development builds.

## Prerequisites

1. Install Firebase CLI:

    ```bash
    npm install -g firebase-tools
    ```

2. Login to Firebase:
    ```bash
    firebase login
    ```

## Running the Emulator

1. Start the Firebase emulator:

    ```bash
    firebase emulators:start
    ```

    This will start the following emulators:

    - Auth: http://localhost:9099
    - Firestore: http://localhost:8080
    - Functions: http://localhost:5001
    - Storage: http://localhost:9199
    - UI: http://localhost:4000

2. Build and run the debug version of the app:
    ```bash
    ./gradlew assembleDebug
    ```

## Configuration

The emulator configuration is defined in `firebase.json`. The app automatically connects to the emulator when running in debug mode.

### Debug Build Configuration

-   Debug builds use the Firebase emulator
-   Release builds use the production Firebase services
-   The emulator connection is controlled by the `USE_FIREBASE_EMULATOR` build config field

### Emulator Host

The app is configured to connect to `10.0.2.2` which is the Android emulator's host IP for localhost connections.

## Testing

1. Open the Firebase Emulator UI at http://localhost:4000
2. Use the UI to:
    - Create test users in Auth
    - Add test data to Firestore
    - Test Cloud Functions
    - Upload test files to Storage

## Troubleshooting

-   Make sure the Firebase emulator is running before starting the app
-   If you see connection errors, verify the emulator is running on the correct ports
-   Check the Android emulator's network settings if using a physical device

