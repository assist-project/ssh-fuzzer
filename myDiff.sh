#! /bin/bash
v=$2
if [[ $# < 1 ]]; then
    echo -e "./my_diff.sh <file> <paramiko version (1.16.0 / 3.1.0)>"
    exit
fi
if [[ $# < 2 ]]; then
    v="1.16.0"
fi
diff --color ssh-mapper/manualparamiko/$1 ssh-mapper/official_paramiko/paramiko-$v/paramiko/$1
