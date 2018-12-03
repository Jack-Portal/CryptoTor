/**
 *
 */
public class messages {
    public static final String[] allComType = {"GETFILE", "FORWARD", "INITNODE", "INITPEER", "CONTACT"};

    public static String craftMessageUp(int[] symkeys, int[] Nodes, String firstPseudo, String finalMessage){
        String msg = "comType: GETFILE, nextnode: TRACKER, msg: " + finalMessage;
        for (int i = symkeys.length -1 ; i > 0 ; i-=1 ){
            msg = CryptoAlgorithms.symEncryption(msg, symkeys[i]);
            msg = "comType: FORWARD, nextnode: "+Nodes[i]+", msg: " +msg;
        }
        msg = CryptoAlgorithms.symEncryption(msg, symkeys[0]);
        msg = "pseudo: "+firstPseudo+", msg: " +msg;
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

    public static String[] getValuesFromMsg(String[] keys, String msg){
        String[] values = new String[keys.length];
        for (int i = 0; i<keys.length; i+=1){
            String toFind = keys[i]+": ";
            int starting = msg.indexOf(toFind) + toFind.length() ;
            int ending;
            if (!toFind.equals("msg: ")) {
                ending = msg.indexOf(',', starting);
            }
            else{
                ending = msg.length();
            }
            values[i] = msg.substring(starting, ending);
        }
        return values;
    }

    public String craftInitMessage(){
        return"";
    }

    public String crafinitResponse(){
        return"";
    }

    public static void main(String args[]){
        int[] nodes = {1, 2, 3};
        int[] keys = {4, 5, 6};
        String firstPseudo = "salut";
        String finalMessage = "ceci est le final message";
        String msg = craftMessageUp(keys, nodes, firstPseudo, finalMessage );
        System.out.println( msg );
        String[] extracted;
        String[] toExtract1 = {"pseudo", "msg"};
        extracted = getValuesFromMsg(toExtract1, msg);
        System.out.println(S(extracted));
        String[] toExtract2 = {"comType", "nextnode", "msg"};
        extracted = getValuesFromMsg(toExtract2, extracted[extracted.length-1]);
        System.out.println(S(extracted));
        extracted = getValuesFromMsg(toExtract2, extracted[extracted.length-1]);
        System.out.println(S(extracted));
    }

    public static String S(String[] Array){
        String toPrint = "[ ";
        for (String i : Array){
            toPrint += "'" + i + "', ";
        }
        toPrint = toPrint.substring(0, toPrint.length()-2) +" ]";
        return toPrint;
    }
}
