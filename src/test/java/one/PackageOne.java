package one;

import org.util.SendField;

public class PackageOne {
    @SendField
    public int one = 0;
    @SendField
    public long two = 0L;
    @SendField
    public double three = 0.;
    @SendField
    public String four = "Hello";
    @SendField
    public boolean five = true;

    public PackageOne() {
    }

    public PackageOne(int one, long two, double three, String four, boolean five) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    @Override
    public String toString() {
        return "PackageOne{" +
                "one=" + one +
                ", two=" + two +
                ", three=" + three +
                ", four='" + four + '\'' +
                ", five=" + five +
                '}';
    }
}
