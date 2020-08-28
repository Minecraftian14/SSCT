public class sefsef {

    public static void main(String[] args) {

        int s = 82374;
        StringBuilder b = new StringBuilder();
        while (s != 0) {
            b.append((char)((s%10)+48));
            s /= 10;
        }
        System.out.println(b.reverse());

    }

}
