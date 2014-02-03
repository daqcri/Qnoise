#!/bin/bash
type -P javac > /dev/null 2>&1 || { echo "JDK cannot be found, please check your PATH var."; exit 1; }

if ! [ -d "out" ]; then
    echo Qnoise is not yet compiled, please first run 'ant' to build it.
else
    cmd='java -cp out/bin/*:. qa.qcri.qnoise.Qnoise'
    exec $cmd $@
fi
