#!/usr/bin/env bash
# After the agent stops: run constitution gates. If they fail, continue the loop
# (up to hooks.json loop_limit 5). If they pass, force HITL — do not merge.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

if [[ -z "${JAVA_HOME:-}" && -d /opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home ]]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
fi
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}$PATH"

if ! command -v mvn >/dev/null 2>&1; then
  echo '{"followup_message":"Maven is not on PATH. Install Maven and re-run quality gates (mvn test + SastScanner) before HITL."}'
  exit 0
fi

TEST_LOG="$(mktemp)"
SAST_LOG="$(mktemp)"
trap 'rm -f "$TEST_LOG" "$SAST_LOG"' EXIT

set +e
mvn -q -B test >"$TEST_LOG" 2>&1
TEST_STATUS=$?
mvn -q -B exec:java -Dexec.mainClass=com.example.shortener.sast.SastScanner -Dexec.classpathScope=compile -Dexec.args="src/main/java" >"$SAST_LOG" 2>&1
SAST_STATUS=$?
set -e

if [[ "$TEST_STATUS" -ne 0 || "$SAST_STATUS" -ne 0 ]]; then
  snippet=$(tail -n 40 "$TEST_LOG" "$SAST_LOG" | tr '"' "'" | tr '\n' ' ' | cut -c1-1200)
  printf '{"followup_message":"Quality gates failed (tests=%s sast=%s). Diagnose, patch, and re-run. Do not present HITL yet. Output: %s"}\n' \
    "$TEST_STATUS" "$SAST_STATUS" "$snippet"
  exit 0
fi

echo '{"followup_message":"Automated gates are green (mvn test + SAST). STOP for HITL: update hitl/HITL_GATE.md, open a PR, and wait for human approval. Do not merge."}'
