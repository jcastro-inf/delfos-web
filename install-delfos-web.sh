#!/bin/bash

#Stop on error
set -e

#Install all tools
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update

aptitude install maven git build-essential oracle-java8-installer tomcat8 tomcat8-admin 

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

