#!/bin/bash -e

sbt package

ssh root@net.pg.lenovo service tomcat7 stop

rsync -aruvz --progress --delete target/webapp/ root@net.pg.lenovo:/var/lib/tomcat7/webapps/tournament/

ssh root@net.pg.lenovo service tomcat7 start
