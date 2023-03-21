#! /bin/bash


port="7000"


while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -V | --version )
    echo "$version"
    exit
    ;;
  -db | --dropbear )
    db=1
    ;;
  -ossh | --openssh )
    openssh=1
    ;;
  -p | --port )
	shift; port=$1
	;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi

if [[ $db -eq 1 ]]; then
	cd ssh-fuzzer/SUTs/dropbear-2022.83
	sudo ./dropbear -R -F -E -p localhost:$port
elif [[ $openssh -eq 1 ]]; then
	echo "OpenSSH"
fi