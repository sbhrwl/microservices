//import java.util.Date;
//import java.util.*;
public class DateTimeDemo {
    public static void main(String[] args) {
        java.util.Date dt = new java.util.Date();
        System.out.println("util.Date: " + dt);

        long timeInMs = dt.getTime();

        java.sql.Date dt1 = new java.sql.Date(timeInMs);
        System.out.println("sql.Date: " + dt1);

        // java.util.ArrayList al=new java.util.ArrayList<>();
    }
}
// Output
// util.Date: Tue Apr 04 13:37:32 EEST 2023
// sql.Date: 2023-04-04