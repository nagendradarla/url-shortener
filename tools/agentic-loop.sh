#!/usr/bin/env bash
# Agentic quality-gate loop (constitution IV).
# implement -> compile -> test -> sast -> {pass -> HITL stop | fail -> exit 1}
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ -z "${JAVA_HOME:-}" ]]; then
  if [[ -d /opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home ]]; then
    export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
  fi
fi
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}$PATH"

ITERATION="${1:-1}"
LOG="${ROOT}/agentic_loop_log.md"
REPORT_DIR="${ROOT}/hitl"
mkdir -p "${REPORT_DIR}"

echo "==> Iteration ${ITERATION}: compile + test"
mvn -q -B test | tee "${REPORT_DIR}/test-report.txt"

echo "==> Iteration ${ITERATION}: SAST"
mvn -q -B exec:java \
  -Dexec.mainClass=com.example.shortener.sast.SastScanner \
  -Dexec.classpathScope=compile \
  -Dexec.args="src/main/java" | tee "${REPORT_DIR}/sast-report.txt"

{
  echo ""
  echo "## Iteration ${ITERATION} ($(date -u +%Y-%m-%dT%H:%M:%SZ))"
  echo ""
  echo "**Test gate:** see hitl/test-report.txt"
  echo "**Security gate:** see hitl/sast-report.txt"
  echo "**Decision:** automated gates green. STOP for HITL — do not merge."
} >> "${LOG}"

echo "All automated gates green. Present hitl/HITL_GATE.md for human approval."
