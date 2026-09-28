#!/bin/bash
set -u -e
export JAVAFX_SDK=/home/student/SE_Team5_Fall2026/lib/
javac --module-path $JAVAFX_SDK --add-modules javafx.controls,javafx.fxml -d bin ./src/*.java
java --module-path $JAVAFX_SDK --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp .:bin:/usr/share/java/postgresql.jar src.Main