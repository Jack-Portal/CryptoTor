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
    public ArrayList<Integer> Nodes;
    public HashMap<Integer, String> NodePBK;
    public HashMap<String, Integer> FilesRDV;
    public HashMap<String, String> FilesCookies;

    public void Tracker(){
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


    public static void main(String args[]) {
        Tracker tracker = new Tracker();
        tracker.Nodes = new ArrayList<>();
        tracker.NodePBK = new HashMap<>();
        tracker.FilesRDV = new HashMap<>();
        tracker.FilesCookies = new HashMap<>();
        connectToServer(9992, tracker);
    }

    public static void connectToServer(int port, Tracker tracker) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {

            Socket connectionSocket = serverSocket.accept();

            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            serverPrintOut.println("Tracker initialised." );

            while(true) {
                String line = scanner.nextLine();
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
