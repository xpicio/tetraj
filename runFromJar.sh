#!/bin/sh

./gradlew shadowJar --no-daemon && \
  cp build/libs/tetraj-all.jar tetraj.jar && \
  java -jar tetraj.jar
