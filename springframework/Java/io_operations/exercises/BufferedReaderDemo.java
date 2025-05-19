import java.io.*;

public class BufferedReaderDemo {
    public static void main(String[] args) throws Exception {
        File dir = new File("dir_demo");
        File file = new File(dir, "skill.txt");

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
    }
}
// Output
// Java
// A
// pwsj