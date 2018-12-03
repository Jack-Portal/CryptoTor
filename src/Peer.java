import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.nio.file.Files;

/**
 *  This class is defining a peer. As the rest of the program, it only considers ports, but the program could be adapted to include IPs
 */
public class Peer {
    public final String[] availableFiles = {"OKfile.txt", "ProbablyIllegalFile.txt"}; //Program will crash if these files are not in the folder where the command prompt is launched
    public ArrayList<Integer> availableNodes;
    public String[] symKeys;                            //for AES
    public HashMap<Integer, Integer> pkbs;              //For RSA
    private int[] Nodes;                                //Circuit, Nodes[-1] is Exit Node
    private int port;
    private int tracker;

    /**
     * Init Peer
     */
    public Peer(int port, int tracker){
        this.port = port;
        this.tracker = tracker;
    }

    /**
     *
     * This function contacts the Tracker to obtain a list of the nodes available and
     * their respective public key for first contact with RSA encryption
     * @param peer
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void connectToServer(Peer peer) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", peer.tracker);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        String initString = "NEWPEER";
        oos.writeObject(initString);
        oos.flush();
        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        peer.availableNodes = (ArrayList<Integer>) ois.readObject();
        peer.pkbs = (HashMap<Integer, Integer>) ois.readObject();
        System.out.println("Connected to Tracker!");
    }

    /**
     * Chooses a path in the Tor network
     * @param peer
     * @param N         //Number of nodes until exit node
     * @return
     */
    private static int[] selectNodes(Peer peer, int N){
        int[] path = new int[N];
        for (int i = 0; i<N; i++){
            Random rand=new Random();
            int randomNumber = rand.nextInt(peer.availableNodes.size());
            path[i] = peer.availableNodes.get(randomNumber);
            peer.availableNodes.remove(randomNumber);
        }
        return path;
    }

    /**
     * Crafts the message that will be sent to the nodes the peer has chosen as it's TOR circuit
     * @param peer
     * @param firstPseudo
     * @return
     */
    private static String craftInitMessage(Peer peer, String firstPseudo){
        //TODO choose sym keys
        //TODO add RSA encryption with node's pbks
        String msg = "comType: ANNOUNCE, nextNode:" + peer.tracker
                + ", msg: " + messages.S(peer.availableFiles).substring(2, messages.S(peer.availableFiles).length()-2);
        for (int i = peer.Nodes.length -1; i>=0; i = i-1){
            System.out.println(msg);
            int nodePort = peer.Nodes[i];
            int anInt = peer.pkbs.get(nodePort);
            msg = CryptoAlgorithms.asymEncryption(msg, anInt);
            msg = "comType: INIT, nextNode: " + peer.Nodes[i] + ", symKeys: " + "INSERTNEWSYMKEYS"
                    + ", msg: " + msg;
        }
        msg = "pseudo: " + firstPseudo + ", previousNode: " + peer.port + ", msg: " + msg;
        System.out.println(msg);
        return msg;
    }

    /**
     * Main function, initialises the variables needed to make a peer
     * This function also contains the logic of a peer:
     *      if it wants to get a file, it can download it
     *      otherwise it becomes a server waiting for someone to download it's files.
     * This function most likely also does not function once it sends the init message for the peer.
     * @param args : peerPort trackerPort numberOfNodes (per circuit)
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String args[]) throws IOException, ClassNotFoundException {     //port tracker numberOfNodes
        int port = Integer.parseInt(args[0]);
        int tracker = Integer.parseInt(args[1]);
        Peer peer = new Peer(port, tracker);
        connectToServer(peer);
        int N = Integer.parseInt(args[2]);
        peer.Nodes = selectNodes(peer, N);
        String peerPseudo = messages.generateRandomString(20);
        String messageToSend = craftInitMessage(peer, peerPseudo);
        Socket clientSocket = new Socket("127.0.0.1", peer.Nodes[0]);

        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(messageToSend);
        oos.flush();

        InputStream inputToServer = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);
        Set<String> fileToDownload = (Set<String>) ois.readObject();

        boolean no = true;
        System.out.println("Do you want to download files?");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String getFile = br.readLine();
        while (true) {
            if (getFile.compareTo("y") == 0 && no) {
                System.out.println("here are all the available files:");
                System.out.println(fileToDownload);
                System.out.println("Enter the name of a file you want:");
                String fileName = br.readLine();
                System.out.println("Do you want to download files?");
                isr = new InputStreamReader(System.in);
                br = new BufferedReader(isr);
                getFile = br.readLine();
                String message = craftGetFileMessage(peer, peerPseudo, fileName);
                oos = new ObjectOutputStream(outputStream);
                oos.writeObject(message);
                oos.flush();

            } else {
                no = false;
                try(ServerSocket serverSocket = new ServerSocket(peer.port)) {

                    System.out.println("Tracker initialised." );
                    while(true) {
                        Socket connectionSocket = serverSocket.accept();
                        CompletableFuture.runAsync(() -> {
                            try {
                                handleConnection(connectionSocket, peer, peerPseudo);
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }

    }

    /**
     * Same as craftInitMessage but makes message when Peer downloads another Peer's files.
     * @param peer
     * @param firstPseudo
     * @param fileName
     * @return
     */
    private static String craftGetFileMessage(Peer peer, String firstPseudo, String fileName){
        //TODO AES encryption
        String msg = "comType: GETFILE, nextNode:" + peer.tracker
                + ", msg: " + fileName;
        for (int i = peer.Nodes.length -1; i>0; i = i-1){
            int nodePBK = peer.pkbs.get(peer.Nodes[i]);
            msg = CryptoAlgorithms.symEncryption(msg, nodePBK);
            msg = "comType: FORWARD, nextNode: " + peer.Nodes[i] + ", msg: " + msg;
        }
        msg = "pseudo: "+firstPseudo+", previousNode: "+ peer.port+", msg:"+ msg;
        return msg;
    }

    /**
     * Same as in Node.java... An attempt at making a server that does not monopolise the peer.
     * @param connectionSocket
     * @param peer
     * @param peerPseudo
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void handleConnection(Socket connectionSocket, Peer peer, String peerPseudo) throws IOException, ClassNotFoundException {
        //Create Input&Outputstreams for the connection
        InputStream inputToServer = connectionSocket.getInputStream();
        OutputStream outputFromServer = connectionSocket.getOutputStream();
        ObjectInputStream ois = new ObjectInputStream(inputToServer);

        // has a look at incoming Request
        String messageReceived = (String) ois.readObject();
        String[] requestExtract = {"previousNode", "comType", "nextNode", "msg"};
        Map<String, String> requestInfo = messages.getValuesFromMsg(requestExtract,messageReceived);
        String decryptedMessage = requestInfo.get("msg");
        //Sends the file after encrypting it with AES
        for (int i = 0; i<peer.Nodes.length; i+=1){
            decryptedMessage = CryptoAlgorithms.symDecryption(decryptedMessage, peer.pkbs.get(i));
        }
        File loadedFile = new File("./"+decryptedMessage);
        byte[] file = Files.readAllBytes(loadedFile.toPath());
        String tstr = "";
        for (int i = peer.Nodes.length; i>=0;  i = i-1){
            tstr = CryptoAlgorithms.symEncryption(file.toString(), peer.Nodes[i]);
        }
        ObjectOutputStream oos = new ObjectOutputStream(outputFromServer);
        oos.writeObject(tstr);
        oos.flush();
    }

}
