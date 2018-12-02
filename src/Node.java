/**
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//TODO change who selects sym key, make the peer generate them

public class Node{

    public int pbKey;
    public int port;
    public int tracker;
    private int pvKey;
    private Map<Node, Integer> symKeys;
    private Map<String, Node> cookies;


    public Node(int port, int pbkey, int pvkey, int tracker){
        this.port = port;
        this.pbKey = pbkey;
        this.tracker = tracker;
        this.pvKey = pvkey;
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


    public static ServerSocket connectToServer(Node node) throws IOException {
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        ServerSocket serverSocket = new ServerSocket(node.port);
        Socket clientSocket = new Socket("127.0.0.1", node.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String initString = "NEWNODE "+ node.port +"/ " +node.pbKey;
        oos.writeObject(initString);
        oos.flush();
        System.out.println("Connected to Tracker / Ready!");
        return serverSocket;
    }


    public static void main(String args[]) throws IOException {
        System.out.println(messages.S(args));
        //choose pbk
        int nodePort = Integer.parseInt(args[0]);
        int pbk = 12;
        int pvk = 31;
        int tracker = Integer.parseInt(args[1]);
        Node node = new Node(nodePort, pbk, pvk, tracker);
        ServerSocket serverSocket = connectToServer(node);
        while(true) {
            Socket connectionSocket = serverSocket.accept();

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
