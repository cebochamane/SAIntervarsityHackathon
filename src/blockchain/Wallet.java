import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's wallet in the blockchain system.
 * Manages public/private key pairs and unspent transaction outputs (UTXOs).
 */
public class Wallet {

    // Cryptographic keys for the wallet
    public PrivateKey privateKey; // Used to sign transactions
    public PublicKey publicKey; // Used as wallet address

    // Tracks all unspent transaction outputs owned by this wallet
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    /**
     * Constructor - automatically generates key pair when wallet is created
     */
    public Wallet() {
        generateKeyPair();
    }

    /**
     * Generates ECDSA key pair using Bouncy Castle provider
     * Uses prime192v1 elliptic curve for signatures
     */
    public void generateKeyPair() {
        try {
            // Initialize key generator with ECDSA algorithm
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Use prime192v1 elliptic curve parameters
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Generate the key pair
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Store the generated keys
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate wallet key pair", e);
        }
    }

    /**
     * Calculates and returns the wallet's total balance
     * Updates the wallet's UTXO list from the global UTXO pool
     * 
     * @return Total balance of unspent outputs owned by this wallet
     */
    public float getBalance() {
        float total = 0;

        // Check all UTXOs in the blockchain
        for (Map.Entry<String, TransactionOutput> item : NoobChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();

            // If output belongs to this wallet (coins we can spend)
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO); // Add to our local UTXO list
                total += UTXO.value; // Add value to balance
            }
        }
        return total;
    }

    /**
     * Creates and sends a new transaction
     * 
     * @param _recipient Public key of recipient
     * @param value      Amount to send
     * @return New transaction or null if failed
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        // Validate transaction amount meets minimum
        if (value < NoobChain.minimumTransaction) {
            System.out.println("#Transaction value below minimum. Transaction Discarded.");
            return null;
        }

        // Check sender has sufficient balance
        if (getBalance() < value) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // Create list of transaction inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        float total = 0;

        // Gather enough UTXOs to cover the transaction amount
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total >= value)
                break; // Stop when we have enough
        }

        // Create new transaction
        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey); // Sign with private key

        // Remove spent UTXOs from wallet
        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}