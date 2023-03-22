#! /bin/bash

lport="8000"
sport="7000"
mapper="Dropbear"

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -V | --version )
    echo "$version"
    exit
    ;;
  -db | --dropbear )
    mapper="Dropbear"
    ;;
  -l )
    shift; lport=$1
    ;;
  -s )
    shift; sport=$1
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi



cd ssh-mapper/
python2 mapper/mapper.py -l localhost:$lport -s localhost:$sport -c $mapper
