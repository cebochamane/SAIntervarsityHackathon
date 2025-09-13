import java.util.List;

public class Group {
    public String id;
    public String name;
    public float monthlyContribution;

    List<Member> members;
    List<LedgerEntry> ledger;

    Wallet wallet = new Wallet();
}