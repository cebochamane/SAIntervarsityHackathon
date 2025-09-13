import java.security.*;
import java.util.ArrayList;

/**
 * Represents a financial transaction between wallets
 */
public class Transaction {

    // Transaction metadata
    public String transactionId; // SHA-256 hash of transaction
    public PublicKey sender; // Sender's public key
    public PublicKey recipient; // Recipient's public key
    public float value; // Amount being transferred
    public byte[] signature; // ECDSA signature

    // Transaction inputs (UTXOs being spent)
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

    // Transaction outputs (new UTXOs being created)
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    // Counter to ensure unique transaction IDs
    private static int sequence = 0;

    /**
     * Constructor
     * 
     * @param from   Sender's public key
     * @param to     Recipient's public key
     * @param value  Amount to transfer
     * @param inputs UTXOs being spent (null for coinbase transactions)
     */
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = (inputs != null) ? inputs : new ArrayList<TransactionInput>();
    }

    /**
     * Calculates transaction hash (used as ID)
     * 
     * @return SHA-256 hash of transaction data
     */
    private String calculateHash() {
        sequence++; // Ensure unique hash even for identical transactions
        return StringUtil.turnIntoUnrecognizableGibberish(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        sequence);
    }

    /**
     * Generates ECDSA signature for transaction
     * 
     * @param privateKey Sender's private key
     */
    public void generateSignature(PrivateKey privateKey) {
        // Create signing data from immutable transaction fields
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * Verifies transaction signature
     * 
     * @return true if signature is valid
     */
    public boolean verifySignature() {
        // Recreate signing data
        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * Processes the transaction:
     * 1. Verifies signature
     * 2. Gathers inputs
     * 3. Checks sufficient funds
     * 4. Creates outputs
     * 
     * @return true if transaction processed successfully
     */
    public boolean processTransaction() {
        // First verify signature
        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gather transaction inputs from global UTXO pool
        for (TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        // Check transaction meets minimum amount
        float inputValue = getInputsValue();
        if (inputValue < NoobChain.minimumTransaction) {
            System.out.println("#Transaction Inputs too small: " + inputValue);
            return false;
        }

        // Calculate leftover change
        float leftOver = inputValue - value;
        transactionId = calculateHash();

        // Create outputs:
        // 1. Payment to recipient
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));

        // 2. Change back to sender (if any)
        if (leftOver > 0) {
            outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
        }

        // Add outputs to global UTXO pool
        for (TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id, o);
        }

        // Remove spent inputs from UTXO pool
        for (TransactionInput i : inputs) {
            if (i.UTXO != null) {
                NoobChain.UTXOs.remove(i.UTXO.id);
            }
        }

        return true;
    }

    /**
     * Calculates total value of inputs
     * 
     * @return Sum of all input values
     */
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO != null) {
                total += i.UTXO.value;
            }
        }
        return total;
    }

    /**
     * Calculates total value of outputs
     * 
     * @return Sum of all output values
     */
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}