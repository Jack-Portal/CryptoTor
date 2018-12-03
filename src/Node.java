/**
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


//TODO change who selects sym key, make the peer generate them

public class Node{

    public int pbKey;
    public int port;
    public int tracker;
    public ServerSocket server;
    private int pvKey;
    private HashMap<String, Integer> symKeys;
    private HashMap<String, Integer> sender;
    private HashMap<String, String> forward;
    private HashMap<String, String> traceback;



    public Node(int port, int pbkey, int pvkey, int tracker){
        this.port = port;
        this.pbKey = pbkey;
        this.tracker = tracker;
        this.pvKey = pvkey;
        this.symKeys = new HashMap<>();
        this.sender = new HashMap<>();
        this.forward = new HashMap<>();
        this.traceback = new HashMap<>();
    }


    private static void connectToServer(Node node) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String initString = "NEWNODE "+ node.port +"/ " +node.pbKey;
        oos.writeObject(initString);
        oos.flush();
        System.out.println("Connected to Tracker / Ready!");
    }


    private static String newPeer(Node node, String nodePseudo, String files) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String newFileString = "NEWFILES "+ node.port +"/ "+ nodePseudo+"/ " + files;
        oos.writeObject(newFileString);
        oos.flush();
        System.out.println("New Files given to the server: " + files);
        return "Success";
    }


    private static String askTracker(Node node, String fileName) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);

        System.out.println("setting up output stream");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "GETFILE "+ fileName;
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


    private static String contactRDVNode(int RDVNode, int pbKey, String cookie, String fileName, int SymKey) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        Socket clientSocket = new Socket("127.0.0.1", RDVNode);

        System.out.println("Contacting RDV Node");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String encryptedMessage = CryptoAlgorithms.asymEncryption("comtype: GETFILE, cookie: "+cookie+", symKey: "+SymKey+
                ", filename: "+fileName, pbKey);
        String request = "pseudo: " + generateRandomString(20) + ", msg: " + encryptedMessage;
        oos.writeObject(request);
        oos.flush();
        System.out.println("sent " + request);

        System.out.println("waiting for reply");
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String response =  (String) ois.readObject();
        System.out.println(response);
        return response + ", symKey: " + SymKey;
    }


    private static String GetFileFromPeer(int nextNode, String fileName) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //decrypt it with prvt key
        // extract things out of message
        //find next node
        Socket clientSocket = new Socket("127.0.0.1", nextNode);

        System.out.println("Contacting Peer");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: " + "newRandomString" + ", msg: " + "ENCRYPTED: comtype: GETFILE, cookie: filename, symKey: "+"SYMKEY)";
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

    private static String forwardUp(String Message, int nextNode, String pseudo) throws IOException, ClassNotFoundException {
        // extract things out of message
        Socket clientSocket = new Socket("127.0.0.1", nextNode);

        System.out.println("forwarding");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: "+pseudo+", msg: " + Message;
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

    private static String forwardDown(String Message, int nextNode, String pseudo) throws IOException, ClassNotFoundException {
        // encrypt
        // extract things out of message
        Socket clientSocket = new Socket("127.0.0.1", nextNode);

        System.out.println("forwarding");
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String request = "pseudo: "+pseudo+", msg: decrypted message";
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


    public static String generateRandomString(int length) {
        String randomString = "";

        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890".toCharArray();
        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomString = randomString + chars[random.nextInt(chars.length)];
        }

        return randomString;
    }


    private static void handleConnection(Socket connectionSocket, Node node) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //Create Input&Outputstreams for the connection
        InputStream inputToServer = connectionSocket.getInputStream();
        OutputStream outputFromServer = connectionSocket.getOutputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String line = (String) ois.readObject();
        String[] toExtract = {"pseudo", "msg"};
        Map<String, String> extractedInfo = messages.getValuesFromMsg(toExtract,line);
        String pseudo = extractedInfo.get("pseudo");
        ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
        if (node.sender.containsKey(pseudo)){                                                                               //if sender is known
            // Symmetric encryption
            int key = node.symKeys.get(pseudo);
            String request = CryptoAlgorithms.symDecryption(extractedInfo.get("msg"), key);
            String[] requestType = {"comType"};
            Map<String, String> requestInfo = messages.getValuesFromMsg(requestType,request);
            String response;
            switch (requestInfo.get("comType")) {
                case "FORWARDUP":
                    System.out.println("FORWARING");
                    String[] forwardup = {"nextNode", "msg"};
                    Map<String, String> forwardUpInfo = messages.getValuesFromMsg(forwardup,request);
                    String upPseudo = node.forward.get(pseudo);                                                             // gives next pseudo
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
                    int newSymKey = 1;
                    response = contactRDVNode(Integer.parseInt(info[0]), Integer.parseInt(info[1]), "cookiie", "salut.txt", newSymKey);
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
            int SymKey = node.symKeys.get(PreviousNodePseudo);
            System.out.println("FORWARING");
            String encryptedMessage = CryptoAlgorithms.symEncryption(extractedInfo.get("msg"), SymKey);
            int portOfPreviousNode = node.sender.get(PreviousNodePseudo);                                                             // gives port of previous node in circuit
            String response = forwardDown(encryptedMessage, portOfPreviousNode, PreviousNodePseudo);
            oos.writeObject(response);
            oos.flush();
        }
        else{
            String request = CryptoAlgorithms.asymDecryption(extractedInfo.get("msg"), node.pvKey);
            String[] requestExtract = {"previousNode", "comType", "nextNode", "symKey", "msg"};
            Map<String, String> requestInfo = messages.getValuesFromMsg(requestExtract,request);
            String response;
            String thisNodePseudo;
            switch (requestInfo.get("comType")) {
                case "INIT":
                    System.out.println("PEER INIT");
                    node.sender.put(pseudo, Integer.parseInt(requestInfo.get("previousNode")));
                    node.symKeys.put(pseudo, Integer.parseInt(requestInfo.get("symKey")));
                    thisNodePseudo = generateRandomString(20);
                    node.traceback.put(thisNodePseudo, pseudo);
                    node.forward.put(pseudo, thisNodePseudo);
                    response = forwardUp(requestInfo.get("msg"), Integer.parseInt(requestInfo.get("nextNode")), thisNodePseudo);
                    oos.writeObject(response);
                    oos.flush();
                    break;
                case "ANNOUNCE":
                    System.out.println("RDV MEETING");
                    node.sender.put(pseudo, Integer.parseInt(requestInfo.get("previousNode")));
                    node.symKeys.put(pseudo, Integer.parseInt(requestInfo.get("symKey")));
                    thisNodePseudo = generateRandomString(20);
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
                    String previousNode = node.traceback.get(requestInfo.get("pseudotracker"));
                    int previousNodeport = node.sender.get(previousNode);
                    response = GetFileFromPeer(previousNodeport, requestInfo.get("msg"));
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

    public static void main(String args[]) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        System.out.println(messages.S(args));
        //choose pbk
        int nodePort = Integer.parseInt(args[0]);
        //Should be chosen randomly
        int pbk = 12;
        int pvk = 31;
        int tracker = Integer.parseInt(args[1]);
        Node node = new Node(nodePort, pbk, pvk, tracker);
        connectToServer(node);
        //newPeer(node, "cookie", "salut.txt, yoyoyo.video, heyheyhey.pem");
        //String a = askTracker(node, "salut.txt");
        //String[] info = a.split(" ");
        //String encryptedFile = contactRDVNode(Integer.parseInt(info[0]), Integer.parseInt(info[1]), "cookiie", "salut.txt");
        try(ServerSocket serverSocket = new ServerSocket(node.port)) {

            System.out.println("Tracker initialised." );
            while(true) {
                Socket connectionSocket = serverSocket.accept();
                CompletableFuture.runAsync(() -> {
                    try {
                        handleConnection(connectionSocket, node);
                    } catch (IOException | ClassNotFoundException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
