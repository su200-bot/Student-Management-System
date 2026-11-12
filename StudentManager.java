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

    public int countFailed() {
        return studentCount - countPassed();
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

        for (String grade : GRADE_LABELS) {
            int gradeCount = countByGrade(grade);
            if (gradeCount > 0) {
                sb.append(String.format("%s : %d%n", grade, gradeCount));
            }
        }

        return sb.toString();
    }

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

    public Course[] getCourses() {
        return Arrays.copyOf(courses, courseCount);
    }

    public void loadSampleData() {
        if (courseCount != 0 || studentCount != 0) {
            return;
        }

        addCourse(new Course("CS101", "Introduction to Programming", 3, "Dr. Aisha Kato"));
        addCourse(new Course("CS102", "Data Structures", 3, "Prof. Ocen"));
        addCourse(new Course("CS103", "Discrete Mathematics", 3, "Dr. Nandutu"));
        addCourse(new Course("CS104", "Object Oriented Programming", 4, "Mr. Kintu"));

        addStudent(new Student("U/2024/101", "Alice Nakate", 20, "CS101", new double[]{78, 82, 88}));
        addStudent(new Student("U/2024/102", "Brian Okello", 21, "CS102", new double[]{68, 74, 83}));
        addStudent(new Student("U/2024/103", "Cynthia Atim", 19, "CS103", new double[]{52, 60, 58}));
        addStudent(new Student("U/2024/104", "Daniel Kizza", 22, "CS104", new double[]{85, 90, 93}));
    }
}
