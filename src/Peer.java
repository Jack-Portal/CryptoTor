import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *  This class is defining a peer.
 */
public class Peer {
    final File folder = new File("./");
    public final String[] availableFiles = {"file.txt", "otherFile.txt"};
    public ArrayList<Integer> availableNodes;
    public ArrayList<>
    public HashMap<Integer, Integer> pkbs;
    private int[] Nodes;
    private int port;
    private int tracker;

    public Peer(int port, int tracker){
        this.port = port;
        this.tracker = tracker;
    }

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

    private static int[] selectNodes(Peer peer, int N){
        int[] path = new int[N];
        for (int i = 0; i<N; i++){
            Random rand=new Random();
            int randomNumber = rand.nextInt(peer.availableNodes.size());
            path[i] = peer.availableNodes.get(randomNumber);
        }
        return path;
    }

    private static String craftInitMessage(Peer peer, String firstPseudo){
        //TODO choose sym keys
        String msg = "comType: INIT, nextNode:" + peer.tracker
                + ", msg: " + messages.S(peer.availableFiles).substring(2, messages.S(peer.availableFiles).length()-2);
        for (int i = peer.Nodes.length -1; i>0; i = i-1){
            int nodePBK = peer.pkbs.get(peer.Nodes[i]);
            msg = CryptoAlgorithms.asymEncryption(msg, nodePBK);
            msg = "comType: INIT, nextNode: " + peer.Nodes[i] + ", symKeys: " + "INSERTNEWSYMKEYS"
                    + ", msg: " + msg;
        }
        msg = "pseudo: "+firstPseudo+", previousNode: "+ peer.port+", msg:"+ msg;
        return msg;
    }

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

        System.out.println("here are all the available files:");
        System.out.print
        System.out.println("Enter the name of a file you want:");
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) System.out.println(sc.nextLine());
    }

}
