import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Map;


/**
 *
 */

//public interface Nodes{
/**
    public int pbKey;
    public int port;
    public Tracker tracker;
    private int pvKey;
    private Map<Nodes, Integer> symKeys;
    private Map<String, Nodes> cookies;


    // initialisation

    public void Node(String someStuff){
        // initialise Nodes here
    }

    private void tellNewNodeInitialisedTracker(int Tracker){
        //warn tracker you exist
    }


    //last node case

    private void getFile(String message){
        // ask tracker
        // contact RDV Nodes / set up contact
        // get the file
        // send down
    }

    private void askForFileTracker(String message){
        // get file name from message
        // ask tracker
    }

    private void contactRDVNode(String message){
        // choose pseudo
        // choose sym key
        // craft message for RDV Nodes
    }


    //server stuff

    private void sendMessage(String message){
        // find node to send the message to
        // sevrer stuff
        // thread stuff
    }

    //typical forwarding

    private void sendUp(String message){
        // decrypt message
        // send
    }

    private void sendDown(String message) {
        // encrypt message
        // send
    }

    private void dealWithMessage(String message){
        // decrypt
        //switch
            // call to other funtions

    }

    public static void connectToServer(int Port) {
        //Try connect to the server on an unused port eg 9991. A successful connection will return a socket
        try(ServerSocket serverSocket = new ServerSocket(Port)) {
            Socket connectionSocket = serverSocket.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");

            serverPrintOut.println("Hello World! Enter Peace to exit.");

            //Have the server take input from the client and echo it back
            //This should be placed in a loop that listens for a terminator text e.g. bye
            boolean done = false;

            while(!done && scanner.hasNextLine()) {
                Socket server = listenSocket.accept();

                dealWithMessage(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(){
        //start node
        //connect
        //make server run
        
    }
**/
//}
