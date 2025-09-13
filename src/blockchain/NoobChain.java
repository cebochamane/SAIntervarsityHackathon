import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main blockchain class that manages the chain and network operations
 */
public class NoobChain {

    // Blockchain storage
    public static ArrayList<Stina> blockchain = new ArrayList<Stina>();

    // Global unspent transaction output pool
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    // Configuration parameters
    public static int difficulty = 5; // Mining difficulty (leading zeros)
    public static float minimumTransaction = 0.1f; // Minimum transaction amount

    // Test wallets
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    /**
     * Main entry point for blockchain system
     */
    public static void main(String[] args) {
        // Setup Bouncy Castle as security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create test wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet(); // Special wallet for genesis block

        // Create genesis transaction (first transaction in blockchain)
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey); // Manually sign
        genesisTransaction.transactionId = "0"; // Special ID for genesis

        // Create initial UTXO
        genesisTransaction.outputs.add(new TransactionOutput(
                genesisTransaction.recipient,
                genesisTransaction.value,
                genesisTransaction.transactionId));

        // Store genesis UTXO
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Stina genesis = new Stina("0"); // First block has no previous hash
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // Test blockchain operations
        testBlockchain();
    }

    /**
     * Tests basic blockchain operations
     */
    private static void testBlockchain() {
        // Block 1: WalletA sends funds to WalletB
        Stina block1 = new Stina(blockchain.get(blockchain.size() - 1).hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        // Block 2: Attempt invalid transaction
        Stina block2 = new Stina(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        // Block 3: WalletB sends funds back to WalletA
        Stina block3 = new Stina(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        addBlock(block3);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        // Validate entire chain
        System.out.println("\nBlockchain is " + (isChainValid() ? "valid" : "invalid"));
    }

    /**
     * Validates the integrity of the entire blockchain
     * 
     * @return true if blockchain is valid
     */
    public static Boolean isChainValid() {
        Stina currentBlock;
        Stina previousBlock;

        // Target hash prefix based on difficulty
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Temporary working list of UTXOs (starts with genesis UTXO)
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // Loop through blockchain starting from block 1 (skip genesis)
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // Verify current block's hash is correct
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }

            // Verify link to previous block
            if (!previousBlock.hash.equals(currentBlock.prevHash)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }

            // Verify proof-of-work was done
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            // Validate all transactions in block
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                // Verify transaction signature
                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }

                // Verify input/output values match
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                // Verify all inputs exist in UTXO set
                for (TransactionInput input : currentTransaction.inputs) {
                    TransactionOutput tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    // Verify input value matches UTXO value
                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    // Remove spent UTXO from temp set
                    tempUTXOs.remove(input.transactionOutputId);
                }

                // Add new outputs to temp UTXO set
                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                // Verify output recipients are correct
                if (!currentTransaction.outputs.get(0).recipient.equals(currentTransaction.recipient)) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }

                // Verify change output goes back to sender
                if (currentTransaction.outputs.size() > 1 &&
                        !currentTransaction.outputs.get(1).recipient.equals(currentTransaction.sender)) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }

        System.out.println("Blockchain is valid");
        return true;
    }

    /**
     * Adds a new block to the blockchain after mining
     * 
     * @param newBlock Block to add
     */
    public static void addBlock(Stina newBlock) {
        newBlock.mineBlock(difficulty); // Mine the block first
        blockchain.add(newBlock); // Add to chain
    }
}