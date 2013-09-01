CORPUS_DIR := /bigdisc/android-apps/benign
TOP :=/home/rubin/workspace/dexter/cg_test
include $(TOP)/path_config

JAVAC_FLAGS := -source 1.5 -target 1.5

ifeq ($(shell uname -o), Cygwin)
    CWD := $(shell cygpath -a -m .)
    PROJECT_ROOT := $(shell cygpath -a -m $(TOP)/../libdexter/)
else
    CWD := $(shell realpath .)
    PROJECT_ROOT := $(shell realpath  $(TOP)/../libdexter/)
endif

DEXTER := $(JAVA) -ea -classpath '$(PROJECT_ROOT)/build/classes/:$(PROJECT_ROOT)/build/libs/*' uk.ac.cam.db538.dexter.MainTest $(PROJECT_ROOT)/framework

APP_FILES := $(notdir $(wildcard $(CORPUS_DIR)/*.apk))
OUT_FILES := $(APP_FILES:.apk=.out)

all: $(OUT_FILES)

%.out:
	-timeout 10m $(DEXTER) $(CORPUS_DIR)/$(@:.out=.apk) $(@:.out=.apk) 2>$(@:.out=.err) | tee $@ 
	-[ ! -s $(@:.out=.err) ] && rm -rf $(@:.out=.err)
	-cat $(@:.out=.err)
clean: 
	rm -f *.apk *.out *.err
	
