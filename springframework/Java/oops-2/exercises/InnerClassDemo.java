
class outerClass {

    public void show() {
        System.out.println("in outer class method");
    }

    static class innerClass {
        public void display() {
            System.out.println("in inner class method");
        }
    }
}

public class InnerClassDemo {
    public static void main(String[] args) {
        outerClass outerClassObj = new outerClass();
        outerClassObj.show();

        outerClass.innerClass innerClassObj = new outerClass.innerClass();
        innerClassObj.display();
    }
}
// Output
// in outer class method
// in inner class method