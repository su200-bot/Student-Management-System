import java.util.Arrays;

/**
 * Manages collections of {@link Student} and {@link Course} objects using fixed-size arrays.
 * <p>
 * Demonstrates core data-structure and algorithm concepts:
 * <ul>
 *   <li>Encapsulation and defensive array trimming ({@link Arrays#copyOf})</li>
 *   <li>CRUD operations (add, linear search, update, shift-delete)</li>
 *   <li>Method overloading ({@code searchStudents}), recursion ({@code sumOfAverages}), and optimized bubble sort</li>
 * </ul>
 *
 * @author LOMER Francis Peter
 * @version 1.1
 */
public class StudentManager {

    // ----- Constants -----
    public static final int MAX_STUDENTS = 100;
    public static final int MAX_COURSES = 20;
    private static final String[] GRADE_LABELS = { "A", "B+", "B", "C+", "C", "D+", "D", "E", "F" };

    // ----- Attributes -----
    private final Student[] students;
    private int studentCount;
    private final Course[] courses;
    private int courseCount;

    /**
     * Initializes empty storage arrays for students and courses.
     */
    public StudentManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        courses = new Course[MAX_COURSES];
        courseCount = 0;
    }

    // =====================================================================
    //  STUDENT OPERATIONS
    // =====================================================================

    public int getStudentCount() {
        return studentCount;
    }

    public boolean isStudentListFull() {
        return studentCount >= MAX_STUDENTS;
    }

    /**
     * Registers a new student if capacity permits and the registration number is unique.
     *
     * @param student The student record to register
     * @return {@code true} if registration succeeds; {@code false} otherwise
     */
    public boolean addStudent(Student student) {
        if (student == null || isStudentListFull() || findStudent(student.getRegNo()) != null) {
            return false;
        }
        students[studentCount++] = student;
        return true;
    }

    /**
     * Finds a student by registration number (case-insensitive).
     *
     * @param regNo The registration number to look up
     * @return Matching {@link Student}, or {@code null} if not found
     */
    public Student findStudent(String regNo) {
        int index = findStudentIndex(regNo);
        return (index == -1) ? null : students[index];
    }

    private int findStudentIndex(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            return -1;
        }
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getRegNo().equalsIgnoreCase(regNo.trim())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Updates an existing student's mutable fields based on their registration number.
     *
     * @param updated Student object carrying the updated details
     * @return {@code true} if the student exists and was updated; {@code false} otherwise
     */
    public boolean updateStudent(Student updated) {
        if (updated == null) {
            return false;
        }
        Student existing = findStudent(updated.getRegNo());
        if (existing == null) {
            return false;
        }
        existing.setName(updated.getName());
        existing.setAge(updated.getAge());
        existing.setCourseCode(updated.getCourseCode());
        existing.setMarks(updated.getMarks());
        return true;
    }

    /**
     * Deletes a student by registration number and shifts subsequent elements left.
     *
     * @param regNo The registration number of the student to remove
     * @return {@code true} if deletion succeeds; {@code false} otherwise
     */
    public boolean deleteStudent(String regNo) {
        int index = findStudentIndex(regNo);
        if (index == -1) {
            return false;
        }
        for (int i = index; i < studentCount - 1; i++) {
            students[i] = students[i + 1];
        }
        students[--studentCount] = null; // Prevent memory leak (loitering reference)
        return true;
    }

    /**
     * Returns an exact-sized copy of all active student records.
     *
     * @return Array containing registered students without trailing {@code null} slots
     */
    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

    /**
     * Searches students whose registration number, name, or course code contains the keyword.
     *
     * @param keyword Search query string
     * @return Array of matching students
     */
    public Student[] searchStudents(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getStudents();
        }
        String key = keyword.trim().toLowerCase();
        Student[] found = new Student[studentCount];
        int n = 0;
        for (int i = 0; i < studentCount; i++) {
            Student s = students[i];
            if (s.getRegNo().toLowerCase().contains(key)
                    || s.getName().toLowerCase().contains(key)
                    || s.getCourseCode().toLowerCase().contains(key)) {
                found[n++] = s;
            }
        }
        return Arrays.copyOf(found, n);
    }

    /**
     * Overloaded search: finds students in a specific course meeting a minimum average.
     *
     * @param courseCode Course code to filter by
     * @param minAverage Minimum required average mark
     * @return Array of matching students
     */
    public Student[] searchStudents(String courseCode, double minAverage) {
        if (courseCode == null || courseCode.isBlank()) {
            return new Student[0];
        }
        Student[] found = new Student[studentCount];
        int n = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getCourseCode().equalsIgnoreCase(courseCode.trim())
                    && students[i].getAverage() >= minAverage) {
                found[n++] = students[i];
            }
        }
        return Arrays.copyOf(found, n);
    }

    /**
     * Sorts students in descending order of their average mark (highest first).
     */
    public void sortByAverage() {
        for (int i = 0; i < studentCount - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getAverage() < students[j + 1].getAverage()) {
                    swapStudents(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // Stop early if already sorted
        }
    }

    /**
     * Sorts students alphabetically by name (A to Z, case-insensitive).
     */
    public void sortByName() {
        for (int i = 0; i < studentCount - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getName().compareToIgnoreCase(students[j + 1].getName()) > 0) {
                    swapStudents(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // Stop early if already sorted
        }
    }

    private void swapStudents(int i, int j) {
        Student temp = students[i];
        students[i] = students[j];
        students[j] = temp;
    }

    // =====================================================================
    //  STATISTICS
    // =====================================================================

    private double sumOfAverages(int index) {
        if (index >= studentCount) {
            return 0;
        }
        return students[index].getAverage() + sumOfAverages(index + 1);
    }

    /**
     * Calculates the overall class average using recursion.
     *
     * @return Class average, or {@code 0.0} if no students exist
     */
    public double getClassAverage() {
        return (studentCount == 0) ? 0.0 : sumOfAverages(0) / studentCount;
    }

    public int countPassed() {
        int passed = 0;
        for (int i = 0; i < studentCount; i++) {
            if ("PASS".equals(students[i].getStatus())) {
                passed++;
            }
        }
        return passed;
    }

    public int countByGrade(String grade) {
        if (grade == null) return 0;
        int count = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getGrade().equalsIgnoreCase(grade)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Builds a formatted summary report of class performance for display in the GUI.
     *
     * @return Multi-line statistics summary string
     */
    public String getStatistics() {
        if (studentCount == 0) {
            return "No students have been registered yet.";
        }
        int best = 0;
        int worst = 0;
        for (int i = 1; i < studentCount; i++) {
            if (students[i].getAverage() > students[best].getAverage()) {
                best = i;
            }
            if (students[i].getAverage() < students[worst].getAverage()) {
                worst = i;
            }
        }

        int passed = countPassed();
        int failed = studentCount - passed;
        double passRate = (passed * 100.0) / studentCount;

        StringBuilder sb = new StringBuilder();
        sb.append("=== CLASS PERFORMANCE SUMMARY ===\n\n");
        sb.append(String.format("Total Students : %d%n", studentCount));
        sb.append(String.format("Class Average  : %.2f%%%n", getClassAverage()));
        sb.append(String.format("Passed / Failed: %d Passed, %d Failed (%.1f%% Pass Rate)%n%n",
                passed, failed, passRate));
        sb.append(String.format("Top Student    : %s (%s) - %.2f%%%n",
                students[best].getName(), students[best].getRegNo(), students[best].getAverage()));
        sb.append(String.format("Lowest Student : %s (%s) - %.2f%%%n%n",
