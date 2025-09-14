import java.util.List;
import java.util.ArrayList;

public class Member {
    public int trustScore = 100;
    public int streak = 0;
    public List<String> badges = new ArrayList<>();
    public String lastContribution = null;
    String id;
    String name;
    String phone;
    Wallet treasuryWallet = new Wallet();
}