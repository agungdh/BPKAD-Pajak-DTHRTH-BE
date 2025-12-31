#!/bin/bash
# Run with seeding profile and disable web server to auto-exit
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=seeding \
  -Dspring-boot.run.jvmArguments="-Dspring.main.web-application-type=none"
