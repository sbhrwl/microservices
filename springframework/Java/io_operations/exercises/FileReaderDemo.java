import java.io.*;

public class FileReaderDemo {
    public static void main(String[] args) throws Exception {
        File dir = new File("dir_demo");
        File file = new File(dir, "skill.txt");

        FileReader fr = new FileReader(file);

        // Read ASCII value
        int i = fr.read();
        System.out.println("Type cast ASCII value to character: " + (char) i);
        while (i != -1) {
            System.out.print(i + "----> ");
            System.out.println((char) i);
            i = fr.read();
        }

        // Store content in character array
        // char ch[] = new char[(int) file.length()];
        // System.out.print("Read character by character from character array");
        // fr.read(ch);
        // for (char data : ch) {
        // System.out.print(data);
        // }
    }
}
// Output
// Type cast ASCII value to character: j
// 106---->j 97---->a 118---->v 97---->a 10---->65---->A 10---->97---->a
// 10---->106---->j 97---->a 118---->v 97---->a 10---->
// Read character by character from character arrayjava
// A
// a
// java