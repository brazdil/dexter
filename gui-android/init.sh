#!/bin/sh

SMALI_DIR='libdexter/smali'

rm -rf "$SMALI_DIR"
git clone -b 'v1.4.2' 'https://code.google.com/p/smali/' "$SMALI_DIR"
