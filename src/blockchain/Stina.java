import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a block in the blockchain
 * Contains transactions and links to previous block via hash
 */
public class Stina {

    // Block metadata
    public String hash; // SHA-256 hash of this block
    public String prevHash; // Reference to previous block's hash
    private long timeStamp; // When block was created (milliseconds since epoch)
    private int nonce; // Proof-of-work counter

    // Transaction data
    public String merkleRoot; // Hash of all transactions in block
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // Transaction list

    /**
     * Constructor - creates new block with reference to previous block
     * 
     * @param prevHash Hash of previous block in chain
     */
    public Stina(String prevHash) {
        this.prevHash = prevHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); // Calculate initial hash
    }

    /**
     * Calculates block hash using:
     * - Previous block's hash
     * - Timestamp
     * - Nonce
     * - Merkle root of transactions
     * 
     * @return SHA-256 hash of block contents
     */
    public String calculateHash() {
        String input = prevHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                merkleRoot;
        return StringUtil.turnIntoUnrecognizableGibberish(input);
    }

    /**
     * Mines the block by finding a hash that meets difficulty target
     * 
     * @param difficulty Number of leading zeros required in hash
     */
    public void mineBlock(int difficulty) {
        // First calculate Merkle root of all transactions
        merkleRoot = StringUtil.getMerkleRoot(transactions);

        // Create target string (e.g. "00000" for difficulty 5)
        String target = StringUtil.getDifficultyString(difficulty);

        // Keep recalculating hash with incremented nonce until we meet target
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined: " + hash);
    }

    /**
     * Adds a transaction to this block after validation
     * 
     * @param transaction Transaction to add
     * @return true if transaction was added successfully
     */
    public boolean addTransaction(Transaction transaction) {
        // Reject null transactions
        if (transaction == null) {
            return false;
        }

        // Skip validation for genesis block transactions
        if (!prevHash.equals("0")) {
            // Process and validate transaction
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        // Add valid transaction to block
        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block");
        return true;
    }
}