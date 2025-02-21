if [ -z "$( ls -A './ssh-keys' )" ]; then
   ssh-keygen -t rsa -f ${PWD}/ssh-keys/learner-ssh -N ""
fi

sleep1
docker compose -f docker-compose.yaml up --build