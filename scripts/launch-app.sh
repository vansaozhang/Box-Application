#!/usr/bin/env bash
set -euo pipefail

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
if [[ -z "${adb_bin:-}" ]]; then
  echo "adb not found. Ensure Android Platform-Tools are installed and on PATH."
  exit 1
fi

component="${1:-com.aeu.boxapplication/.MainActivity}"
"$adb_bin" shell am start -n "$component"
