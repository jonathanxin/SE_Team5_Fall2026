#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVAFX_VERSION="17.0.2"
JAVAFX_DIR="$PROJECT_DIR/.javafx/javafx17"
MIN_JAVA_VERSION=17

echo "Updating package lists"
sudo apt-get update -y

#========================checking Java========================#
echo "Checking Java"

if ! command -v java >/dev/null 2>&1 || ! command -v javac >/dev/null 2>&1; then
    echo "Java not found. Installing OpenJDK 17."
    sudo apt-get install -y openjdk-17-jdk
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | sed -E 's/.*version "([0-9]+).*/\1/')

if [ -z "$JAVA_VERSION" ] || [ "$JAVA_VERSION" -lt "$MIN_JAVA_VERSION" ]; then
    echo "Installed Java is too old. Installing OpenJDK 17."
    sudo apt-get install -y openjdk-17-jdk
fi

echo "Compatible Java version detected."

#========================JavaFX architecture========================#
ARCH=$(uname -m)

case "$ARCH" in
    aarch64|arm64)
        JAVAFX_PLATFORM="linux-aarch64"
        ;;
    x86_64|amd64)
        JAVAFX_PLATFORM="linux"
        ;;
    *)
        echo "Unsupported architecture: $ARCH"
        exit 1
        ;;
esac

#========================downloading JavaFX========================#
echo "Setting up JavaFX $JAVAFX_VERSION for $ARCH"
mkdir -p "$JAVAFX_DIR"

for MODULE in base graphics controls fxml; do
    JAR="$JAVAFX_DIR/javafx-$MODULE-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"

    if [ ! -f "$JAR" ]; then
        echo "Downloading javafx-$MODULE"
        curl -fL -o "$JAR" \
            "https://repo1.maven.org/maven2/org/openjfx/javafx-$MODULE/$JAVAFX_VERSION/javafx-$MODULE-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"
    fi
done

echo "JavaFX ready"

#=======================downloading PostgreSQL JDBC driver========================#
echo "Downloading PostgreSQL JDBC driver"
curl -fL -o "$PROJECT_DIR/postgresql.jar" \
    "https://jdbc.postgresql.org/download/postgresql-42.7.13.jar"

#=======================setting up database========================#
"$PROJECT_DIR/setup_database.sh"

echo "Install script finished"
