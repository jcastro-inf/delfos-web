#!/bin/bash

#Stop on error
set -e

#Install all tools
aptitude install maven git build-essential tomcat8 tomcat8-admin

#dependencies
git clone https://github.com/apache/commons-math.git
cd commons-math/
mvn clean install -DskipTests
cd ..

git clone https://github.com/RuedigerMoeller/j-text-utils.git
cd j-text-utils/
mvn clean install -DskipTests
cd ..

git clone https://github.com/jcastro-inf/delfos.git
cd delfos/
git checkout develop
mvn clean install -DskipTests
cd ..

git clone https://github.com/jcastro-inf/delfos-web.git
cd delfos-web/
mvn clean install -DskipTests

#Add the user with the password in the tomcat file
nano /etc/tomcat8/tomcat-users.xml

#Put the same credentials in the maven plugin configuration
nano pom.xml

service tomcat8 restart

mvn tomcat7:redeploy

