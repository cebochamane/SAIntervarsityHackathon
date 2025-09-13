import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Provides cryptographic utility functions for the blockchain
 */
public class StringUtil {

    /**
     * Applies SHA-256 hashing to input string
     * 
     * @param input String to hash
     * @return Hex-encoded SHA-256 hash
     */
    public static String turnIntoUnrecognizableGibberish(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            // Convert byte array to hex string
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SHA-256 hash", e);
        }
    }

    /**
     * Signs data using ECDSA private key
     * 
     * @param privateKey Signer's private key
     * @param input      Data to sign
     * @return Digital signature
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            dsa.update(input.getBytes());
            return dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ECDSA signature", e);
        }
    }

    /**
     * Verifies ECDSA signature
     * 
     * @param publicKey Signer's public key
     * @param data      Original data that was signed
     * @param signature Signature to verify
     * @return true if signature is valid
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify ECDSA signature", e);
        }
    }

    /**
     * Encodes key as Base64 string
     * 
     * @param key Public or private key
     * @return Base64-encoded key
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Calculates Merkle root from transaction list
     * 
     * @param transactions List of transactions
     * @return SHA-256 hash representing Merkle root
     */
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        // Handle empty transaction list
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }

        ArrayList<String> previousTreeLayer = new ArrayList<String>();

        // Start with transaction IDs as first layer
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;

        // Recursively hash pairs until we get to root
        while (treeLayer.size() > 1) {
            treeLayer = new ArrayList<String>();

            // Hash adjacent pairs
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                String left = previousTreeLayer.get(i - 1);
                String right = (i < previousTreeLayer.size()) ? previousTreeLayer.get(i) : left;
                treeLayer.add(turnIntoUnrecognizableGibberish(left + right));
            }
            previousTreeLayer = treeLayer;
        }

        // Return root hash (or empty string if no transactions)
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    /**
     * Creates difficulty target string for mining
     * 
     * @param difficulty Number of leading zeros required
     * @return String of zeros with specified length
     */
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
}