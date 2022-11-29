package pgdp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoopTest {
    // Note: This test has a lot of false positives (e.g. using the word "for"
    // in a method name or a comment). The test should just warn you if you use
    // loops in your solution.
    @DisplayName("Source code should not contain loops because they are not allowed in this exercise")
    @Test
    public void testUsageOfLoops() {
        String[] filePaths = new String[] {
                "src/pgdp/datastructures/lists/RecIntList.java",
                "src/pgdp/datastructures/lists/RecIntListElement.java"
        };

        // Not allowed keywords
        String[] notAllowedKeywords = new String[] { "for", "while", "Stream" };

        for (String path : filePaths) {
            // Methods that are allowed to use loops
            String[] allowedMethods = new String[] {
                    "public static void main(String[] args) {",
                    "public String toString() {",
                    "public String toConnectionString() {"
            };

            // Read RecIntList.java
            String file = readFile(path);

            // Remove methods
            String filteredFile = removeMultipleMethods(file, allowedMethods);

            // Check if there are any loops
            for (String keyword : notAllowedKeywords) {
                assertTrue(!filteredFile.contains(keyword),
                        "You are not allowed to use loops in your solution! Found a loop : " + keyword);
            }
        }
    }

    private String removeMultipleMethods(String file, String[] methods) {
        for (String method : methods) {
            file = removeMethod(file, method);
        }
        return file;
    }

    /**
     * Removes a method from a file
     *
     * The goes throws the file line by line and searches the name of the
     * method. When the method is found, the method is searching for the closing
     * bracket of the method. When the closing bracket is found, the method is
     * removed.
     * 
     * @param file   The file as string
     * @param method The name of the method to remove
     * @return
     */
    private String removeMethod(String file, String method) {
        String[] lines = file.split("\n");
        int start = -1;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(method)) {
                start = i;
                break;
            }
        }

        if (start == -1) {
            // Could not find me
            return file;
        }

        // When the method is found, we search for the closing bracket. However,
        // we need to keep in mind that we can not search for the next closing
        // bracket because this could be the closing bracket of a for loop or
        // what ever.For this we use a stack. When we find a opening bracket, we
        // push it to the stack. When we find a closing bracket, we pop the
        // stack. When the stack is empty, we found the closing bracket of the
        // method.
        Stack<String> brackets = new Stack<String>();

        int end = -1;
        for (int i = start + 1; i < lines.length && end == -1; i++) {
            String[] characters = lines[i].split("");
            for (String c : characters) {
                if (c.equals("{")) {
                    brackets.push(c);
                } else if (c.equals("}")) {
                    if (brackets.isEmpty()) {
                        // When the stack is empty, we know that we found the
                        // matching closing bracket
                        end = i;
                        break;
                    }
                    brackets.pop();
                }
            }
        }

        if (end == -1) {
            // Could not find end
            return file;
        }

        // Build new file without the lines of the removed method.
        String newFile = "";
        for (int i = 0; i < lines.length; i++) {
            if (i < start || i > end) {
                newFile += lines[i] + "\n";
            }
        }

        return newFile;
    }

    private String readFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
