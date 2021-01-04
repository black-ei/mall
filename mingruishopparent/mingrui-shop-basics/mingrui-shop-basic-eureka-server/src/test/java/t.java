public class t {
    public static void main(String[] args) {
        String s1 = "123";
        String s = new String("123");
        char[] chars = s1.toCharArray();
        chars[0] = 2;
        System.err.println(s1.equals(s));
        System.err.println(s.hashCode());
    }
}
