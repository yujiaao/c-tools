#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

pushd "${ROOT_DIR}" >/dev/null

echo "[e2e] Compiling sources..."
npm run compile

echo "[e2e] Running VS Code integration tests..."
node ./client/out/test/runTest.js

popd >/dev/null

