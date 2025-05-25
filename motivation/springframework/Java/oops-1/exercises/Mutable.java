public class Mutable {
    public static void main(String[] args) {
        // StringBuffer
        System.out.println("---sbf1---");
        StringBuffer sbf1 = new StringBuffer("Virat");
        System.out.println(sbf1);
        // StringBuffer sb1="";invalid
        sbf1.append("Kohli");
        System.out.println(sbf1);

        // StringBuilder
        System.out.println("---sbd1---");
        StringBuilder sbd1 = new StringBuilder("Virat");
        System.out.println(sbd1);
        // StringBuffer sbd="";invalid
        sbd1.append("Kohli");
        System.out.println(sbd1);

        // final int a=10;
        // a=20;
        // System.out.println(a);
        System.out.println("---sbf2---");
        final StringBuffer sbf2 = new StringBuffer("Virat");
        sbf2.append("Kohli");
        System.out.println(sbf2);

        System.out.println("---sbf3---");
        StringBuffer sbf3 = new StringBuffer("Sachin");
        System.out.println(sbf3);
        System.out.println(sbf3.capacity());
        System.out.println(sbf3.charAt(1));
        sbf3.setCharAt(1, 'A'); // SAachin or SAchin
        System.out.println(sbf3);

        System.out.println("---sbd2---");
        StringBuilder sbd2 = new StringBuilder(150);
        System.out.println(sbd2.capacity());
        sbd2.append("java");
        System.out.println(sbd2);
        sbd2.trimToSize();
        System.out.println(sbd2.capacity());

        System.out.println("---sbd3---");
        StringBuilder sbd3 = new StringBuilder();
        System.out.println(sbd3.capacity());
        sbd3.append("abcdefghijklmnop");
        System.out.println(sbd3.capacity());
        sbd3.append("s");
        System.out.println(sbd3.capacity());// how many character you can add
        System.out.println(sbd3.length());// how mancy character r present

        System.out.println("---sbd4---");
        StringBuilder sbd4 = new StringBuilder("pwjava");
        System.out.println(sbd4);
        System.out.println(sbd4.reverse());
    }
}
// Output
// ---sbf1---
// Virat
// ViratKohli
// ---sbd1---
// Virat
// ViratKohli
// ---sbf2---
// ViratKohli
// ---sbf3---
// Sachin
// 22
// a
// SAchin
// ---sbd2---
// 150
// java
// 4
// ---sbd3---
// 16
// 16
// 34
// 17
// ---sbd4---
// pwjava
// avajwp