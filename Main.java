import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the <b>CSC 1102: Structured and Object-Oriented Programming</b>
 * Practical Exam Project — <i>Student Management System</i>.
 * <p>
 * This Java Swing desktop application enables university administrators to register
 * students, record subject marks, compute totals, averages, and grades, manage courses,
 * search and sort records, and view class performance statistics.
 *
 * <h3>Project Architecture</h3>
 * <ul>
 *   <li>{@link Main} – Application entry point and Swing EDT launcher</li>
 *   <li>{@link StudentManagementGUI} – Swing graphical user interface and event handling</li>
 *   <li>{@link StudentManager} – Data controller managing {@code Student[]} and {@code Course[]} arrays</li>
 *   <li>{@link Student} – Entity class modeling student details and academic calculations</li>
 *   <li>{@link Course} – Entity class modeling university course units</li>
 *   <li>{@link Validator} – Utility class for validating user inputs</li>
 * </ul>
 *
 * <h3>Group Members</h3>
 * <ol>
 *   <li>LOMER FRANCIS PETER (25/U/26198/EVE)</li>
 *   <li>MAYANJA JUDITH (25/U/28752/EVE)</li>
 *   <li>AKELLO SUNDAY OBONYO (24/U/03151/EVE) — <i>Group Leader</i></li>
 *   <li>MADETE HECTOR (24/U/25933/EVE)</li>
 *   <li>ANYINE ELIZABETH (25/U/29497/EVE)</li>
 * </ol>
 *
 * <h3>Build &amp; Run Instructions (JDK 17+)</h3>
 * <pre>
 *   javac *.java
 *   java Main
 * </pre>
 *
 * @author LOMER Francis Peter
 * @author AKELLO Sunday Obonyo (Group Leader)
 * @author MAYANJA Judith
 * @author MADETE Hector
 * @author ANYINE Elizabeth
 * @version 1.1
 * @see <a href="https://github.com/su200-bot/Student-Management-System">GitHub Repository</a>
 */
public class Main {

    /**
     * Launches the Student Management System GUI safely on the Swing
     * Event Dispatch Thread (EDT).
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Apply the operating system's native Look & Feel for a cleaner modern UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to Java's default Metal Look & Feel if unavailable
        }

        // Create and display the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(StudentManagementGUI::new);
    }
}
