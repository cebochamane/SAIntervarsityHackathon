import java.security.PublicKey;

/**
 * Represents an unspent transaction output (UTXO)
 * Can be spent as input to new transactions
 */
public class TransactionOutput {

    // Unique identifier for this output
    public String id;

    // Recipient's public key (who can spend this output)
    public PublicKey recipient;

    // Value of this output
    public float value;

    // ID of transaction that created this output
    public String parentTransactionId;

    /**
     * Constructor
     * 
     * @param recipient           Owner of this output
     * @param value               Amount this output represents
     * @param parentTransactionId Transaction that created this output
     */
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;

        // Create ID by hashing recipient, value and parent transaction
        this.id = StringUtil.turnIntoUnrecognizableGibberish(
                StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        parentTransactionId);
    }

    /**
     * Checks if this output belongs to specified public key
     * 
     * @param publicKey Key to check ownership against
     * @return true if output belongs to this key
     */
    public boolean isMine(PublicKey publicKey) {
        return recipient.equals(publicKey);
    }
}