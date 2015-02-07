#!/bin/bash

$ANDROID_HOME/platform-tools/adb -s MTP22343000409 install -r bin/MobileConnectSDKTest-debug.apk
$ANDROID_HOME/platform-tools/adb -s MTP22343000409 logcat

