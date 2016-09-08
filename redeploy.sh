#!/bin/bash

mvn clean install tomcat7:redeploy

sudo service tomcat8 restart
