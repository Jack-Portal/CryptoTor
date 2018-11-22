To build a network, First establish a tracker:

Tracker.java:
	----
	public port (int)
	public availableNodes (Node[])
	public availableFiles (map file -> Node -> cookie)
	----
	public void Tracker(int port)
	private void addNode(Node node)
	private void addFile(String fileName, Node rdvNode, String cookie)
	private void giveRDVNode(String fileName)
	private void updateNode(Node newNode)
	
Then add as many Nodes as needed. You need a number of nodes that is bigger than
the maximum number of Nodes used in a TOR circuit. 

Node.java:
	----
	public pbKey (int?)
	public port (int)
	public tracker (Tracker)
	private pvKey (int?)
	private symKeys (map Node -> int?)
	private cookies (map String -> Node)
	----
	public void Node(int port, Tracker tracker)
	private void newConnection(String newConnectionMessage)
	private void transfer(String encryptedMessage)
	
Then you can create Peers:

Peer.java:
	----
	public pbKey (int?)
	public tracker (Tracker)
	public port (int)
	private pvKey (int?)
	private fileList (String[])
	private torCircuit (Node[])
	private randomCookie (String)
	----
	public void Peer(int port, String dir, Tracker tracker)
	private void getFile(string fileName)
	private void updateAvailableFiles()
	private void changeTORConnection()
	private void updateMyFiles()
	
	
To function, use the following file containing encryption functions: AES and RSA

CryptoAlgorithms.java:
	----
	*no attributes*
	----
	public String symEncryption(String message, int? key)
	public String symDecryption(String message, int? key)
	public String asymEncryption(String message, int? pbKey)
	public String asymDecryption(String message, int? pvKey)
	
	
If you wish to have all of this done automatically, run the main program: main.java
	
	
	
	
	
	
	
	
	