
interface InterfaceA {
    void methodOfInterfaceA();
}

interface InterfaceX {
    void methodOfInterfaceX();
}

class classToImplementInterfaceMethods implements InterfaceA, InterfaceX {
    public void methodOfInterfaceA() {
        System.out.println("in method of interface A");
    }

    public void methodOfInterfaceX() {
        System.out.println("in method of interface X");
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        InterfaceA obj = new classToImplementInterfaceMethods();
        obj.methodOfInterfaceA();
    }
}
// Output
// in method of interface A