#!/bin/bash
#
#

mvn -f ./secret_consumer/pom.xml clean install

docker rm $(docker ps -a -q)
docker volume prune -f
docker build ./secret_consumer/ -t secret_consumer
docker-compose up
