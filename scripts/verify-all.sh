#!/usr/bin/env sh
set -eu
mvn clean test
echo "All Maven tests passed."
echo "You can now run: mvn spring-boot:run"
