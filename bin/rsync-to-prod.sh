#!/bin/bash -e

sbt package

ssh pointsgame.net service tomcat7 stop

rsync -aruvz --progress --delete target/webapp/ pointsgame.net:/var/lib/tomcat7/webapps/tournament/

ssh pointsgame.net service tomcat7 start
