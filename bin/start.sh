#!/bin/bash -e

cd "`dirname "$0"`/../"

./bin/sbt assembly
./bin/stop.sh

mkdir -p log

java -Drun.mode=production \
  -jar ./target/scala-2.11/robocup.jar \
  1>>log/log 2>>log/log &

# write pid
echo $! > server.pid

tail -f log/log
