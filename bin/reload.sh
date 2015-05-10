#!/bin/bash -eu

# to be used on production

# for development it's easier to start with
#   ./bin/sbt ~reStart
# this way you'll have the project restarted on source changes

cd "`dirname "$0"`/../"

mkdir -p log

./bin/stop.sh || true

java -Drun.mode=production \
  -jar ./target/scala-2.11/robocup.jar \
  1>>log/log 2>>log/log &

# write pid
echo $! > server.pid
