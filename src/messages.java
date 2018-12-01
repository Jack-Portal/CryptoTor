/**
 *
 */
public class messages {
    public static final String[] allComType = {"GETFILE", "FORWARD", "INITNODE", "INITPEER", "CONTACT"};

    public String craftMessaegUp(int[] symkeys, int[] Nodes, String firstPseudo, String finalMessage){
        String msg = "comType: GETFILE, nextnode: TRACKER, msg: " + finalMessage + ",";
        for (int i = symkeys.length -1 ; i > 0 ; i-=0 ){
            msg = CryptoAlgorithms.symEncryption(msg, symkeys[i]);
            msg = "comType: FORWARD, nextnode: "+Nodes[i]+", msg:" +msg+",";
        }
        msg = CryptoAlgorithms.symEncryption(msg, symkeys[0]);
        msg = "pseudo: "+firstPseudo+", msg:" +msg+",";
        return msg;
    }

    public String forwardMessaegUp(String nextPseudo, String msg){
        msg = "pseudo: "+nextPseudo+", msg:" +msg+",";
        return msg;
    }

    public String craftMessageDown(String msgReceived, String lastPseudo, int thisNodeSymKey){
        String msg = "pseudo: " + lastPseudo + ", msg: " + CryptoAlgorithms.symEncryption(msgReceived, thisNodeSymKey) +",";
        return msg;
    }

    public String[] getvaluesFromMsg(String[] keys, String msg){
        String[] values = new String[keys.length];
        for (int i = 0; i<keys.length-1; i+=1){
            int starting = msg.indexOf(keys[i]) + keys[i].length() + 1;
            values[i] = msg.substring(msg.indexOf(starting, msg.indexOf(',', starting)));
        }
        return values;
    }

    public String craftInitMessage(){
        return"";
    }
    
    public String crafinitResponse(){
        return"";
    }
}
