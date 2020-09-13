package five;

import org.util.annotations.SendEveryField;

import java.util.Arrays;

@SendEveryField
public class Counter {
    int value;

    public Counter(int value) {
        this.value = value;
    }

    public Counter() {
    }
    @Override
    public String toString() {
        return " " + value + " ";
    }

    public Counter addOne() {
        return new Counter(value + 1);
    }


    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println(Arrays.stream(Counter.class.getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findFirst().orElseThrow(NoSuchMethodException::new));
    }

}
