#!/bin/bash
set -euo pipefail

# Clean Install Script for Box Application
# This ensures changes are properly reflected on the device

echo "🧹 Starting clean installation process..."

# Get the active device
DEVICE=$(~/Library/Android/sdk/platform-tools/adb devices | grep -w "device" | head -1 | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo "❌ No Android device/emulator found. Please start an emulator first."
    exit 1
fi

echo "📱 Found device: $DEVICE"

# Step 1 (optional): Uninstall the app.
# NOTE: Uninstalling removes SharedPreferences, so login session is lost.
if [ "${FULL_RESET:-0}" = "1" ]; then
    echo "🗑️  FULL_RESET=1 -> Uninstalling old version (this clears login/session data)..."
    ~/Library/Android/sdk/platform-tools/adb -s "$DEVICE" uninstall com.aeu.boxapplication 2>/dev/null || true
else
    echo "ℹ️  Preserving app data/session (skip uninstall). Set FULL_RESET=1 to force uninstall."
fi

# Step 2: Clean build
echo "🧼 Cleaning build cache..."
./gradlew clean

# Step 3: Build and install
echo "🔨 Building and installing app..."
./gradlew installDebug

# Step 4: Launch the app
echo "🚀 Launching app..."
~/Library/Android/sdk/platform-tools/adb -s "$DEVICE" shell am start -n com.aeu.boxapplication/.MainActivity

echo "✅ Done! Your changes should now be visible on the device."
