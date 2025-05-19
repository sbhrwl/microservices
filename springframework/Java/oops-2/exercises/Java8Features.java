interface interfaceA {
    void methodShow();

    default void config() {
        System.out.println("in interface implementation of method config of interfaceA");
    }

    static void methodDisplay() {
        System.out.println("in interface implementation of method display of interfaceA");
    }
}

class showOrDisplay implements interfaceA {
    public void methodShow() {
        System.out.println("in class implementation of method show of interfaceA");
    }
}

public class Java8Features {
    public static void main(String[] args) {
        interfaceA.methodDisplay();
        interfaceA obj = new showOrDisplay();
        obj.config();
    }
}
// Output
// in interface implementation of method display of interfaceA
// in interface implementation of method config of interfaceA