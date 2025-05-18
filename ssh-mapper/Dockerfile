FROM python:3.9

WORKDIR /app

COPY mapper/mapper.py .
COPY mapper/messages.py .
COPY manualparamiko ./manualparamiko/
COPY paramiko ./paramiko/
COPY dev-requirements.txt ./
COPY setup.py ./
COPY README.rst ./

RUN pip3 install -r dev-requirements.txt


ENTRYPOINT ["python3", "-u", "mapper.py"]
