#!/usr/bin/env bash

mvn clean package -T 2 -DskipTests
java -jar target/demo.war
