import java.io.*;

public class FileWriterDemo {
    public static void main(String[] args) throws IOException {

        File dir = new File("dir_demo");
        File file = new File(dir, "skill.txt");
        // file.createNewFile();

        // over write a file using append: true
        FileWriter fw = new FileWriter(file, true);
        fw.write("java");
        fw.write("\n");
        fw.write(65); // ASCII A
        fw.write("\n");
        fw.write(97); // ASCII a
        fw.write("\n");
        char ch[] = { 'j', 'a', 'v', 'a' };
        fw.write(ch);
        fw.write("\n");

        // Flush and close
        fw.flush();
        // fw.close();

        System.out.println("open skill.txt to see result");
    }
}
// Open skill.txt
// java
// A
// a
// java