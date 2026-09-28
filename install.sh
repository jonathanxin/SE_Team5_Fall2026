#!/bin/bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)" #finds the path for the project folder
JAVAFX_VERSION="27"
MIN_JAVA_VERSION=21
MIN_CLASS_VERSION=$((MIN_JAVA_VERSION + 44))   #java's numbering is +44
JAVAFX_DIR="$PROJECT_DIR/.javafx" #a hidden folder to put javafx files in
LIB_DIR="$JAVAFX_DIR/javafx-sdk-${JAVAFX_VERSION}/lib" 

echo "Updating package lists"
sudo apt-get update -y

#========================checking Java========================#
echo "Checking Java"
if ! command -v javac || ! command -v java; then
    echo "Error: Java not found. Please install JDK $MIN_JAVA_VERSION"
    exit 1
fi
CLASS_VERSION=$(java -XshowSettings:properties -version 2>&1 | awk -F'= ' '/java.class.version/ {print int($2)}') #get number after "= "
if [ -z "$CLASS_VERSION" ] || [ "$CLASS_VERSION" -lt "$MIN_CLASS_VERSION" ]; then
    echo "Error: This application requires Java $MIN_JAVA_VERSION or newer."
    exit 1
fi
echo "Compatible Java version detected."
#========================downloading JavaFX========================#
if [ ! -f "$LIB_DIR/javafx.controls.jar" ]; then #checks if JavaFX is not downloaded
    echo "Downloading JavaFX SDK"
    mkdir -p "$JAVAFX_DIR"
    curl -fL -o "$JAVAFX_DIR/javafx-sdk.zip" "https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip"
    unzip -q -o "$JAVAFX_DIR/javafx-sdk.zip" -d "$JAVAFX_DIR"
    rm "$JAVAFX_DIR/javafx-sdk.zip"
else
    echo "JavaFX SDK already downloaded"
fi
#checks if JavaFX downloaded properly
if [ ! -f "$LIB_DIR/javafx.controls.jar" ]; then
    echo "Error: JavaFX download/extract failed -- javafx.controls.jar not found at $LIB_DIR"
    exit 1
fi
echo "JavaFX ready" 
echo "Install script finished"