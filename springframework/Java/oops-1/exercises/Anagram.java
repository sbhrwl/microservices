import java.util.Arrays;

public class Anagram {
    public static void main(String[] args) {
        // String str1="School Master";
        // String str2="The Classroom Java";
        String str1 = "keep";
        String str2 = "peek";

        // Step 1: Remove spaces
        // str1=str1.replace(" ", "");
        // str2=str2.replace(" ", "");

        // Step 2: Change to lower case
        // str1=str1.toLowerCase();
        // str2=str2.toLowerCase();

        // Step 3: Convert string to array
        char[] ar1 = str1.toCharArray();
        char[] ar2 = str2.toCharArray();

        // Step 4: Sort the array of characters
        Arrays.sort(ar1);
        Arrays.sort(ar2);

        // Step 5: Compare arrays
        if (Arrays.equals(ar1, ar2)) {
            System.out.println("It's an Anagram");
        } else {
            System.out.println("Its not an Anagram");
        }
    }
}
// Output
// It's an Anagram