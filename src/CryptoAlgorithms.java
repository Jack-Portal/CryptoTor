/**
 * Jacques-Antoine Portal
 * This was a class where my team mates would have integrated the algorithms they were working on....
 */
public abstract class CryptoAlgorithms {

    public static String symEncryption(String message, int key){
        return "ENCRYPTED("+message+")";
    }

    public static String symDecryption(String message, int key){
        return message.substring("ENCRYPTED(".length(), message.length()-1);
    }

    public static String asymEncryption(String message, int pbKey){
        return message;
    }

    public static String asymDecryption(String message, int pvKey){
        return message;
    }
}
