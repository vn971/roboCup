#!/bin/bash -eu

cd "`dirname "$0"`/../"

if [ -e server.pid ]; then
  kill "`cat server.pid`"
  wait "`cat server.pid`" || true
  rm server.pid
fi
