/**
 *Jacques-Antoine Portal
 */

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;


//TODO change who selects sym key, make the peer generate them


public class Node{

    public BigInteger pbKey;
    public int port;
    public int tracker;
    private BigInteger pvKey;
    private BigInteger m;
    private HashMap<String, String> symKeys;
    private HashMap<String, Integer> sender;
    private HashMap<String, String> forward;
    private HashMap<String, String> traceback;


    /**
     * This initialises the Node
     */
    public Node(int port, BigInteger pbkey, BigInteger pvkey, int tracker, BigInteger m){
        this.port = port;
        this.pbKey = pbkey;
        this.tracker = tracker;
        this.pvKey = pvkey;
        this.symKeys = new HashMap<>();
        this.sender = new HashMap<>();
        this.forward = new HashMap<>();
        this.traceback = new HashMap<>();
        this.m = m;
    }

    /**
     * This function contacts the Tracker to add itself in the list of available nodes
     */
    private static void connectToServer(Node node) throws IOException {
        //connecting to the Tracker
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        //sending a string / command that the tracker can understand.
        String initString = "NEWNODE "+ node.port +"/ " +node.pbKey;
        oos.writeObject(initString);
        oos.flush();
        System.out.println("Connected to Tracker / Ready!");
    }

    /**
     * This function is called when a new peer chooses the node as its exit node.
     * it contacts the tracker to add the files to the list of available files
     */
    private static String newPeer(Node node, String nodePseudo, String files) throws IOException {
        //connecting to the Tracker
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        //sending a string / command that the tracker can understand.
        String newFileString = "NEWFILES "+ node.port +"/ "+ nodePseudo+"/ " + files;
        oos.writeObject(newFileString);
        oos.flush();
        System.out.println("New Files given to the server: " + files);
        return "Success";
    }

    /**
     * This function contacts the Tracker to ask which Node to contact to obtain a file (after a Peer requested this node to do so
     */
    private static String askTracker(Node node, String fileName) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //contacting the Tracker
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "GETFILE "+ fileName;
        oos.writeObject(request);
        oos.flush();
        // listening for the response of the Tracker
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        return response;
    }

    /**
     * This function is used after asking the tracker who to contact to get a file.
     * It sets up a connection with the exit node / RDV node of the other peer
     * who owns the file.
     */
    private static String contactRDVNode(int RDVNode, int pbKey, String cookie, String fileName, String SymKey) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //Setting up a connection with the RDV Node
        Socket clientSocket = new Socket("127.0.0.1", RDVNode);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        // Crafting a message that it will understand
        String encryptedMessage = CryptoAlgorithms.asymEncryption("comType: GETFILE, cookie: "+cookie+", symKey: "+SymKey+
                ", filename: "+fileName, pbKey);
        String request = "pseudo: " + messages.generateRandomString(20) + ", msg: " + encryptedMessage;
        oos.writeObject(request);
        oos.flush();

        //waiting for the response which is the encrypted file
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        // does not decrypt the file, the Peer will do so with the symmetric key provided after the file
        return response + ", symKey: " + SymKey;
    }

    /**
     * This function is called once a RDV node receives a request for a file, it will craft a message for the peer who owns the
     * file and send it in the right down the circuit of nodes to get the file back.
     */
    private static String GetFileFromPeer(int nextNode, String fileName) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //Setting up a connection with the node before the RDV node to reach a Peer
        Socket clientSocket = new Socket("127.0.0.1", nextNode);
        System.out.println("Contacting Peer");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: " + "newRandomString" + ", msg: " + "ENCRYPTED: comtype: GETFILE, cookie: filename, symKey: "+"SYMKEY)";
        oos.writeObject(request);
        oos.flush();

        // waiting for the reply
        System.out.println("waiting for reply");
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        return response;
    }

    /**
     * This function forwards a message up.
     * In other words, it receives a message from another node (or the Peer) and decrypts it once.
     * Then it will craft a message to forward the rest of the encrypted message to the next node in the circuit.
     * Note: The decryption is done outside the function.
     */
    private static String forwardUp(String Message, int nextNode, String pseudo) throws IOException, ClassNotFoundException {
        System.out.println(Message);
        Socket clientSocket = new Socket("127.0.0.1", nextNode);

        System.out.println("forwardingup");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: "+pseudo+", msg: " + Message;
        oos.writeObject(request);
        oos.flush();

        //waiting for reply
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        return response;
    }

    /**
     * This function is called when contacting a peer, it does the oposit than forward up
     * since it "encrypts" the message and then sends it to a node "closer" to the peer.
     * The node will then encrypt it as well, until the request reaches the peer, who is the only one in
     * possession of all the symmetric keys needed to decrypt the message.
     * NOTE: again the encryption is done outside of the function.
     */
    private static String forwardDown(String Message, int nextNode, String pseudo) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", nextNode);
        System.out.println("forwardingdown");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: "+pseudo+", msg: "+Message;
        oos.writeObject(request);
        oos.flush();
        System.out.println("sent " + request);

        System.out.println("waiting for reply");
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        return response;
    }


    /**
     * This is most likely where everything breaks down.
     * It is a big switch that should have handled all possible situations differently.
     *  Different cases:
     *      - The pseudo is known:
     *          The node has received a connection from this sender before and has all the information needed to either:
     *              --Forward (up) the connection torwards the RDV node
     *              --Ask the tracker / other nodes to get a file.
     *      - This node has already sent a message to the node contacting it:
     *          Someone is trying to reach a Peer, the node has all the necesssary information to process the
     *          request and forwards (down) the request.
     *      - The pseudo is not known, meaning it is a connection from someone never seen before
     *          The node needs to save all sorts of information (including a symetric key)
     *          to make sure it can handle the connection faster next time.
     *              -- A peer is initialising a connection to the TOR network and sending symmetric keys to
     *                  all the nodes it is going to use, until the exit node
     *              -- A peer is contacting this node and uses it as an exit node.
     *                  The node has to contact the Tracker to announce it is the RDV node (node to contact)
     *                  if another peer wants to get one of the files it is in charge of.
     *              -- Another node is contacting this node (RDV node for a peer) to get a file.
     */
    private static void handleConnection(Socket connectionSocket, Node node) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //Create Input&Outputstreams for the connection
        InputStream inputToServer = connectionSocket.getInputStream();
        OutputStream outputFromServer = connectionSocket.getOutputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String line = (String) ois.readObject();
        String[] toExtract = {"pseudo", "msg"};
        Map<String, String> extractedInfo = messages.getValuesFromMsg(toExtract,line);
        String pseudo = extractedInfo.get("pseudo");
        System.out.println(pseudo);
        ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
        if (node.sender.containsKey(pseudo)){                                                                               //if sender is known
            // Symmetric encryption
            String key = node.symKeys.get(pseudo);
            //TODO encryption AES
            String request = extractedInfo.get("msg");
            String[] requestType = {"comType"};
            Map<String, String> requestInfo = messages.getValuesFromMsg(requestType,request);
            String response;
            switch (requestInfo.get("comType")) {
                case "FORWARDUP":
                    System.out.println("FORWARING");
                    String[] forwardup = {"nextNode", "msg"};
                    Map<String, String> forwardUpInfo = messages.getValuesFromMsg(forwardup,request);
                    String upPseudo = node.forward.get(pseudo);                                                             // gives next pseudo
                    sleep(100);
                    response = forwardUp(forwardUpInfo.get("msg"), Integer.parseInt(forwardUpInfo.get("nextNode")), upPseudo);
                    oos.writeObject(response);
                    oos.flush();
                    break;
                case "DOWNLOAD":
                    System.out.println("DOWNLOADING");
                    //contact tracker
                    String whoToContact = askTracker(node, "salut.txt");
                    //contact node
                    String[] info = whoToContact.split(" ");
                    //TODO generate sym key
                    String SymKey = messages.generateRandomString(8);                                                        //can be used as a 128 bit key for symmetric
                    response = contactRDVNode(Integer.parseInt(info[0]), Integer.parseInt(info[1]), "cookiie", "salut.txt", SymKey); //encryption with AES
                    oos.writeObject(response);
                    oos.flush();
                    break;
                default:
                    System.out.println("Unintelligible query:");
                    System.out.println(request);
            }

        }
        else if (node.traceback.containsKey(pseudo)){                                                                   //if someone contacting peer
            String PreviousNodePseudo = node.traceback.get(pseudo);
            String SymKey = node.symKeys.get(PreviousNodePseudo);
            System.out.println("FORWARING");
            //TODO ENCRYPTION AES
            String encryptedMessage = extractedInfo.get("msg");
            int portOfPreviousNode = node.sender.get(PreviousNodePseudo);                                                             // gives port of previous node in circuit
            String response = forwardDown(encryptedMessage, portOfPreviousNode, PreviousNodePseudo);
            oos.writeObject(response);
            oos.flush();
        }
        else{                                                                                                               //if someone new is contacting Node
            //TODO add encryption RSA
            String request = extractedInfo.get("msg");
            //TODO Change where previous node is
            String[] previousNodeExtractor =  {"previousNode"};
            int previousNode = Integer.parseInt(messages.getValuesFromMsg(previousNodeExtractor ,line).get("previousNode"));
            String[] requestExtract = {"comType", "nextNode", "symKey", "msg"};
            Map<String, String> requestInfo = messages.getValuesFromMsg(requestExtract,request);
            String response;
            String thisNodePseudo;
            switch (requestInfo.get("comType")) {
                case "INIT":
                    System.out.println("PEER INIT");
                    node.sender.put(pseudo, previousNode);
                    System.out.println(node.sender.get(pseudo));
                    node.symKeys.put(pseudo, requestInfo.get("symKey"));
                    thisNodePseudo = messages.generateRandomString(20);
                    node.traceback.put(thisNodePseudo, pseudo);
                    node.forward.put(pseudo, thisNodePseudo);
                    response = forwardUp(requestInfo.get("msg"), Integer.parseInt(requestInfo.get("nextNode")), thisNodePseudo);
                    oos.writeObject(response);
                    oos.flush();
                    break;
                case "ANNOUNCE":
                    System.out.println("RDV MEETING");
                    node.sender.put(pseudo, Integer.parseInt(requestInfo.get("previousNode")));
                    node.symKeys.put(pseudo, requestInfo.get("symKey"));
                    thisNodePseudo = messages.generateRandomString(20);
                    node.traceback.put(thisNodePseudo, pseudo);
                    node.forward.put(pseudo, thisNodePseudo);
                    response = newPeer(node, thisNodePseudo, requestInfo.get("msg"));
                    oos.writeObject(response);
                    oos.flush();
                    break;
                case "GETFILE":
                    System.out.println("RDV MEETING");
                    //craft message for peer
                    String [] requestDownload = {"pseudotracker"};
                    requestInfo = messages.getValuesFromMsg(requestDownload,request);
                    String previousNodet = node.traceback.get(requestInfo.get("pseudotracker"));
                    int previousNodePort = node.sender.get(previousNodet);
                    response = GetFileFromPeer(previousNodePort, requestInfo.get("msg"));
                    oos.writeObject(response);
                    oos.flush();
                    // forward down
                    break;
                default:
                    System.out.println("Unintelligible query:");
                    System.out.println(request);
            }
        }
    }

    /**
     * This function sets up all the variables to make a Node.
     * args[] should contain the port number of this node and the port number of the tracker in charge of the network.
     */
    public static void main(String args[]) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        System.out.println(messages.S(args));
        //choose pbk
        int nodePort = Integer.parseInt(args[0]);
        //Should be chosen randomly
        BigInteger[] keyPair = RSA.generateKeyPair();
        BigInteger pbk = keyPair[0];
        BigInteger pvk = keyPair[1];
        BigInteger m = keyPair[2];
        int tracker = Integer.parseInt(args[1]);
        Node node = new Node(nodePort, pbk, pvk, tracker, m);
        connectToServer(node);
        try(ServerSocket serverSocket = new ServerSocket(node.port)) {
            System.out.println("Tracker initialised." );
            while(true) {
                Socket connectionSocket = serverSocket.accept();                        //waiting for an incoming request
                CompletableFuture.runAsync(() -> {                                      //dealing with the request without blocking the node
                    try {
                        handleConnection(connectionSocket, node);
                    } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
