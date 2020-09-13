package four;

import org.util.annotations.SendEveryField;

@SendEveryField
public class StrMsg {
    String msg;

    public StrMsg() {
    }

    public StrMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "StrMsg{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
