package one;

import org.util.SendField;

public class PackageTwo {
    @SendField
    int i = 9;
    @SendField
    PackageOne demo = new PackageOne(0, 1L, 2.0, "3", true);

    @Override
    public String toString() {
        return "PackageTwo{" +
                "i=" + i +
                ", demo=" + demo +
                '}';
    }
}
