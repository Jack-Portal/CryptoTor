import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

/**
 *
 */
public class Tracker extends Thread{
    private Node[] Nodes;
    private Map<String, Node> Files;


    public void Tracker(){
    }

    private void addNodes(){
        // adds new node to this.Nodes
    }

    private void addFiles(){
        // adds new node to this.Files
    }

    private void giveNextIP(){
        // when peer asks for a file location, returns IP and Port to access such file.
    }


    public static void run(String IP, int port) {
        connectToServer(IP, port);
    }

    public static void connectToServer(String IP, int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
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
}
