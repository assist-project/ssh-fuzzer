#! /bin/bash

lport="8000"
sport="7000"
host="localhost"
remote="localhost"

print_help(){
  echo "Usage: ./mapper.sh [opt]"
  echo "[opt]:"
  echo -e "\t-v <num>\n\t--verbosity=<num>\n\t    Select the verbosity of the (clinet) SUT.\n\t    0 - None (default)\n\t    1 - Mild\n\t    2 - Mid\n\t    3 - High\n"
  echo -e "\t--db\n\t    Fuzz Dropbear\n"
  echo -e "\t--ssh\n\t    Fuzz OpenSSH\n"
  echo -e "\t-p [Dropbear | OpenSSH]\n\t--protocol [Dropbear | OpenSSH]\n\t    Alternate way to define protocol\n"
  echo -e "\t--remote [ip-address]\n\t    The ip address on which the SUT will communicate on (default: localhost)\n"
  echo -e "\t--host [ip-address\n\t    The ip address on which the Learner will communicate on (default: localhost)\n"
  echo -e "\t-l <num>\n\t    Port to learner (default: 8000)\n"
  echo -e "\t-s <num>\n\t    Port to server (default: 7000)\n"
  echo -e "\t-f [client | server]\n\t--fuzz [client | server]\n\t    Fuzz the client or the server\n"
  echo -e "\t--client\n\t    Alternate way of fuzzing client\n"
  echo -e "\t--server\n\t    Alternate way of fuzzing server\n"
  exit
}

check_multiple(){
  if ! [[ -z "${!1}" ]]; then
    echo -e "==> ERROR: Multiple $1 arguments defined...\n==> EXTING <==\n"
    print_help
  fi
}

if [[ $# < 1 ]]; then
  print_help
fi

while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
  -h | --help )
    print_help
    ;;
  -p | --protocol )
    check_multiple "mapper"
    shift; mapper=$1
    ;;
  --db )
    check_multiple "mapper"
    mapper="Dropbear"
    ;;
  --ssh )
    check_multiple "mapper"
    mapper="OpenSSH"
    ;;
  -l )
    shift; lport=$1
    ;;
  -s )
    shift; sport=$1
    ;;
  -f | --fuzz )
    check_multiple "sut"
    shift; sut="-f $1"
    ;;
  --client )
    check_multiple "sut"
    sut="-f client"
    ;;
  --server )
    check_multiple "sut"
    sut="-f server"
    ;;
  --verbosity=* )
    verbosity="${1#*=}"
    ;;
  -v)
    check_multiple "verbosity"
    shift; verbosity="-v $1"
    ;;
  --remote )
    shift; remote="$1"
    ;;
  --host )
    shift; host="$1"
    ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi

if [[ -z "$mapper" ]]; then
  echo -e "==> ERROR: '$mapper' is not a valid protocol"
  print_help
fi



cd ssh-mapper/
python3 mapper/mapper.py -l $host:$lport -s $remote:$sport $verbosity -c $mapper $sut
