package chapter9;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class EdDSADemo {

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        /*
         * Create a Signature object and initialize it with the private key
         */
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(priv);
        String str = "This is string to sign";
        byte[] strByte = str.getBytes(StandardCharsets.UTF_8);
        ecdsa.update(strByte);
        byte[] realSig = ecdsa.sign();
        System.out.println("Signature: " + new BigInteger(1, realSig).toString(16));

        /*
         * Verify the signature
         */
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(pub);
        sig.update(strByte);
        System.out.println(sig.verify(realSig));
    }
}
