/**
 *
 */

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Node extends Thread{

    public int pbKey;
    public int port;
    public Tracker tracker;
    private int pvKey;
    private Map<Node, Integer> symKeys;
    private Map<String,Node> cookies;


    public void Node(int port, Tracker tracker){
        // Choose pbk and pvk
        // initialise empty sym key and cookie
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

    @Override
    public void run(){
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
