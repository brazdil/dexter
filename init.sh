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

# clone dalvik & libcore google repo
DALVIK_DIR='libdexter/dalvik'
DALVIK_REPO='https://android.googlesource.com/platform/dalvik'
DALVIK_BUILDFILE='build.gradle.dx'
LIBCORE_DIR='libdexter/libcore'
LIBCORE_REPO='https://android.googlesource.com/platform/libcore'
LIBCORE_BUILDFILE='build.gradle.libcore'
echo "deleting \"$LIBCORE_DIR\"..."
rm -rf "$LIBCORE_DIR" || exit
echo "deleting \"$DALVIK_DIR\"..."
rm -rf "$DALVIK_DIR" || exit
echo "cloning \"$LIBCORE_REPO\"..."
git clone -q "$LIBCORE_REPO" "$LIBCORE_DIR" || exit
echo "cloning \"$DALVIK_REPO\"..."
git clone -q "$DALVIK_REPO" "$DALVIK_DIR" || exit
echo "copying libcore build.gradle"
cp "$LIBCORE_BUILDFILE" "$LIBCORE_DIR"/dex/build.gradle || exit
echo "copying dx build.gradle"
cp "$DALVIK_BUILDFILE" "$DALVIK_DIR"/dx/build.gradle || exit

# download smali jar to dexter_test
DEXTER_TEST_LIBDIR='dexter_test/libs/'
echo "downloading smali.jar to \"$DEXTER_TEST_LIBDIR\"..."
mkdir -p "$DEXTER_TEST_LIBDIR"
wget -q 'https://smali.googlecode.com/files/smali-1.4.2.jar' -P "$DEXTER_TEST_LIBDIR"

echo "DONE"
