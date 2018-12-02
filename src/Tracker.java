import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

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
        int nodeRDV = Integer.parseInt(lineSplit[0]);
        String fileCookie = lineSplit[1];
        String[] fileList = lineSplit[2].split(", ");
        for (String file : fileList){
            this.FilesCookies.put(file, fileCookie);
            this.FilesRDV.put(file, nodeRDV);
        }
        System.out.println("NEW FILES ADDED: NodeRDV: " + lineSplit[0]+ " NodeCookie: "
                +  lineSplit[1] + " FileList: " + lineSplit[2]);
    }

    private void getFile(String line, Tracker tracker, PrintWriter serverPrintOut){
        int NodePort = tracker.FilesRDV.get(line);
        String nodePBK = tracker.NodePBK.get(NodePort);
        String fileCookie = tracker.FilesCookies.get(line);
        serverPrintOut.println(NodePort+", "+nodePBK+", "+fileCookie);
    }


    public static void main(String args[]) throws ClassNotFoundException {
        int port = Integer.parseInt(args[0]);
        Tracker tracker = new Tracker(port);
        connectToServer(tracker);
    }

    public static void connectToServer(Tracker tracker) throws ClassNotFoundException {
        try(ServerSocket serverSocket = new ServerSocket(tracker.port)) {

            while(true) {
                Socket connectionSocket = serverSocket.accept();
                //Create Input&Outputstreams for the connection
                InputStream inputToServer = connectionSocket.getInputStream();
                OutputStream outputFromServer = connectionSocket.getOutputStream();
                ObjectInputStream ois = new ObjectInputStream(inputToServer);
                PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
                serverPrintOut.println("Tracker initialised." );
                String line = (String) ois.readObject();
                // creating node
                if (line.startsWith("NEWNODE ")) {
                    tracker.addNodes(line.substring("NEWNODE ".length(), line.length()));
                }
                if (line.startsWith("NEWFILES")) {
                    tracker.addFiles(line.substring("NEWFILES ".length(), line.length()));
                }
                if (line.startsWith("GETFILE")) {
                    tracker.getFile(line.substring("GETFILE ".length(), line.length()), tracker, serverPrintOut);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
