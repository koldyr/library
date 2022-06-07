#!/bin/sh

git pull
mvn -DskipTests clean install
rm -f ./app/library.jar
cp ./rest/target/library-rest-*.jar ./app/library.jar
