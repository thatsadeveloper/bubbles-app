#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load .env into environment
# shellcheck source=/dev/null
source "$SCRIPT_DIR/load-env.sh"

cd "$ROOT_DIR/backend"

# Run Spring Boot app
./gradlew bootRun


