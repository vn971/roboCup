#!/bin/bash -e

host='deb7vostro.pointsgame.net'

cd `dirname $0`/../

rm ./target/*.deb || true
./bin/sbt debian:packageBin
scp ./target/*.deb ${host}:/root/

# also consider this for building packages on a remote machine:
# rsync -aruvz --progress --delete --exclude='/.git' --filter="dir-merge,- .gitignore"  ./ ${host}:/home/robocup-build/dir/
