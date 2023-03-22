#! /bin/bash


port="7000"
db=1

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -V | --version )
    echo "$version"
    exit
    ;;
  -h | --help )
    echo "Usage: ./sut.sh [opt]"
    echo "[opt]:"
    echo -e "\t-db  --dropbear"
    echo -e "\t-os  --oppenssh"
    echo -e "\t-p\tport to listen to"
    exit
    ;;
  -db | --dropbear )
    db=1
    ;;
  -os | --openssh )
    openssh=1
    ;;
  -p | --port )
	shift; port=$1
	;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi

if [[ $db -eq 1 ]]; then
  cd SUTs/dropbear-2022.83
  if ! [[ -e "./dropbear" ]]; then
    ./configure
    make
  fi
	sudo ./dropbear -R -F -E -p localhost:$port
elif [[ $openssh -eq 1 ]]; then
	echo "OpenSSH"
fi
