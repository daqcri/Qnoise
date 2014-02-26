#!/bin/bash

if ! [ -n "$QNOISE_HOME" ]; then 
    echo "QNOISE_HOME is not set."
    exit 1
fi
    
mkdir -p $QNOISE_HOME/out/gen/py
thrift -gen java:private-members -out $QNOISE_HOME/src $QNOISE_HOME/src/qa/qcri/qnoise/external/qnoise.thrift

