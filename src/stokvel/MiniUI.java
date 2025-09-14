import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.Security;

public class MiniUI extends JFrame {
    private final StokvelService svc = new StokvelService();
    private Group g;
    private final JTextField tfGroup = new JTextField("Amathuba", 10);
    private final JTextField tfMonthly = new JTextField("200", 6);
    private final JTextField tfMember = new JTextField("maGumede", 10);
    private final JComboBox<Member> cbMembers = new JComboBox<>();
    private final JTextField tfAmt = new JTextField("200", 6);
    private final JTextArea log = new JTextArea(12, 40);

    public MiniUI() {
        super("Stokvel Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        log.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Group:"));
        top.add(tfGroup);
        top.add(new JLabel("Monthly R"));
        top.add(tfMonthly);
        top.add(btn("Create", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGroup();
            }
        }));
        top.add(new JLabel("Member:"));
        top.add(tfMember);
        top.add(btn("Add", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        }));
        add(top, BorderLayout.NORTH);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        cbMembers.setPrototypeDisplayValue(new Member() {
            {
                name = "(select)";
            }
        });
        mid.add(new JLabel("Member:"));
        mid.add(cbMembers);
        mid.add(new JLabel("Amount R"));
        mid.add(tfAmt);
        mid.add(btn("Contribute", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contribute();
            }
        }));
        mid.add(btn("Payout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                payout();
            }
        }));
        add(mid, BorderLayout.CENTER);

        add(new JScrollPane(log), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton btn(String text, AbstractAction action) {
        return new JButton(action) {
            {
                setText(text);
            }
        };
    }

    private void createGroup() {
        try {
            float monthly = Float.parseFloat(tfMonthly.getText().trim());
            g = svc.createGroup(tfGroup.getText().trim(), monthly);
            cbMembers.removeAllItems();
            println("Created group " + g.name + " (id=" + g.id + "), monthly R" + g.monthlyContribution);
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void addMember() {
        if (g == null) {
            warn("Create a group first.");
            return;
        }
        String name = tfMember.getText().trim();
        if (name.isEmpty()) {
            warn("Enter member name.");
            return;
        }
        try {
            Member m = svc.addMember(g.id, name);
            cbMembers.addItem(m);
            println("Member added: " + m.name + " (id=" + m.id + ")");
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void contribute() {
        Member m = (Member) cbMembers.getSelectedItem();
        if (g == null || m == null) {
            warn("Create group and select a member.");
            return;
        }
        float amt = parseAmt();
        if (Float.isNaN(amt))
            return;
        try {
            LedgerEntry e = svc.contribute(g.id, m.id, amt);
            if (e == null) {
                warn("Contribution failed (insufficient funds?)");
                return;
            }
            println(m.name + " contributed R" + strip(amt));
        } catch (Exception ex) {
            error(ex);
        }
    }

    private void payout() {
        Member m = (Member) cbMembers.getSelectedItem();
        if (g == null || m == null) {
            warn("Create group and select a member.");
            return;
        }
        float amt = parseAmt();
        if (Float.isNaN(amt))
            return;
        try {
            LedgerEntry e = svc.payout(g.id, m.id, amt);
            if (e == null) {
                warn("Payout failed (insufficient funds?)");
                return;
            }
            println("Payout R" + strip(amt) + " to " + m.name);
        } catch (Exception ex) {
            error(ex);
        }
    }

    private float parseAmt() {
        try {
            return Float.parseFloat(tfAmt.getText().trim());
        } catch (Exception e) {
            warn("Enter a valid amount.");
            return Float.NaN;
        }
    }

    private void println(String s) {
        log.append(s + "\n");
    }

    private void warn(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    private void error(Exception e) {
        e.printStackTrace();
        warn(e.getMessage());
    }

    private String strip(float f) {
        String s = String.valueOf(f);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SwingUtilities.invokeLater(() -> new MiniUI().setVisible(true));
    }
}