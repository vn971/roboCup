#!/bin/bash -eu

cd "`dirname "$0"`/../"

if [ -e server.pid ]; then
  kill -9 `cat server.pid` || true
  rm server.pid
fi
