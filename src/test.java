/**
 *
 */
import java.util.Map;
import java.util.concurrent.CompletableFuture;



public class test extends CryptoAlgorithms {

    public int pbKey;
    public int port;
    public Tracker tracker;
    private int pvKey;
    private Map<Node, Integer> symKeys;
    private Map<String, Node> cookies;


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

    private static String decrypt(String sender, String Message){
        return "";
    }

    private static String performAction(String decryptedMessage){
        // get what to do from the message (probably massive switch)
        // functional programming ? do that action?
        return "";
    }

    private static void Reply(String message){

    }

    public static void main(String [] args){
        String message = "";
        String sender = "";
        CompletableFuture
                .supplyAsync(()-> decrypt(sender, message))
                .thenApply((decryptedMessage) -> performAction(decryptedMessage))
                .thenApply((response) -> {
                    Reply(response);
                    return "Completed!";
                });
    }

}
