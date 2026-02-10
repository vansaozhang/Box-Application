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

if "$adb_bin" devices | awk 'NR>1 && $2=="device"{found=1} END{exit !found}'; then
  echo "Device already connected."
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
    preferred="${AVD_PREFERRED:-}"
    if [[ -n "$preferred" ]]; then
      preferred_matches="$(printf "%s\n" "$avds" | awk 'NF' | grep -i -F "$preferred" || true)"
      if [[ -n "$preferred_matches" ]]; then
        avd_name="$(printf "%s\n" "$preferred_matches" | awk 'NF{print; exit}')"
        preferred_count="$(printf "%s\n" "$preferred_matches" | awk 'NF' | wc -l | tr -d ' ')"
        if [[ "$preferred_count" -gt 1 ]]; then
          echo "Multiple AVDs matched AVD_PREFERRED=\"$preferred\"; using: $avd_name"
        fi
      fi
    fi
    if [[ -z "${avd_name:-}" ]]; then
      avd_name="$(printf "%s\n" "$avds" | awk 'NF{print; exit}')"
      echo "Multiple AVDs found; defaulting to: $avd_name"
      echo "Set AVD_NAME or AVD_PREFERRED to choose a specific AVD."
    fi
  else
    avd_name="$(printf "%s\n" "$avds" | awk 'NF{print; exit}')"
  fi
fi

emulator_args=(-avd "$avd_name" -netdelay none -netspeed full)
if [[ -n "${AVD_FLAGS:-}" ]]; then
  # shellcheck disable=SC2206
  emulator_args+=($AVD_FLAGS)
else
  emulator_args+=(-no-snapshot-load -no-snapshot-save)
fi

launch_method=""
launch_emulator() {
  local log="/tmp/avd.log"

  # On macOS, launchd is the most reliable way to detach from task runners that
  # kill the entire process tree when a task ends.
  if command -v launchctl >/dev/null 2>&1; then
    local label="com.aeu.boxapplication.emu.${avd_name//[^A-Za-z0-9]/_}.$RANDOM"
    local cmd
    cmd="$(printf '%q ' "$emu_bin" "$@")"
    if launchctl submit -l "$label" -- /bin/sh -c "exec $cmd </dev/null >>'$log' 2>&1"; then
      launch_method="launchctl"
      echo ""
      return 0
    fi
  fi

  # Fallbacks when launchctl isn't available.
  if command -v setsid >/dev/null 2>&1; then
    launch_method="setsid"
    setsid "$emu_bin" "$@" </dev/null >>"$log" 2>&1 &
  else
    launch_method="nohup"
    nohup "$emu_bin" "$@" </dev/null >>"$log" 2>&1 &
  fi
  echo $!
}

start_pid="$(launch_emulator "${emulator_args[@]}")"
if [[ -n "${launch_method:-}" ]]; then
  echo "Emulator launch method: $launch_method"
fi

# If the emulator exits immediately, retry once with safer flags.
if [[ -n "${start_pid:-}" && -z "${AVD_FLAGS:-}" ]]; then
  sleep 8
  if ! kill -0 "$start_pid" >/dev/null 2>&1; then
    echo "Emulator exited early; retrying with safe flags."
    emulator_args+=(-gpu swiftshader_indirect -wipe-data)
    start_pid="$(launch_emulator "${emulator_args[@]}")"
  fi
fi

wait_for_emulator() {
  local serial=""
  for _ in {1..120}; do
    serial="$("$adb_bin" devices | awk 'NR>1 && $1 ~ /^emulator-/ && $2=="device" {print $1; exit}')"
    if [[ -n "$serial" ]]; then
      echo "$serial"
      return 0
    fi
    sleep 2
  done
  return 1
}

wait_for_boot() {
  local serial="$1"
  for _ in {1..120}; do
    boot_completed="$("$adb_bin" -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
    dev_boot_completed="$("$adb_bin" -s "$serial" shell getprop dev.bootcomplete 2>/dev/null | tr -d '\r' || true)"
    if [[ "$boot_completed" == "1" || "$dev_boot_completed" == "1" ]]; then
      return 0
    fi
    sleep 2
  done
  return 1
}

serial="$(wait_for_emulator || true)"
if [[ -z "${serial:-}" ]]; then
  echo "Timed out waiting for AVD to appear online: $avd_name"
  exit 1
fi

if ! wait_for_boot "$serial"; then
  echo "Timed out waiting for AVD to finish booting: $avd_name ($serial)"
  exit 1
fi

echo "AVD started and booted: $avd_name ($serial)"
exit 0
