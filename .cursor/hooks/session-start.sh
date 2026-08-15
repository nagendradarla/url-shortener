#!/usr/bin/env bash
cat <<'EOF'
{
  "additional_context": "Constitution is in .specify/memory/constitution.md. Java-only. Run quality gates (mvn test + SastScanner) before claiming done. Max 5 autonomous iterations. Stop at HITL — never merge."
}
EOF
