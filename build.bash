#!/bin/bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

JDK_DIR="$PROJECT_DIR/.jdk"
JAVAFX_DIR="$PROJECT_DIR/.javafx/javafx21"
JDBC_JAR="$PROJECT_DIR/postgresql.jar"
BIN_DIR="$PROJECT_DIR/bin"

#========================checking installation========================#

if [ ! -x "$JDK_DIR/bin/java" ] || [ ! -x "$JDK_DIR/bin/javac" ]; then
    echo "Error: Project JDK not found. Run ./install.sh first."
    exit 1
fi

if [ ! -d "$JAVAFX_DIR" ]; then
    echo "Error: JavaFX not found. Run ./install.sh first."
    exit 1
fi

if [ ! -f "$JDBC_JAR" ]; then
    echo "Error: PostgreSQL JDBC driver not found. Run ./install.sh first."
    exit 1
fi

#========================compiling========================#

rm -rf "$BIN_DIR"
mkdir -p "$BIN_DIR"

"$JDK_DIR/bin/javac" \
    --module-path "$JAVAFX_DIR" \
    --add-modules javafx.controls,javafx.fxml \
    -d "$BIN_DIR" \
    "$PROJECT_DIR"/src/*.java

#========================running========================#

"$JDK_DIR/bin/java" \
    --module-path "$JAVAFX_DIR" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$PROJECT_DIR:$BIN_DIR:$JDBC_JAR" \
    Main
