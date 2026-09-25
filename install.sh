#!/bin/bash
set -e
echo "Updating package lists"
sudo apt-get update -y
echo "Downloading JDK"
sudo apt-get install -y openjdk-25-jdk curl unzip

#========================downloading JavaFX========================#
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)" #finds the path for the project folder
JAVAFX_VERSION="27"
JAVAFX_DIR="$PROJECT_DIR/.javafx" #a hidden folder to put javafx files in
LIB_DIR="$JAVAFX_DIR/javafx-sdk-${JAVAFX_VERSION}/lib" 
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

#========================Compiling========================#

rm -rf bin
mkdir -p bin
echo "Compiling"
javac --module-path "$LIB_DIR" --add-modules javafx.controls,javafx.fxml -d bin "$PROJECT_DIR"/*.java
cp "$PROJECT_DIR"/*.fxml bin/ #view.fxml
cp "$PROJECT_DIR"/*.jpg bin/ #splashscreen image


#========================Running========================#
echo "Running"
java --module-path "$LIB_DIR" --add-modules javafx.controls,javafx.fxml -cp bin Main
