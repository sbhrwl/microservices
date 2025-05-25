import java.io.*;

public class FileDemo {
    public static void main(String[] args) throws IOException {

        File file = new File("filedemo.txt");
        System.out.println("file.exists(): " + file.exists());// false

        file.createNewFile();
        System.out.println("file.exists():" + file.exists());// true

        File dir = new File("filedemo_dir");
        System.out.println("dir.exists():" + dir.exists()); // false
        dir.mkdir();
        System.out.println("dir.exists():" + dir.exists()); // true
    }
}
// Output
// Run 1
// file.exists(): false, creates a file "filedemo" in base folder "java"
// file.exists():true
// dir.exists():false, creates a directory "filedemo_dir" in base folder "java"
// dir.exists():true
// Run 2
// file.exists(): true
// file.exists():true
// dir.exists():true
// dir.exists():true