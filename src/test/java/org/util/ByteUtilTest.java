package org.util;


public class ByteUtilTest {
    public static void main(String[] args) {

        byte[] h = ByteUtil.forString("a b c d e f g h i j k l m n o p q r s t u v w x y z");
        System.out.println(ByteUtil.asString(h));

        h = ByteUtil.forShortString("a b c d e f g h i j k l m n o p q r s t u v w x y z");
        System.out.println(ByteUtil.asShortString(h));

        h = ByteUtil.forByteString("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrtu");
        System.out.println(ByteUtil.asByteString(h));

        h = ByteUtil.forByteString("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
        System.out.println(ByteUtil.asByteString(h));

        System.out.println(ByteUtil.asInt(ByteUtil.forInt(27354)));

    }
}
