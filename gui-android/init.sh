#!/bin/sh

# clone smali repository & delete its build.gradle file

SMALI_DIR='libdexter/smali'
SMALI_REPO='https://code.google.com/p/smali/'
echo "deleting \"$SMALI_DIR\"..."
rm -rf "$SMALI_DIR" || exit
echo "cloning \"$SMALI_REPO\"..."
git clone -q "$SMALI_REPO" "$SMALI_DIR" || exit
echo "checking out the correct version..."
(cd "$SMALI_DIR" && git checkout -q -b 'v1.4.2') || exit
echo "deleting smali's gradle config file..." 
rm "$SMALI_DIR"/build.gradle || exit

echo "DONE"
