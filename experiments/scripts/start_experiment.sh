#!/bin/bash

# Define paths
SSH_KEY_DIR="../orchestration/ssh-keys"
SSH_KEY_NAME="learner-ssh"
DOCKER_COMPOSE_DIR="../orchestration"

# Ensure the ssh-keys directory exists
mkdir -p "${SSH_KEY_DIR}"

# Check if the SSH keys exist, if not, generate them
if [[ ! -f "${SSH_KEY_DIR}/${SSH_KEY_NAME}" || ! -f "${SSH_KEY_DIR}/${SSH_KEY_NAME}.pub" ]]; then
    echo "SSH keys not found. Generating new SSH keys..."
    ssh-keygen -t rsa -b 4096 -f "${SSH_KEY_DIR}/${SSH_KEY_NAME}" -N ""
    echo "SSH keys generated at ${SSH_KEY_DIR}"
else
    echo "SSH keys already exist. Skipping key generation."
fi


print_usage() {
    echo "Usage:"
    echo "./start-experiment.sh <experiment> [ra] [learning_algorithm]"
    echo
    echo "Examples:"
    echo "RA Learning:"
    echo "./start-experiment.sh dropbear ra RALAMBDA"
    echo "./start-experiment.sh openssh8 ra RASTAR"
    echo
    echo "Mealy Learning:"
    echo "./start-experiment.sh openssh8"
    echo "./start-experiment.sh dropbear"
    exit 1
}

EXPERIMENT=$1
MODE=$2
ALGO=$3


# Validate input and start corresponding docker-compose
if [[ ! "${EXPERIMENT}" =~ ^(openssh7|openssh8|dropbear)$ ]]; then
    echo "Error: Invalid experiment name '${EXPERIMENT}'."
    print_usage
fi

# Determine which compose file to use
if [[ "${MODE}" == "ra" ]]; then
    if [[ -z "${ALGO}" ]]; then
        echo "Error: RA mode requires a learning algorithm name."
        print_usage
    fi
    COMPOSE_FILE="docker-compose-${EXPERIMENT}-ra.yaml"
else
    COMPOSE_FILE="docker-compose-${EXPERIMENT}.yaml"
fi

# Run the appropriate docker compose setup
if [[ -f "${DOCKER_COMPOSE_DIR}/${COMPOSE_FILE}" ]]; then
    pushd "${DOCKER_COMPOSE_DIR}" > /dev/null
    echo "Starting ${MODE:-mealy} learning experiment for ${EXPERIMENT}..."

    if [[ "${MODE}" == "ra" ]]; then
        # not running in background as not used in any ci so far
        # and I would want ot see the progress locally
        LEARNING_ALGORITHM=${ALGO} docker compose -f "${COMPOSE_FILE}" up --build
    else
        docker compose -f "${COMPOSE_FILE}" up --build -d
    fi

    popd > /dev/null
else
    echo "Error: ${COMPOSE_FILE} not found in ${DOCKER_COMPOSE_DIR}"
    exit 1
fi