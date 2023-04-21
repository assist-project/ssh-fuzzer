#! /bin/bash

lport="8000"
sport="7000"
mapper="Dropbear"


print_help(){
  echo "Usage: ./mapper.sh [opt]"
  echo "[opt]:"
  echo -e "\t-db   --dropbear\n\t    Fuzz Dropbear\n"
  echo -e "\t-ssh  --openssh\n\t    Fuzz OpenSSH\n"
  echo -e "\t-p    --protocol [Dropbear | OpenSSH]\n\t    Alternate way to define protocol\n"
  echo -e "\t-l <num>\n\t    port to learner\n"
  echo -e "\t-s <num>\n\t    port to server\n"
  echo -e "\t-f   --fuzz [client | server]\n\t    Fuzz the client or the server\n"
  exit
}

if [[ $# < 1 ]]; then
  print_help
fi

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -h | --help )
    print_help
    ;;
  -p | --protocol )
    shift; mapper=$1
    ;;
  -db | --dropbear )
    mapper="Dropbear"
    ;;
  -ssh | --openssh )
    mapper="OpenSSH"
    ;;
  -l )
    shift; lport=$1
    ;;
  -s )
    shift; sport=$1
    ;;
  -f | --fuzz )
    shift; sut="-f $1"
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi



cd ssh-mapper/
python3 mapper/mapper.py -l localhost:$lport -s localhost:$sport -c $mapper $sut
