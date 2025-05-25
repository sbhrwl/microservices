public class Pangram {
    public static void main(String[] args) {
        boolean flag = false;
        String str = "THE QUICK ROWN FOX JUMPS OVER LAZY DOG";
        str = str.replace(" ", "");

        // Convert string to array of characters
        char[] ch = str.toCharArray();
        // Create an empty array of length 26 (equivalent to 26 alphabets)
        int ar[] = new int[26];

        // Traverse character array and initilise with ASCII decimal equivalent of
        // alphabets
        for (int i = 0; i < ch.length; i++) {
            ar[ch[i] - 65]++; // ch[i]: alphabet in character array
        }

        // Check if there is a missing aplphabet
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] == 0) {
                System.out.println("Its not pangram");
                flag = true;
            }
        }

        if (flag == false) {
            System.out.println("Its pangram");
        }
    }
}
// Output
// Its not pangram