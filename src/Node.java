/**
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

//TODO change who selects sym key, make the peer generate them

public class Node{

    public int pbKey;
    public int port;
    public int tracker;
    public ServerSocket server;
    private int pvKey;
    private Map<Node, Integer> symKeys;
    private Map<String, Node> cookies;


    public Node(int port, int pbkey, int pvkey, int tracker){
        this.port = port;
        this.pbKey = pbkey;
        this.tracker = tracker;
        this.pvKey = pvkey;
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newConnection(String newConnectionMessage){
        // decrypt with private key
        // transfer
        // if not last node:
            // wait for answer
        // generate symmetric key
        // make message
        // encrypt message with previous node's public key
        // send response
    }


    private void forward(String encryptedMessage){
        // get symmetric key
        // decrypt message
        // if not last node:
            // find next node
        // transfer the message
    }

    private static String decrypt(String msg){
        // messages.extract pseudo
        //if pseudo known
        //  sym
        //else
        //  asym
        return"";
    }

    private static String performAction(String decryptedMessage){
        //message.extract everything
        //switch on comType
            //newPeer
                //init sym key
                //save pseudo
                //create new pseudo
                //save new pseudo -> pseudo map
                //if next node == tracker
                    //tell tracker files
                //else
                    //send new message to next node
                    //return response
            //Forward
                //craft message
                //send
                //return response
            //GETFILE
                //ask Tracker
                //start com with RDV node
                //return received file
            //SENDFILE
                //find last node
                //encrypt / forward
                //return Received file
        return "";
    }

    private static String reply(String messageReceived){
        //encryprt message with stored key
        //reply to connection set up allready
        //or send with new connection
        return "";
    }


    public static void connectToServer(Node node) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String initString = "NEWNODE "+ node.port +"/ " +node.pbKey;
        oos.writeObject(initString);
        oos.flush();
        System.out.println("Connected to Tracker / Ready!");
    }


    public static void newPeer(Node node, String fileCookies, String files) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String newFileString = "NEWFILES "+ node.port +"/ "+ fileCookies+"/ " + files;
        oos.writeObject(newFileString);
        oos.flush();
        System.out.println("New Files given to the server: " + files);
    }


    public static String askTracker(Node node, String fileName) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
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


    public static String contactRDVNode(int RDVNode, String message) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        //encrypt tit with pbkey
        // extract things out of message
        Socket clientSocket = new Socket("127.0.0.1", RDVNode);

        System.out.println("Contacting RDV Node");
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
        return response; //pbbly add symkey
    }


    public static String GetFileFromPeer(int nextNode, String message) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
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

    public static void forwardUp(String Message, int nextNode, String pseudo, int key) throws IOException, ClassNotFoundException {
        //decrypt
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
    }

    public static void forwardDown(String Message, int nextNode, String pseudo, int key) throws IOException, ClassNotFoundException {
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
    }


    public static void main(String args[]) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        System.out.println(messages.S(args));
        //choose pbk
        int nodePort = Integer.parseInt(args[0]);
        int pbk = 12;
        int pvk = 31;
        int tracker = Integer.parseInt(args[1]);
        Node node = new Node(nodePort, pbk, pvk, tracker);
        connectToServer(node);
        Node node2 = new Node(4441, pbk, pvk, tracker);
        connectToServer(node2);
        newPeer(node, "cookie", "");
        newPeer(node2, "cookiie", "salut.txt, yoyoyo.video, heyheyhey.pem");
        String a = askTracker(node, "salut.txt");

        while(true) {
            Socket connectionSocket = node.server.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(inputToServer);
            CompletableFuture
                    .supplyAsync(() -> {
                        String msg = "";
                        try {
                            msg += (String) ois.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        return msg;
                    })
                    .thenApply((msg)->decrypt(msg))
                    .thenApply((msg)->performAction(msg))
                    .thenApply((msg)->reply(msg));
        }
    }
}
