#!/bin/bash -e

cd `dirname $0`/../
host='deb7vostro.pointsgame.net'

./bin/sbt package

ssh ${host} service tomcat7 stop
rsync -aruvz --progress --delete target/webapp/ ${host}:/var/lib/tomcat7/webapps/tournament/
ssh ${host} service tomcat7 start
