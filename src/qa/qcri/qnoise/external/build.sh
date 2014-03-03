#!/bin/bash

if ! [ -n "$QNOISE_HOME" ]; then 
    echo "QNOISE_HOME is not set."
    exit 1
fi

thrift -strict -gen java:private-members -out $QNOISE_HOME/src $QNOISE_HOME/src/qa/qcri/qnoise/external/qnoise.thrift
thrift -gen py:new_style -out $QNOISE_HOME/src/qa/qcri/qnoise/external/binding/py $QNOISE_HOME/src/qa/qcri/qnoise/external/qnoise.thrift
