import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class Tracker{
    public int port;
    public ArrayList<Integer> Nodes;
    public HashMap<Integer, String> NodePBK;
    public HashMap<String, Integer> FilesRDV;
    public HashMap<String, String> FilesCookies;

    public Tracker(int port){
        this.port = port;
        this.Nodes = new ArrayList<>();
        this.NodePBK = new HashMap<>();
        this.FilesRDV = new HashMap<>();
        this.FilesCookies = new HashMap<>();
    }

    private void addNodes(String line){
        String[] lineSplit = line.split("/ ");
        int nodePort = Integer.parseInt(lineSplit[0]);
        String nodePBK = lineSplit[1];
        this.Nodes.add(nodePort);
        this.NodePBK.put(nodePort, nodePBK);
        System.out.println("NEW NODE ADDED: " + lineSplit[0]+ ' ' +  nodePBK);
    }

    private void addFiles(String line){
        String[] lineSplit = line.split("/ ");
        if (lineSplit.length > 2 ) {
            int nodeRDV = Integer.parseInt(lineSplit[0]);
            String fileCookie = lineSplit[1];
            String[] fileList = lineSplit[2].split(", ");
            for (String file : fileList) {
                this.FilesCookies.put(file, fileCookie);
                this.FilesRDV.put(file, nodeRDV);
            }
            System.out.println("NEW FILES ADDED: NodeRDV: " + lineSplit[0] + ", NodeCookie: "
                    + lineSplit[1] + ", FileList: " + lineSplit[2]);
        }
    }

    private String getFile(String line){
        int NodePort = this.FilesRDV.get(line);
        String nodePBK = this.NodePBK.get(NodePort);
        String fileCookie = this.FilesCookies.get(line);
        return NodePort+", "+nodePBK+", "+fileCookie;
    }


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
            //TODO get that back in Nodes and send it to peer
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


    public static void main(String args[]) throws ClassNotFoundException, IOException {
        int port = Integer.parseInt(args[0]);
        Tracker tracker = new Tracker(port);
        connectToServer(tracker);
    }

    public static void connectToServer(Tracker tracker) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(tracker.port)) {

            System.out.println("Tracker initialised." );
            while(true) {
                Socket connectionSocket = serverSocket.accept();
                CompletableFuture.runAsync(() -> {
                    try {
                        handleConnection(connectionSocket, tracker);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
