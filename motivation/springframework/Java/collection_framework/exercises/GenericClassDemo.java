import java.util.*;

class Gen<T> {
    T obj;

    public Gen(T obj) {
        this.obj = obj;
    }

    void disp() {
        System.out.println("The type of data is : " + obj.getClass().getName());
    }

    public T getObj() {
        return obj;
    }
}

public class GenericClassDemo {
    public static void main(String[] args) {
        Gen<Integer> g = new Gen<Integer>(10);
        g.disp();
        System.out.println("Show value of Gen<Integer> " + g.getObj());

        Gen<String> g1 = new Gen<String>("PW");
        g1.disp();
        System.out.println("Show value of Gen<String> " + g1.getObj());
        // ArrayList<Gen> list1=new ArrayList<Gen>();
        // List<String> list2=new ArrayList<String>();
        // Collection<Integer> list3=new ArrayList<Integer>();
        // // List<Object> list4=new ArrayList<String>(); CE
        // List<Integer> list5=new ArrayList<Integer>();
        // //List<int> list6=new ArrayList<int>();
    }
}
// Output
// The type of data is : java.lang.Integer
// Show value of Gen<Integer> 10
// The type of data is : java.lang.String
// Show value of Gen<String> PW