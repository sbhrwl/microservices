import java.lang.Class;
import java.lang.reflect.*;

// refer Dog.java file
class Main {
    public static void main(String[] args) {
        try {
            // 1. create an object of Dog
            Dog d1 = new Dog();

            // 2. create an object of Class using getClass()
            Class obj = d1.getClass();

            // 3. get name of the class
            String name = obj.getName();
            System.out.println("Name of the class: " + name);

            // 4. get the access modifier of the class
            int modifier = obj.getModifiers();

            // 5. convert the access modifier to string
            String mod = Modifier.toString(modifier);
            System.out.println("Access modifier of the class " + name + ": " + mod);

            // 6. get the superclass of Dog
            Class superClass = obj.getSuperclass();
            System.out.println("Superclass is: " + superClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// Output
// Name of the class: Dog
// Access modifier of the class Dog: public
// Superclass is: Animal