class ObjectCreation {
    int a = 2;
    String name = "John";

    public static void main(String[] args) {
        // declare the variable
        // create the object
        int num = 9;
        ObjectCreation obj1 = new ObjectCreation();
        ObjectCreation obj2 = new ObjectCreation();

        obj1.name = "Navin";

        System.out.println(obj1.a);
        System.out.println(obj1.name);
        System.out.println(obj2.a);
        System.out.println(obj2.name);
    }
}
// Output
// 2
// Navin
// 2
// John