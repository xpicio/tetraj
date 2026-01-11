#!/bin/bash

# Local release script for testing the build process
# Usage: ./scripts/local-release.sh [version]

# Exit on error
set -e

VERSION=${1:-"1.0.0-SNAPSHOT"}
echo "Building Tetraj release $VERSION"

# Build shadowJar
echo "Building shadow JAR..."
./gradlew shadowJar --no-daemon

# Copy JAR with version
echo "Packaging JAR..."
cp build/libs/tetraj-all.jar tetraj-$VERSION.jar
cp build/libs/tetraj-all.jar tetraj.jar

# Generate Javadoc
echo "Generating Javadoc..."
./gradlew javadoc --no-daemon
