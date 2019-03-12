package com.example.asus.blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;


public class DigitalSignature {

    /**
     * convertPrivateKeyToPEM converts the private key to the PEM format.
     * @param key: the secret key
     * @return the PEM encoded secret key
     */
    public static String convertPrivateKeyToPEM(String key) {
        // header
        StringBuffer buff = new StringBuffer("-----BEGIN PRIVATE KEY-----\n");
        // body
        int n = key.length();
        int num_lines = (int)Math.ceil((double)n/64.0d);
        for(int i = 0; i < num_lines; i++) {
            int begin = i*64;
            int end = (i+1)*64;
            if(end > n) end = n;
            buff.append(key.substring(begin, end));
            buff.append("\n");
        }
        // footer
        buff.append("-----END PRIVATE KEY-----");

        return buff.toString();
    }


    /**
     * convertPublicKeyToPEM converts the public key to the PEM format.
     * @param key: the public key
     * @return the PEM encoded secret key
     */
    public static String convertPublicKeyToPEM(String key) {
        // header
        StringBuffer buff = new StringBuffer("-----BEGIN PUBLIC KEY-----\n");
        // body
        int n = key.length();
        int num_lines = (int)Math.ceil((double)n/64.0d);
        for(int i = 0; i < num_lines; i++) {
            int begin = i*64;
            int end = (i+1)*64;
            if(end > n) end = n;
            buff.append(key.substring(begin, end));
            buff.append("\n");
        }
        // footer
        buff.append("-----END PUBLIC KEY-----");
        return buff.toString();
    }


    /**
     * generateRSAKeyPair generates a pair of RSA private and public keys.
     * @return a pair of RSA private and public keys
     * @throws Exception
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(spec);
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }


    /**
     * generateRSAKeyPair generates a pair of EC (Elliptic Curve) private and public keys.
     * @return a pair of EC private and public keys
     * @throws Exception
     */
    public static KeyPair generateECKeyPair() throws Exception {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        keyPairGen.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }


    /**
     * sign() can generate the digital signature using a specific signing algorithm.
     * @param data: the message to be signed
     * @param privateKey: the private key used to sign the message
     * @param algorithm: the signing algorithm ("SHA256withRSA", "SHA256withECDSA", ...)
     * @return the digital signature
     * @throws Exception
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] digitalSignature = Base64.getEncoder().encode(signature.sign());
        return digitalSignature;
    }


    /**
     * verify() can verify the digital signature.
     * @param data: the message to be verified
     * @param publicKey: the public key
     * @param digitalSignature: the digital signature
     * @param algorithm: the signing algorithm ("SHA256withRSA", "SHA256withECDSA", ...)
     * @return a boolean value (true or false) representing the verification result
     * @throws Exception
     */
    public static boolean verify(byte[] data, PublicKey publicKey, byte[] digitalSignature, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data);
        boolean result = signature.verify(digitalSignature);
        return result;
    }
    /**
     *
     * @param privateKey
     * @param message
     * @return the encrypted message
     * @throws Exception
     */
    public static byte[] rsaEncryptPrivate(PrivateKey privateKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");  // correspond to crypto.privateEncrypt(.) in Node.js
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(message.getBytes());
    }


    /**
     *
     * @param publicKey
     * @param encrypted
     * @return the decrypted message
     * @throws Exception
     */
    public static byte[] rsaDecryptPublic(PublicKey publicKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");  // correspond to crypto.publicDecrypt(.) in Node.js
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(encrypted);
    }


    public static byte[] rsaEncryptPublic(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");  // correspond to crypto.publicEncrypt(.) in Node.js
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(message.getBytes());
    }


    public static byte[] rsaDecryptPrivate(PrivateKey privateKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");  // correspond to crypto.privateDecrypt(.) in Node.js
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(encrypted);
    }
}
