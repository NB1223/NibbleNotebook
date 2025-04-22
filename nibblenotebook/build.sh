#!/bin/bash

OLD_JAVA_HOME=$JAVA_HOME
OLD_PATH=$PATH

export JAVA_HOME=/usr/lib/jvm/jdk-23.0.2
export PATH=$JAVA_HOME/bin:$PATH

echo "Switched to Java 23 for building"
java -version

./mvnw clean install
./mvnw spring-boot:run

    export JAVA_HOME=$OLD_JAVA_HOME
export PATH=$OLD_PATH

echo "Build completed. Reverted to Java 8:"
java -version 