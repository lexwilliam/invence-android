#!/bin/bash

# Start Firebase Emulator for Development
echo "Starting Firebase Emulator..."

# Check if firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "Firebase CLI is not installed. Please install it first:"
    echo "npm install -g firebase-tools"
    exit 1
fi

# Check if user is logged in
if ! firebase projects:list &> /dev/null; then
    echo "Please login to Firebase first:"
    echo "firebase login"
    exit 1
fi

# Start the emulator
firebase emulators:start

echo "Firebase Emulator started successfully!"
echo "Emulator UI: http://localhost:4000"
echo "Auth: http://localhost:9099"
echo "Firestore: http://localhost:8080"
echo "Functions: http://localhost:5001"
echo "Storage: http://localhost:9199"
