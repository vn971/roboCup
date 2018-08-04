#!/bin/bash -eu

cd "`dirname "$0"`/../"

if [ -e server.pid ]; then
  kill "`cat server.pid`" 2>/dev/null || true
  rm server.pid
fi
