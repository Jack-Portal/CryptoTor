import java.math.BigInteger;
import java.security.SecureRandom;

public final class RSA {
    private final static BigInteger one = new BigInteger("1");
    private final static SecureRandom random = new SecureRandom();

    public static BigInteger[] generateKeyPair() {
        int N = random.nextInt(1000);
        BigInteger p = BigInteger.probablePrime(N / 2, random);
        BigInteger q = BigInteger.probablePrime(N / 2, random);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

        BigInteger m = p.multiply(q);
        BigInteger pbk = new BigInteger("65537");
        BigInteger pvk = pbk.modInverse(phi);
        BigInteger[] toReturn = {pbk, pvk, m};
        return toReturn;
    }

    public static byte[] encrypt(byte[] message, BigInteger pbk, BigInteger m) {
        BigInteger integerMessage = new BigInteger(1, message);
        return integerMessage.modPow(pbk, m).toByteArray();
    }

    public static byte[] decrypt(byte[] encrypted, BigInteger pvk, BigInteger m) {
        BigInteger integerEncrypted = new BigInteger(1, encrypted);
        return integerEncrypted.modPow(pvk, m).toByteArray();
    }
}