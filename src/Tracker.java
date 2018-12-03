/**
 * Jacques-Antoine Portal
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * This is the only class that actually behaves as expected and has been fully tested. :/
 *
 */
public class Tracker{
    public int port;
    public ArrayList<Integer> Nodes;
    public HashMap<Integer, Integer> NodePBK;
    public HashMap<String, Integer> FilesRDV;
    public HashMap<String, String> FilesPseudo;

    /**
     * initialises the tracker
     * @param port
     */
    public Tracker(int port){
        this.port = port;
        this.Nodes = new ArrayList<>();
        this.NodePBK = new HashMap<>();
        this.FilesRDV = new HashMap<>();
        this.FilesPseudo = new HashMap<>();
    }

    /**
     * when a new node is initialised and contacts the tracker, the tracker calls this function to
     * add it (and its public key) to its memory.
     * @param line
     */
    private void addNodes(String line){
        String[] lineSplit = line.split("/ ");
        int nodePort = Integer.parseInt(lineSplit[0]);
        int nodePBK = Integer.parseInt(lineSplit[1]);
        this.Nodes.add(nodePort);
        this.NodePBK.put(nodePort, nodePBK);
        System.out.println("NEW NODE ADDED: " + lineSplit[0]+ ' ' +  nodePBK);
    }

    /**
     * Adds all the information needed to download a file by contacting it's RDV node.
     * @param line
     */
    private void addFiles(String line){
        String[] lineSplit = line.split("/ ");
        if (lineSplit.length > 2 ) {
            int nodeRDV = Integer.parseInt(lineSplit[0]);
            String fileCookie = lineSplit[1];
            String[] fileList = lineSplit[2].split(", ");
            for (String file : fileList) {
                this.FilesPseudo.put(file, fileCookie);
                this.FilesRDV.put(file, nodeRDV);
            }
            System.out.println("NEW FILES ADDED: NodeRDV: " + lineSplit[0] + ", NodeCookie: "
                    + lineSplit[1] + ", FileList: " + lineSplit[2]);
        }
    }

    /**
     * returns all the information that the tracker has to download a file.
     * @param line
     * @return
     */
    private String getFile(String line){
        int NodePort = this.FilesRDV.get(line);
        int nodePBK = this.NodePBK.get(NodePort);
        String fileCookie = this.FilesPseudo.get(line);
        return NodePort+", "+nodePBK+", "+fileCookie;
    }


    /**
     * The model for all the other classes, this function deals with all the possible cases of requests the tracker has.
     * it is launched asynch of the main thread to let the tracker do multiple things at once.
     * cases:
     *      NEWNODE : calls new node
     *      NEWFILE: calls addfiles
     *      GETFILE: calls getFile and provides all this information to the node initialising the request.
     *      NEWPEER: provide the peer with a list of nodes and pbkeys to set up its TOR route.
     * @param connectionSocket
     * @param tracker
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void handleConnection(Socket connectionSocket, Tracker tracker) throws IOException, ClassNotFoundException {
        //Create Input&Outputstreams for the connection
        InputStream inputToServer = connectionSocket.getInputStream();
        OutputStream outputFromServer = connectionSocket.getOutputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        String line = (String) ois.readObject();
        // creating node
        if (line.startsWith("NEWNODE ")) {
            tracker.addNodes(line.substring("NEWNODE ".length(), line.length()));
        }
        else if (line.startsWith("NEWFILES")) {
            tracker.addFiles(line.substring("NEWFILES ".length(), line.length()));
            ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
            oos.writeObject(tracker.FilesRDV.keySet());
            oos.flush();
            System.out.println("gave the list of files available");
        }
        else if (line.startsWith("GETFILE")) {
            System.out.println("Request Received: " + line);
            String toSend = tracker.getFile(line.substring("GETFILE ".length(), line.length()));
            ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
            oos.writeObject(toSend);
            oos.flush();
            System.out.println("sent");
        }
        else if(line.startsWith("NEWPEER")) {
            System.out.println("NewPeer: ");
            ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
            oos.writeObject(tracker.Nodes);
            oos.writeObject(tracker.NodePBK);
            oos.flush();
            System.out.println("sent");
        }
    }


    /**
     * just for good practice, takes an int and uses it as a port number
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String args[]) throws ClassNotFoundException, IOException {
        int port = Integer.parseInt(args[0]);
        Tracker tracker = new Tracker(port);
        connectToServer(tracker);
    }

    /**
     * Deals with all the connections and calls handleConnection after accepting them.
     * @param tracker
     * @throws IOException
     */
    public static void connectToServer(Tracker tracker) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(tracker.port)) {

            System.out.println("Tracker initialised." );
            while(true) {
                Socket connectionSocket = serverSocket.accept();
                CompletableFuture.runAsync(() -> {
                    try {
                        handleConnection(connectionSocket, tracker);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
