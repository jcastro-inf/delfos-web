#!/bin/bash

git pull 

mvn clean install tomcat7:redeploy

sudo service tomcat8 restart
