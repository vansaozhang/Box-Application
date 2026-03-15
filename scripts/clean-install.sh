#!/bin/bash
set -euo pipefail

# Clean Install Script for Box Application
# This ensures changes are properly reflected on the device

echo "🧹 Starting clean installation process..."

ADB="${ADB:-$HOME/Library/Android/sdk/platform-tools/adb}"
APP_ID="app.aeu.box"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Get the active device
DEVICE=$("$ADB" devices | awk '/\tdevice$/{print $1; exit}')

if [ -z "$DEVICE" ]; then
    echo "❌ No Android device/emulator found. Please start an emulator first."
    exit 1
fi

echo "📱 Found device: $DEVICE"

echo "⏳ Waiting for emulator/device to be fully ready..."
"$ADB" -s "$DEVICE" wait-for-device
for _ in $(seq 1 45); do
    BOOT_COMPLETED=$("$ADB" -s "$DEVICE" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
    API_LEVEL=$("$ADB" -s "$DEVICE" shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r')
    if [ "$BOOT_COMPLETED" = "1" ] && [[ "$API_LEVEL" =~ ^[0-9]+$ ]]; then
        break
    fi
    sleep 2
done

BOOT_COMPLETED=$("$ADB" -s "$DEVICE" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
API_LEVEL=$("$ADB" -s "$DEVICE" shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r')
if [ "$BOOT_COMPLETED" != "1" ] || ! [[ "$API_LEVEL" =~ ^[0-9]+$ ]]; then
    echo "❌ Device is connected but not ready (boot/api unresolved)."
    echo "   Try: Emulator Manager -> Cold Boot Now, then rerun this script."
    exit 1
fi

echo "✅ Device ready (API $API_LEVEL)"

# Step 1 (optional): Uninstall the app.
# NOTE: Uninstalling removes SharedPreferences, so login session is lost.
if [ "${FULL_RESET:-0}" = "1" ]; then
    echo "🗑️  FULL_RESET=1 -> Uninstalling old version (this clears login/session data)..."
    "$ADB" -s "$DEVICE" uninstall "$APP_ID" 2>/dev/null || true
else
    echo "ℹ️  Preserving app data/session (skip uninstall). Set FULL_RESET=1 to force uninstall."
fi

# Step 2: Clean build
echo "🧼 Cleaning build cache..."
./gradlew clean

# Step 3: Build and install
echo "🔨 Building and installing app..."
if ./gradlew installDebug; then
    echo "✅ Installed via Gradle task."
else
    echo "⚠️  Gradle install failed; trying ADB fallback install..."
    ./gradlew :app:assembleDebug
    if [ ! -f "$APK_PATH" ]; then
        echo "❌ APK not found at $APK_PATH"
        exit 1
    fi
    "$ADB" -s "$DEVICE" install -r -d "$APK_PATH"
    echo "✅ Installed via ADB fallback."
fi

# Step 4: Launch the app
echo "🚀 Launching app..."
"$ADB" -s "$DEVICE" shell am start -n "app.aeu.box/com.aeu.boxapplication.MainActivity"

echo "✅ Done! Your changes should now be visible on the device."
