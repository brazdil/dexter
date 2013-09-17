#!/bin/sh

# PREPARE INPUTS

APK_ORIG="$1"
APK_INST="${APK_ORIG}_new.apk"

# input 2 in format Lpackage/class;.method (prototype)V
CLASS_PATH=`echo "$2" | sed "s/L\(.*\);\..* (.*).*/\1/"`
METHOD_NAME=`echo "$2" | sed "s/L.*;\.\(.*\) (.*).*/\1/"`
METHOD_PROTOTYPE=`echo "$2" | sed "s/L.*;\..* \((.*).*\)/\1/"`


echo "extracting Dexter's representation"
./run_console.sh framework "$APK_ORIG" "L${CLASS_PATH};->${METHOD_NAME}${METHOD_PROTOTYPE}" 2> "${METHOD_NAME}.dexter"

echo "baksmali on the result"
baksmali "${APK_INST}"
cp "out/${CLASS_PATH}.smali" "${METHOD_NAME}.smali"
