// //functional interface
// @FunctionalInterface
// interface Demo {
//     void disp();
//     // void disp2();
// }

// // this is parent class
// @Deprecated
// class Plane {
//     public void planeFliesAtGoodHeight() {
//         System.out.println("Plane is flying");
//     }
// }

// // this is child class or subclass
// class CargoPlane extends Plane {
//     // overriden method from parent class
//     @Override
//     public void planeFliesAtGoodHeight() {
//         System.out.println("cargoplane flies low");
//     }
// }

// public class AnnotationDemo {
//     public static void main(String[] args) {
//         // object of child classa
//         Plane cp = new CargoPlane();
//         cp.planeFliesAtGoodHeight();
//     }
// }

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@interface CricketPlayer {
    String country() default "India";

    int age() default 34;
}

@CricketPlayer
class Virat {
    @CricketPlayer
    private int innings;
    private int runs;

    @CricketPlayer
    public int getInnings() {
        return innings;
    }

    public void setInnings(int innings) {
        this.innings = innings;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }
}

public class CustomAnnotationDemo {
    public static void main(String[] args) {

        Virat v = new Virat();
        v.setInnings(300);
        v.setRuns(20000);

        System.out.println("Innings- " + v.getInnings());
        System.out.println("Runs- " + v.getRuns());

        Class c = v.getClass();
        Annotation a = c.getAnnotation(CricketPlayer.class);
        CricketPlayer cp = (CricketPlayer) a;

        String country = cp.country();
        System.out.println("Country- " + country);
        int age = cp.age();
        System.out.println("Age- " + age);
    }
}
// Output
// Innings- 300
// Runs- 20000
// Country- India
// Age- 34