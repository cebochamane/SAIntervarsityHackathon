/**
 * Represents an input to a transaction
 * References an unspent transaction output (UTXO)
 */
public class TransactionInput {

    // Reference to the transaction output being spent
    public String transactionOutputId;

    // The actual unspent transaction output
    public TransactionOutput UTXO;

    /**
     * Constructor
     * 
     * @param transactionOutputId ID of the UTXO being spent
     */
    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}