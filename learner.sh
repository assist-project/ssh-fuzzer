#! /bin/bash

file="config.prop"

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -V | --version )
    echo "$version"
    exit
    ;;
  -h | --help )
    echo "Usage: ./learner.sh [opt]"
    echo "    [opt]:"
    echo -e "\t-f   --file\n\t\tChoose a config file for the learner, default config.prop\n"
    echo -e "\t-t   --testfile\n\t\tChoose a test run from input/test-run/\n"
    exit
    ;;
  -f | --file )
    shift; file=$1
    ;;
  -t | --testfile )
    shift; testrun="testWords=./input/test-runs/$1"
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi


cd ssh-learner

if ! [[ -e "./target/ssh-learner.jar" ]]; then
  mvn install
fi

java -jar target/ssh-learner.jar ./input/$file $testrun
