public class Main {
    public static void main(String[] args) {
        try {
            StokvelService service = new StokvelService();

            Group group = service.createGroup("Amathuba", 200f);
            System.out.println(
                    "Created group: " + group.name + " (id=" + group.id + "), monthly R" + group.monthlyContribution);

            Member maGumede = service.addMember(group.id, "maGumede");
            Member maMjwara = service.addMember(group.id, "maMjwara");

            System.out.println("Added members: " + maGumede.name + " (id= " + maGumede.id + "), " + maMjwara.name
                    + " (id" + maMjwara.id + ")");

            service.contribute(group.id, maGumede.id, 200f);
            service.contribute(group.id, maMjwara.id, 200f);

            service.payout(group.id, maGumede.id, 400f);

            System.out.println();
            service.showLedger(group.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
