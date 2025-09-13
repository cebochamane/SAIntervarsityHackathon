import java.util.ArrayList;
import java.util.List;

public class Group {
    public String id;
    public String name;
    public float monthlyContribution;

    public List<Member> members = new ArrayList<>();
    public List<LedgerEntry> ledger = new ArrayList<>();

    Wallet wallet = new Wallet();

    public Member findMemberByID(String memberId) {
        for (Member member : members) {
            if (member.id.equals(memberId)) {
                return member;
            }

        }
        return null;
    }

    public void addLedgerEntry(LedgerEntry entry) {
        ledger.add(entry);
    }
}