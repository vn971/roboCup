#!/bin/bash -e

cd `dirname $0`/../

./bin/sbt package

ssh pointsgame.net service tomcat7 stop
rsync -aruvz --progress --delete target/webapp/ lenovo.pointsgame.net:/var/lib/tomcat7/webapps/tournament/
ssh pointsgame.net service tomcat7 start
