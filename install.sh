#!/bin/bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

JAVA_VERSION="21"
JDK_DIR="$PROJECT_DIR/.jdk"

JAVAFX_VERSION="21"
JAVAFX_DIR="$PROJECT_DIR/.javafx/javafx21"

JDBC_VERSION="42.7.13"

#========================detecting architecture========================#
ARCH=$(uname -m)

case "$ARCH" in
    x86_64|amd64)
        ADOPTIUM_ARCH="x64"
        JAVAFX_PLATFORM="linux"
        ;;
    aarch64|arm64)
        ADOPTIUM_ARCH="aarch64"
        JAVAFX_PLATFORM="linux-aarch64"
        ;;
    *)
        echo "Error: Unsupported architecture: $ARCH"
        exit 1
        ;;
esac

echo "Detected architecture: $ARCH"

#========================required Linux tools========================#
echo "Installing required tools"

sudo apt-get update -y
sudo apt-get install -y curl ca-certificates tar gzip

#========================installing JDK 21========================#
echo "Checking project JDK"

INSTALLED_JAVA_VERSION=""

if [ -x "$JDK_DIR/bin/java" ] && [ -x "$JDK_DIR/bin/javac" ]; then
    INSTALLED_JAVA_VERSION=$(
        "$JDK_DIR/bin/java" -version 2>&1 |
        head -n 1 |
        sed -E 's/.*version "([0-9]+).*/\1/'
    )
fi

if [ "$INSTALLED_JAVA_VERSION" != "$JAVA_VERSION" ]; then
    echo "Installing Eclipse Temurin JDK $JAVA_VERSION for $ARCH"

    rm -rf "$JDK_DIR"
    mkdir -p "$JDK_DIR"

    JDK_ARCHIVE=$(mktemp)

    curl -fL -o "$JDK_ARCHIVE" \
        "https://api.adoptium.net/v3/binary/latest/${JAVA_VERSION}/ga/linux/${ADOPTIUM_ARCH}/jdk/hotspot/normal/eclipse"

    tar -xzf "$JDK_ARCHIVE" \
        -C "$JDK_DIR" \
        --strip-components=1

    rm -f "$JDK_ARCHIVE"
else
    echo "JDK $JAVA_VERSION already installed for this project"
fi

ACTIVE_JAVA_VERSION=$(
    "$JDK_DIR/bin/java" -version 2>&1 |
    head -n 1 |
    sed -E 's/.*version "([0-9]+).*/\1/'
)

if [ "$ACTIVE_JAVA_VERSION" != "$JAVA_VERSION" ]; then
    echo "Error: JDK $JAVA_VERSION setup failed."
    exit 1
fi

echo "Using project JDK:"
"$JDK_DIR/bin/java" -version

#========================downloading JavaFX========================#
echo "Setting up JavaFX $JAVAFX_VERSION for $ARCH"

mkdir -p "$JAVAFX_DIR"

for MODULE in base graphics controls fxml; do

    JAR="$JAVAFX_DIR/javafx-$MODULE-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"

    if [ ! -s "$JAR" ]; then
        echo "Downloading javafx-$MODULE"

        TEMP_JAR="$JAR.tmp"
        rm -f "$TEMP_JAR"

        curl -fL -o "$TEMP_JAR" \
            "https://repo1.maven.org/maven2/org/openjfx/javafx-$MODULE/$JAVAFX_VERSION/javafx-$MODULE-$JAVAFX_VERSION-$JAVAFX_PLATFORM.jar"

        mv "$TEMP_JAR" "$JAR"
    fi

done

echo "JavaFX ready"

#=======================downloading PostgreSQL JDBC driver========================#
echo "Downloading PostgreSQL JDBC driver"

JDBC_JAR="$PROJECT_DIR/postgresql.jar"
JDBC_TEMP="$JDBC_JAR.tmp"

rm -f "$JDBC_TEMP"

curl -fL -o "$JDBC_TEMP" \
    "https://jdbc.postgresql.org/download/postgresql-$JDBC_VERSION.jar"

mv "$JDBC_TEMP" "$JDBC_JAR"

#=======================setting up database========================#
chmod +x "$PROJECT_DIR/setup_database.sh"

"$PROJECT_DIR/setup_database.sh"

echo "Install script finished successfully"
