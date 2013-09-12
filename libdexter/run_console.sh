#!/bin/sh

java -Xmx2048M -ea -cp "build/libs/*" uk.ac.cam.db538.dexter.MainConsole $@
