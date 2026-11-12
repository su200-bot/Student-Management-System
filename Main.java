/*
 * ============================================================================
 *  CSC 1102: Structured and Object-Oriented Programming
 *  PRACTICAL EXAM PROJECT - STUDENT MANAGEMENT SYSTEM
 * ============================================================================
 *  Group Leader : AKELLO SUNDAY OBONYO (24/U/03151/EVE)
 *
 *  Group Members:
 *     1. LOMER FRANCIS PETER   - 25/U/26198/EVE
 *     2. MAYANJA JUDITH        - 25/U/28752/EVE
 *     3. AKELLO SUNDAY OBONYO  - 24/U/03151/EVE   (Group Leader)
 *     4. MADETE HECTOR         - 24/U/25933/EVE
 *     5. ANYINE ELIZABETH      - 25/U/29497/EVE
 *
 *  GitHub Repository : <PASTE YOUR GROUP'S GITHUB LINK HERE>
 *
 *  Description:
 *     A simple Java Swing application that lets a university administrator
 *     register students, record their marks, calculate totals / averages /
 *     grades, manage courses, search and sort students, and view statistics.
 *
 *  Files in this project:
 *     Main.java                   - entry point (this file)
 *     StudentManagementGUI.java   - Swing user interface (event handling)
 *     StudentManager.java         - stores and manages the Student[] and Course[] arrays
 *     Student.java                - entity class: one student
 *     Course.java                 - entity class: one course
 *     Validator.java              - utility class for validating user input
 *
 *  How to compile and run (from the project folder):
 *     javac *.java
 *     java Main
 * ============================================================================
 */
public class Main {

    public static void main(String[] args) {
        // Swing programs should create their windows on the Event Dispatch Thread.
        // The lambda below is the same style used in Lecture 1 (event-driven programming).
        javax.swing.SwingUtilities.invokeLater(() -> new StudentManagementGUI());
    }
}
