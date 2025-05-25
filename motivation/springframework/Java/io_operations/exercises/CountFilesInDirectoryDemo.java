import java.io.*;

public class CountFilesInDirectoryDemo {
    public static void main(String[] args) throws IOException {
        File dir = new File("dir_demo");
        System.out.println("dir.isDirectory(): " + dir.isDirectory()); // false
        dir.mkdir();
        System.out.println("created directory dir_demo using mkdir():");
        System.out.println("dir.isDirectory(): " + dir.isDirectory()); // true

        // Create file "skill.txt" in "dir_demo"
        File file = new File(dir, "skill.txt");
        file.createNewFile();
        System.out.println("Created file skill.txt : " + file.isFile());

        // Check number of files in a directory
        int count = 0;
        // String s="IO";
        File f = new File("dir_demo");
        String str[] = f.list();

        for (String name : str) {
            count++;
            System.out.println("File name: " + name);
        }
        System.out.println("No of files in directory dir_demo: " + count);
    }
}
// Output
// dir.isDirectory(): true
// created directory dir_demo using mkdir():
// dir.isDirectory(): true
// Created file skill.txt : true
// File name: skill.txt
// No of files in directory dir_demo: 1