#!/usr/bin/env bash
set -euo pipefail

resolve_emulator() {
  local sdk
  for sdk in "${ANDROID_SDK_ROOT:-}" "${ANDROID_HOME:-}" "$HOME/Library/Android/sdk"; do
    if [[ -n "$sdk" && -x "$sdk/emulator/emulator" ]]; then
      echo "$sdk/emulator/emulator"
      return 0
    fi
  done
  if command -v emulator >/dev/null 2>&1; then
    echo "emulator"
    return 0
  fi
  return 1
}

resolve_adb() {
  local sdk
  for sdk in "${ANDROID_SDK_ROOT:-}" "${ANDROID_HOME:-}" "$HOME/Library/Android/sdk"; do
    if [[ -n "$sdk" && -x "$sdk/platform-tools/adb" ]]; then
      echo "$sdk/platform-tools/adb"
      return 0
    fi
  done
  if command -v adb >/dev/null 2>&1; then
    echo "adb"
    return 0
  fi
  return 1
}

adb_bin="$(resolve_adb || true)"
emu_bin="$(resolve_emulator || true)"

if [[ -z "${adb_bin:-}" ]]; then
  echo "adb not found. Ensure Android Platform-Tools are installed and on PATH."
  exit 1
fi

if "$adb_bin" devices | awk 'NR>1 && $1 ~ /^emulator-/{found=1} END{exit !found}'; then
  echo "AVD already running."
  exit 0
fi

if [[ -z "${emu_bin:-}" ]]; then
  echo "emulator not found. Ensure Android Emulator is installed."
  exit 1
fi

avd_name="${AVD_NAME:-}"
if [[ -z "$avd_name" ]]; then
  avds="$("$emu_bin" -list-avds || true)"
  avd_count="$(printf "%s\n" "$avds" | awk 'NF' | wc -l | tr -d ' ')"
  if [[ "$avd_count" -eq 0 ]]; then
    echo "No AVDs found. Create one in Android Studio Device Manager."
    exit 1
  fi
  if [[ "$avd_count" -gt 1 ]]; then
    echo "Multiple AVDs found. Set AVD_NAME to select one:"
    printf "%s\n" "$avds"
    exit 1
  fi
  avd_name="$(printf "%s\n" "$avds" | awk 'NF{print; exit}')"
fi

nohup "$emu_bin" -avd "$avd_name" -netdelay none -netspeed full >/tmp/avd.log 2>&1 &

for _ in {1..60}; do
  if "$adb_bin" devices | awk 'NR>1 && $1 ~ /^emulator-/{found=1} END{exit !found}'; then
    echo "AVD started: $avd_name"
    exit 0
  fi
  sleep 2
done

echo "Timed out waiting for AVD: $avd_name"
exit 1
