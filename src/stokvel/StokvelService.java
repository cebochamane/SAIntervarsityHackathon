import java.text.SimpleDateFormat;
import java.util.*;

public class StokvelService {
    private final Map<String, Group> groupsById = new HashMap<>();
    public final List<Group> groups = new ArrayList<>();

    public void showLedger(String groupId) {
        Group group = getGroupOrThrow(groupId);
        System.out.println("== Ledger" + group.name + " ==");

        List<LedgerEntry> sorted = new ArrayList<>(group.ledger);
        Collections.sort(sorted, new Comparator<LedgerEntry>() {
            @Override
            public int compare(LedgerEntry ledger1, LedgerEntry ledger2) {
                return Long.compare(ledger1.timestamp, ledger2.timestamp);
            }

        });

        for (LedgerEntry ledger : sorted) {
            String amount = stripTrailingZeros(ledger.amount);
            String hash = shortHash(ledger.transactionHash);

            if ("contribution".equalsIgnoreCase(ledger.type)) {
                System.out.println(ledger.timeString + "  " + ledger.memberName + " contributed R" + amount + "  (tx "
                        + hash + ")");
            } else if ("payout".equalsIgnoreCase(ledger.type)) {
                System.out.println(ledger.timeString + " " + ledger.memberName + " received payout of R " + amount + " "
                        + "  (tx " + hash + ")");
            } else {
                System.out.println(ledger.timeString + " " + ledger.memberName + " " + ledger.type + " R" + amount + " "
                        + "  (tx " + hash + ")");
            }

        }

    }

    public Member addMember(String groupId, String memberName) {
        Group group = getGroupOrThrow(groupId);

        Member member = new Member();
        member.id = generateId();
        member.name = memberName;
        member.treasuryWallet = new Wallet();      // member's personal wallet

        if (group.members == null) group.members = new ArrayList<>();
        group.members.add(member);
        return member;
    }

    public Group createGroup(String name, float monthlyContribution) {
        Group group = new Group();
        group.id = generateId();
        group.name = name;
        group.monthlyContribution = monthlyContribution;
        group.wallet = new Wallet();
        if (group.members == null)
            group.members = new ArrayList<>();
        if (group.ledger == null)
            group.ledger = new ArrayList<>();

        groupsById.put(group.id, group);
        groups.add(group);
        return group;

    }

    public LedgerEntry contribute(String groupId, String memberId, float amount) {
        Group group = getGroupOrThrow(groupId);
        Member member = getMemberOrThrow(group, memberId);

        Transaction t = group.wallet.sendFunds(member.treasuryWallet.publicKey, amount);
        String txHash = resolveTxHash(t);

        LedgerEntry entry = new LedgerEntry();
        entry.type = "contribution";
        entry.memberName = member.name;
        entry.timestamp = System.currentTimeMillis();
        entry.timeString = formatTs(entry.timestamp);
        entry.transactionHash = txHash;

        group.ledger.add(entry);
        return entry;

    }

    public LedgerEntry payout(String groupId, String memberId, float amount) {
        Group group = getGroupOrThrow(groupId);
        Member member = getMemberOrThrow(group, memberId);

        Transaction t = group.wallet.sendFunds(member.treasuryWallet.publicKey, amount);
        String txHash = resolveTxHash(t);

        LedgerEntry entry = new LedgerEntry();
        entry.type = "payout";
        entry.memberName = member.name;
        entry.timestamp = System.currentTimeMillis();
        entry.timeString = formatTs(entry.timestamp);
        entry.transactionHash = txHash;

        group.ledger.add(entry);
        return entry;

    }

    private Group getGroupOrThrow(String groupId) {
        Group g = groupsById.get(groupId);
        if (g == null)
            throw new IllegalArgumentException("Group not found: " + groupId);
        return g;
    }

    private Member getMemberOrThrow(Group g, String memberId) {
        for (Member m : g.members) {
            if (m.id != null && m.id.equals(memberId))
                return m;
        }
        throw new IllegalArgumentException("Member not found in group: " + memberId);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private String formatTs(long ts) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(ts));
    }

    private String shortHash(String full) {
        if (full == null)
            return "-";
        return (full.length() <= 8) ? full : full.substring(0, 8);
    }

    private String stripTrailingZeros(float f) {
        String s = String.valueOf(f);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    private String resolveTxHash(Transaction tx) {
        if (tx == null)
            return "-";
        try {
            return (String) tx.getClass().getField("transactionId").get(tx);
        } catch (Exception ignore) {
        }
        try {
            return (String) tx.getClass().getMethod("getTransactionId").invoke(tx);
        } catch (Exception ignore) {
        }
        try {
            return (String) tx.getClass().getField("hash").get(tx);
        } catch (Exception ignore) {
        }
        try {
            return (String) tx.getClass().getMethod("getHash").invoke(tx);
        } catch (Exception ignore) {
        }
        return "-";
    }
}
