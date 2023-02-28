**SSH-Fuzzer** is a protocol state fuzzer for SSH, derived from the [artifact][learning-ssh-artifact] of the publication "Model Learning and Model Checking of SSH Implementations".

This project consists of several components:
* the learner (ssh-learner)
* the mapper (ssh-mapper)
* results (ssh-learner/results)
* .dot trimming script (mypydot)

# Learner
The learner is implemented by a java Eclipse project. 

To deploy, make sure you include the two provided jar files (sqlite and learnlib) in the build path (libraries tab under project properties).

The learner connects to the (python-based) mapper on localhost:8000. Make sure the mapper is running before running the learner. More on the mapper below. The input alphabet is defined in Mapper.java (generateInputAlphabet). All symbols are enabled by default, but some need to be commented out for partial learning (for example, just one layer of SSH). WordProcessor.java defines the link to the trace&response log and the non-determinism check. 

# Mapper
The mapper part is more complex. The reason for this is that is was built on top of an existing SSH implementation (Paramiko). This did ultimately involve a lost of nasty hacks to make things work. 

The core of the mapper is defined in mapper/mapper.py. This is also the command you have to run to fire up the mapper (user@desktop:mapper$ python mapper.py). You might run in some missing packages (depending on your configuration), which are easily resolved using Google.

Please read to mapper.py file to get a grip of the global interaction process with the learner. For debugging purposes, it's also possible to simply connect to the mapper using telnet (telnet localhost 8000 for a connection, and then a sequence like "reset KEXINIT KEX30 NEWKEYS"). 

The mapper/messages.py file provides a mapping to actual functions being executed as soon as a query has been received. It also provides the mapping back to textual representations.

The hacked version of paramiko can be found in manualparamiko directory. This file has been edited at countless places (use diff against the normal transport.py file in paramiko directory to find all changes). Essentially, the state machine has been removed, and the functions as noted in messages.py are executed on message arrival (for example, fuzz_disconnect on a SSH_MSG_DISCONNECT). At the end of each fuzz_ function, the read_multiple_responses is used to query and correctly handle responses. The read_response() function (used by read_multiple_responses) defines a handlers dictionary. This defines the state-changing responses and their effects.

Timing variances were a nuisance. Timing is controlled at various places:
- The time.sleep() in mapper/mapper.py (controls set timeout between runs)
- read_multiple_responses() in manualparamiko/transport.py (has both a timeout between responses and total timeout -in case of multiple responses- argument). 
- Timeout based on the type of message can be set in the individual read_multiple_responses-calls. 


 --- RESULTS UTILITY ---
Results include the .dot models obtained by learning plus various 

 --- DOTTOSMV UTILITY ---
The dotToNuSMV utility was used to convert the .dot Mealy machines obtained by learning, to NuSMV (.smv) models that are suitable for model checking. 

# Trimming script (mypydot)
The python pydot package was altered to merge edges between the same nodes. 
Feed it a .dot file and let it do its magic. 
It needs some python packages to work with. 
A Google search will quickly determine which packages are missing.

[learning-ssh-artifact]:https://repository.ubn.ru.nl/handle/2066/184275
