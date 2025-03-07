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

# Check user input
if [[ "$#" -ne 1 ]]; then
    echo "Usage: $0 <openssh|dropbear>"
    exit 1
fi

EXPERIMENT=$1
COMPOSE_FILE="docker-compose-${EXPERIMENT}.yaml"

# Validate input and start corresponding docker-compose
if [[ "${EXPERIMENT}" == "openssh" || "${EXPERIMENT}" == "dropbear" ]]; then
    if [[ -f "${DOCKER_COMPOSE_DIR}/${COMPOSE_FILE}" ]]; then
        pushd "${DOCKER_COMPOSE_DIR}"
        echo "Starting experiment for ${EXPERIMENT}..."
        docker compose -f "${DOCKER_COMPOSE_DIR}/${COMPOSE_FILE}" up --build -d
    else
        echo "Error: ${COMPOSE_FILE} not found in ${DOCKER_COMPOSE_DIR}"
        exit 1
    fi
else
    echo "Invalid argument. Use 'openssh' or 'dropbear'."
    exit 1
fi
