#!/bin/bash -eu

host='deb7vostro.pointsgame.net'
user='robocup-sbt'
sshAddress="${user}@${host}:/home/${user}/dir/"

cd `dirname $0`/../

rsync -aruvz --progress --delete \
	--exclude='/.git' \
	--include='/src/main/resources/props/production.*' \
	--filter='dir-merge,- .gitignore' \
	./ "$sshAddress"
