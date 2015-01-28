#!/bin/bash -eu

cd "`dirname "$0"`/../"

if [ -e server.pid ]; then
  kill `cat server.pid`
  wait `cat server.pid`
  rm server.pid
fi
