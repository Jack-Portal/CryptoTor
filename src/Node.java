/**
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Node{

    public int pbKey;
    public int port;
    public Tracker tracker;
    private int pvKey;
    private Map<Node, Integer> symKeys;
    private Map<String, Node> cookies;


//    public void Node(int port, int pbkey, int pvkey, Tracker tracker){
//        this.port = port;
//        this.pbKey = pbkey;
//        this.tracker = tracker;
//        this.pvKey = pvkey;
//    }

    public void Node(String port, String pbKey){
        this.port = Integer.parseInt(port);
        this.pbKey = Integer.parseInt(pbKey);
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


    private void transfer(String encryptedMessage){
        // get symmetric key
        // decrypt message
        // if not last node:
            // find next node
        // transfer the message
    }

    private void performAction(String decryptedMessage){
        // get what to do from the message (probably massive switch)
        // functional programming ? do that action?
    }

    public static void connectToServer(int Port) {
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        try(ServerSocket serverSocket = new ServerSocket(Port)) {
            Socket connectionSocket = serverSocket.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            serverPrintOut.println("Hello World! Enter Peace to exit.");

            //Have the server take input from the client and echo it back
            //This should be placed in a loop that listens for a terminator text e.g. bye
            boolean done = false;

            while(!done && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                serverPrintOut.println("Echo from <Your Name Here> Server: " + line);

                if(line.toLowerCase().trim().equals("peace")) {
                    done = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]){
        connectToServer(9991);
        // create a server / socket
        // connect to the Tracker and announce itself
        while(true){
            // wait for request
            // completableFuture.supplyAsync(()->....
                // decrypt
                // performAction
        }
    }
}
