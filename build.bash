#!/bin/bash
set -u -e
export JAVAFX_SDK=./.javafx/javafx-sdk-27/lib/
javac --module-path $JAVAFX_SDK --add-modules javafx.controls,javafx.fxml -d bin ./src/*.java
java --module-path $JAVAFX_SDK --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp .:bin:postgresql.jar Main
