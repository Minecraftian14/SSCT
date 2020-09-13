package two;

import org.util.annotations.ByteString;
import org.util.annotations.SendField;
import org.util.annotations.ShortString;

public class SamplePack1 {

    @SendField
    boolean bl = true;
    @SendField
    byte bt = 23;
    @SendField
    char ch = 42;
    @SendField
    short sh = 14545;
    @SendField
    int in = 34264;
    @SendField
    long lg = 234643;
    @SendField
    float fl = 345.0f;
    @SendField
    double db = 3452.45;
    @SendField
    String st = "wsr";
//    @ShortString   // TODO fix the bug MANNN
//    String sst = "rgeg";
//    @ByteString
//    String bst = "shsh";

    @Override
    public String toString() {
        return "SamplePack1{" +
                "bl=" + bl +
                ", bt=" + bt +
                ", ch=" + ch +
                ", sh=" + sh +
                ", in=" + in +
                ", lg=" + lg +
                ", fl=" + fl +
                ", db=" + db +
                ", st='" + st + '\'' +
//                ", sst='" + sst + '\'' +
//                ", bst='" + bst + '\'' +
                '}';
    }
}
