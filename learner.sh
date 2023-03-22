#! /bin/bash

file="config.prop"

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -V | --version )
    echo "$version"
    exit
    ;;
  -h | --help )
    echo "Usage: ./learner.sh [opt]"
    echo "[opt]:"
    echo -e "\t-f   --file\tchoose a config file for the learner, default config.prop"
    exit
    ;;
  -f | --file )
    shift; file=$1
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi


cd ssh-learner

if ! [[ -e "./target/ssh-learner-jar-with-dependencies.jar" ]]; then
  mvn install
  mvn assembly:single
fi

java -jar target/ssh-learner-jar-with-dependencies.jar ./input/$file
