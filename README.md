# bid-parser
Read the objects from the attached file, consider the file contents is updated 1/min.

Create Queues for each bid type.

Instantiate a thread per object and based on message type queue it in the appropriate queue.

Log the bid id(id), timestamp(ts), queue “name”( ty), and payload (decoded from Base64 pl) when the queueing is completed.

Event-based reading from the stream
Spawn a thread for each queue message
Use logging library to log output to console


What's implemented?
Event reading from file, using java Watch API, Two threads, each is reading its own queue (AQ, ZU types). 
FileUpdateRunner is used for updating bids.json once a minute, so it will trigger event of MODIFY and will process file, filtering and sending bids to appropriate queues
