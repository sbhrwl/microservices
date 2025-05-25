// //enum -- 1.5 
// enum Week {
//     MON, TUE, WED, THU, FRI, SAT, SUN;
// }

// public class EnumDemo {
//     enum Result {
//         PASS, FAIL, NR;
//     }

//     public static void main(String[] args) {
//         Week w = Week.MON;
//         System.out.println(w);

//         int index = Week.MON.ordinal();
//         System.out.println(index);

//         Week[] wr = Week.values();
//         for (Week w1 : wr) {
//             System.out.println(w1 + " : " + w1.ordinal());

//         }
//         // Result r=Result.PASS;
//         // System.out.println(r);
//     }
// }

enum Result {
    PASS, FAIL, NR;

    // public static final Result PASS=new Result();
    // public static final Result FAIL=new Result();
    // public static final Result NR=new Result();
    int marks;

    Result() {
        System.out.println("Constructor in Enum");
    }

    void setMarks(int marks) {
        this.marks = marks;
    }

    int getMarks() {
        return marks;
    }
}

public class EnumDemo {
    public static void main(String[] args) {

        Result.PASS.setMarks(50);

        int m1 = Result.PASS.getMarks();
        System.out.println(m1);// 50

        int m2 = Result.FAIL.getMarks();
        System.out.println(m2);// As FAIL has not been set, so it has 0 (default value of int)

        Result.NR.setMarks(45);
        int m3 = Result.NR.getMarks();
        System.out.println(m3);// 45
    }
}
// Output
// Constructor in Enum
// Constructor in Enum
// Constructor in Enum
// 50
// 0
// 45