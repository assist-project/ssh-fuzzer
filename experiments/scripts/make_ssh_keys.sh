curr_dir="$(dirname "$0")"
base_path="${curr_dir}/../.."

pushd ${base_path}

mkdir ${base_path}/ssh-keys
mkdir ${base_path}/learner_output
ssh-keygen -t rsa -f ${base_path}/ssh-keys/learner-ssh -N ""

popd