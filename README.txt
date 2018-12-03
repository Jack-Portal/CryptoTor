To build a network, First initialise a tracker:

compile the tracker:
javac Trackerjava

then run the Tracker program: 
	example: java Tracker 5555
	
Then create some Nodes: 
javac Node.java 
java Node 4440 5555
java Node 444X 5555 ...

We recommend at least 6 nodes, and they should all be launched from different command prompt,
You should see that the tracker is adding them to it's attributes as they are created
You should use the same port number for the tracker as before

Then create Peers:
javac Peer.java 
java Peer 1110 5555 3
java Peer 1111 5555 3

Peers should be launched from a folder with the right files for the downloading to work... 	
	
https://github.com/Jack-Portal/CryptoTor
