#!/bin/bash

#Stop on error
set -e

#Install all tools
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update

aptitude install maven git build-essential oracle-java8-installer tomcat8 tomcat8-admin

#Set JAVA_HOME for tomcat8
#write this after set -e: 
export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
nano /etc/init.d/tomcat8


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

mvn tomcat7:redeploy

#Use port 80 insteadof 8080

#Set AUTHBIND=yes
	#install the tool
	aptitude install authbind
	
	#Set privileges on the port 80
	touch /etc/authbind/byport/80
	chmod 500 /etc/authbind/byport/80
	chown tomcat8 /etc/authbind/byport/80
	
	#Tell tomcat8 to use authbind by setting AUTHBIND=yes
	nano /etc/default/tomcat8

	#Change port 8080 to 80
	nano /etc/tomcat8/server.xml

service tomcat8 restart







#Access through ssh 
ssh delfos@serezade.ujaen.es -p 8001

#Web access
lynx http://serezade.ujaen.es:8000/

