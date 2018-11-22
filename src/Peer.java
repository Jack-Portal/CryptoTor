/**
 *  This class is defining a peer.
 */
public class Peer {
    public final int publicKey;
    public String[] fileNames;
    public String[] AvailableFiles;

    private final int privateKey;
    private String[] nodes;

    public Peer(int publicKey, int privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
