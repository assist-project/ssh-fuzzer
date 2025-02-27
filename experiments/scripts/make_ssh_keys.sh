curr_dir="$(dirname "$0")"
base_path="${curr_dir}/../.."

pushd ${base_path}

if [ -z "$( ls -A './ssh-keys' )" ]; then
   ssh-keygen -t rsa -f ${PWD}/ssh-keys/learner-ssh -N ""
fi
popd