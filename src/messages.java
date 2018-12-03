/**
 * Jacques-Antoine portal
 */

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class just contains a few functions used by all the other classes.
 */
public class messages {

    /**
     * This function extracts elements out of a string ina  similar way a dictionary would.
     * It is used to carry all the information and keep a consistant format between nodes, the tracker and the peers (as often as possible)
     * @param keys
     * @param msg
     * @return
     */
    public static Map<String, String> getValuesFromMsg(String[] keys, String msg){
        HashMap<String, String> values = new HashMap<String, String>() {};
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
            values.put(keys[i], msg.substring(starting, ending));
        }
        return values;
    }

    /**
     * This function takes an array and makes a string out of it
     * @param Array
     * @return
     */
    public static String S(String[] Array){
        String toPrint = "[ ";
        for (String i : Array){
            toPrint += "'" + i + "', ";
        }
        toPrint = toPrint.substring(0, toPrint.length()-2) +" ]";
        return toPrint;
    }

    /**
     * self explanatory, used to generate symmetric keys and pseudonyms.
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        String randomString = "";

        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890".toCharArray();
        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomString = randomString + chars[random.nextInt(chars.length)];
        }

        return randomString;
    }
}
