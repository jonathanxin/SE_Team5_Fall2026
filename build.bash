#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVAFX_DIR="$PROJECT_DIR/.javafx/javafx17"

rm -rf "$PROJECT_DIR/bin"
mkdir -p "$PROJECT_DIR/bin"

javac \
    --module-path "$JAVAFX_DIR" \
    --add-modules javafx.controls,javafx.fxml \
    -d "$PROJECT_DIR/bin" \
    "$PROJECT_DIR"/src/*.java

java \
    --module-path "$JAVAFX_DIR" \
    --add-modules javafx.controls,javafx.fxml \
    --enable-native-access=javafx.graphics \
    -cp "$PROJECT_DIR:$PROJECT_DIR/bin:$PROJECT_DIR/postgresql.jar" \
    Main
