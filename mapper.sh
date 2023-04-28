#! /bin/bash

lport="8000"
sport="7000"

print_help(){
  echo "Usage: ./mapper.sh [opt]"
  echo "[opt]:"
  echo -e "\t-v   --verbosity <num>\n\t    Select the verbosity of the (clinet) SUT.\n\t    0 - None\n\t    1 - Mild\n\t    2 - Mid\n\t    3 - High\n"
  echo -e "\t--db\n\t    Fuzz Dropbear\n"
  echo -e "\t--ssh\n\t    Fuzz OpenSSH\n"
  echo -e "\t-p    --protocol [Dropbear | OpenSSH]\n\t    Alternate way to define protocol\n"
  echo -e "\t-l <num>\n\t    port to learner\n"
  echo -e "\t-s <num>\n\t    port to server\n"
  echo -e "\t-f   --fuzz [client | server]\n\t    Fuzz the client or the server\n"
  echo -e "\t--client\n\t    Alternate way of fuzzing client\n"
  echo -e "\t--server\n\t    Alternate way of fuzzing server\n"
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
  --db )
    mapper="Dropbear"
    ;;
  --ssh )
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
  --client )
    sut="-f client"
    ;;
  --server )
    sut="-f server"
    ;;
  -v | --verbosity )
    shift; verbosity="-v $1"
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi

if [[ -z "$mapper" ]]; then
  echo -e "==> ERROR: '$mapper' is not a valid protocol"
  print_help
fi



cd ssh-mapper/
python3 mapper/mapper.py -l localhost:$lport -s localhost:$sport $verbosity -c $mapper $sut
