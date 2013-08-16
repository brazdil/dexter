#!/bin/sh
for x in `find | grep ".java$"`
do
	astyle -s4 -n "$x"
done
