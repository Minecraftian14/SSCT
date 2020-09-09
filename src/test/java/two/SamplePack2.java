package two;

import org.util.annotations.SendEveryField;

@SendEveryField
public class SamplePack2 {

    boolean bl = true;
    byte bt = 23;
    char ch = 42;
    short sh = 14545;
    int in = 34264;
    long lg = 234643;
    float fl = 345.0f;
    double db = 3452.45;
    String st = "wsr";

    @Override
    public String toString() {
        return "SamplePack2{" +
                "bl=" + bl +
                ", bt=" + bt +
                ", ch=" + ch +
                ", sh=" + sh +
                ", in=" + in +
                ", lg=" + lg +
                ", fl=" + fl +
                ", db=" + db +
                ", st='" + st + '\'' +
                '}';
    }
}
