import java.io.*;

public class BufferedWriterDemo {
    public static void main(String[] args) throws Exception {
        File dir = new File("dir_demo");
        File file = new File(dir, "skill.txt");

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("Java");
        bw.newLine();
        bw.write(65);
        bw.newLine();
        char ch[] = { 'p', 'w', 's', 'j' };
        bw.write(ch);

        bw.flush();
        bw.close();
    }
}
// Output
// Open skill.txt
// Java
// A
// pwsj