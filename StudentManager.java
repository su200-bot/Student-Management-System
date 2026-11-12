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

    public static final int MAX_STUDENTS = 100;
    public static final int MAX_COURSES = 20;
    private static final String[] GRADE_LABELS = {"A", "B+", "B", "C+", "C", "D+", "D", "E", "F"};

    private final Student[] students;
    private int studentCount;
    private final Course[] courses;
    private int courseCount;

    public StudentManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        courses = new Course[MAX_COURSES];
        courseCount = 0;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public boolean isStudentListFull() {
        return studentCount >= MAX_STUDENTS;
    }

    public boolean isCourseListFull() {
        return courseCount >= MAX_COURSES;
    }

    public boolean addStudent(Student student) {
        if (student == null || isStudentListFull() || findStudent(student.getRegNo()) != null) {
            return false;
        }
        students[studentCount++] = student;
        return true;
    }

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

    public boolean updateStudent(Student updated) {
        if (updated == null || updated.getRegNo() == null || updated.getRegNo().isBlank()) {
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

    public boolean deleteStudent(String regNo) {
        int index = findStudentIndex(regNo);
        if (index == -1) {
            return false;
        }
        for (int i = index; i < studentCount - 1; i++) {
            students[i] = students[i + 1];
        }
        students[--studentCount] = null;
        return true;
    }

    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

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

    public void sortByAverage() {
        for (int i = 0; i < studentCount - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getAverage() < students[j + 1].getAverage()) {
                    swapStudents(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public void sortByName() {
        for (int i = 0; i < studentCount - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getName().compareToIgnoreCase(students[j + 1].getName()) > 0) {
                    swapStudents(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    private void swapStudents(int i, int j) {
        Student temp = students[i];
        students[i] = students[j];
        students[j] = temp;
    }

    // =====================================================================
    //  COURSE OPERATIONS
    // =====================================================================

    public boolean addCourse(Course course) {
        if (course == null || isCourseListFull() || findCourse(course.getCourseCode()) != null) {
            return false;
        }
        courses[courseCount++] = course;
        return true;
    }

    public Course findCourse(String courseCode) {
        int index = findCourseIndex(courseCode);
        return (index == -1) ? null : courses[index];
    }

    private int findCourseIndex(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return -1;
        }
        for (int i = 0; i < courseCount; i++) {
            if (courses[i].getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                return i;
            }
        }
        return -1;
    }

    public boolean updateCourse(Course updated) {
        if (updated == null || updated.getCourseCode() == null || updated.getCourseCode().isBlank()) {
            return false;
        }
        Course existing = findCourse(updated.getCourseCode());
        if (existing == null) {
            return false;
        }
        existing.setCourseName(updated.getCourseName());
        existing.setCreditUnits(updated.getCreditUnits());
        existing.setLecturer(updated.getLecturer());
        return true;
    }

    public boolean deleteCourse(String courseCode) {
        if (countEnrolled(courseCode) > 0) {
            return false;
        }
        int index = findCourseIndex(courseCode);
        if (index == -1) {
            return false;
        }
        for (int i = index; i < courseCount - 1; i++) {
            courses[i] = courses[i + 1];
        }
        courses[--courseCount] = null;
        return true;
    }

    public int countEnrolled(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                count++;
            }
        }
        return count;
    }

    public Course[] getCourses() {
        return Arrays.copyOf(courses, courseCount);
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

    public int countFailed() {
        return studentCount - countPassed();
    }

    public int countByGrade(String grade) {
        if (grade == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getGrade().equalsIgnoreCase(grade)) {
                count++;
            }
        }
        return count;
    }

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
        int failed = countFailed();
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
                students[worst].getName(), students[worst].getRegNo(), students[worst].getAverage()));
        
        sb.append("Grade Distribution:\n");
        for (String label : GRADE_LABELS) {
            sb.append(String.format("   %s : %d%n", label, countByGrade(label)));
        }
        return sb.toString();
    }

    /**
     * Loads sample course and student records for testing and demonstration.
     */
    public void loadSampleData() {
        if (courseCount != 0 || studentCount != 0) {
            return;
        }

        addCourse(new Course("CSC1102", "Structured and Object-Oriented Programming", 4, "Dr. Lillian Muyama"));
        addCourse(new Course("CSC1105", "Mathematics for Computer Science", 4, "To be assigned"));
        addCourse(new Course("BIT1101", "Introduction to Information Technology", 3, "To be assigned"));

        String[] names = {"Namukasa Grace", "Okello Brian", "Nakato Ruth", "Ssebunya David", "Atim Patricia", "Mugisha Joel", "Nabirye Esther", "Kiiza Samuel"};
        String[] codes = {"CSC1102", "CSC1102", "CSC1105", "CSC1105", "BIT1101", "CSC1102", "BIT1101", "CSC1105"};
        double[][] marksArray = {
            {85.0, 78.0, 91.0},
            {62.0, 58.0, 70.0},
            {74.0, 69.0, 80.0},
            {45.0, 52.0, 38.0},
            {90.0, 88.0, 94.0},
            {55.0, 61.0, 49.0},
            {67.0, 72.0, 64.0},
            {30.0, 41.0, 35.0}
        };

        for (int i = 0; i < names.length; i++) {
            String regNo = String.format("25/U/0%d/EVE", 101 + i);
            addStudent(new Student(regNo, names[i], 18 + (i % 4), codes[i], marksArray[i]));
        }
    }
}