#!/usr/bin/env bash
set -euo pipefail

# Load environment variables from the project root .env into the current shell
# Usage: source scripts/load-env.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

ENV_FILE="$ROOT_DIR/.env"

if [[ -f "$ENV_FILE" ]]; then
  # Export all variables defined in .env without printing them
  set -a
  # shellcheck disable=SC1090
  . "$ENV_FILE"
  set +a
else
  echo ".env not found at $ENV_FILE — continuing with current environment" >&2
fi


