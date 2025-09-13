import java.util.ArrayList;
import java.util.List;

public class StokvelService {
    public List<Group> groups = new ArrayList<>();

    public Group createGroup(String name, float monthlyContribution) {
        Group group = new Group();

        group.name = name;
        group.monthlyContribution = monthlyContribution;
        group.wallet = new Wallet();
        groups.add(group);
        return group;

    }

}
