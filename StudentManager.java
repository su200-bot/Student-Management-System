/**
 * StudentManager - stores and manages all Student and Course objects.
 * Demonstrates: arrays of objects (Student[] and Course[]), loops, method
 * overloading (searchStudents), recursion (sumOfAverages), sorting, encapsulation.
 */
public class StudentManager {

    // ----- Constants -----
    public static final int MAX_STUDENTS = 100;
    public static final int MAX_COURSES = 20;
    private static final String[] GRADE_LABELS = { "A", "B+", "B", "C+", "C", "D+", "D", "E", "F" };

    // ----- Attributes: arrays that hold the objects -----
    private Student[] students;
    private int studentCount;
    private Course[] courses;
    private int courseCount;

    /** Creates empty arrays. */
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

    /** Adds a student. Returns false if the array is full or the registration number already exists. */
    public boolean addStudent(Student student) {
        if (isStudentListFull() || findStudent(student.getRegNo()) != null) {
            return false;
        }
        students[studentCount] = student;
        studentCount++;
        return true;
    }

    /** Finds a student by registration number (ignoring upper/lower case). Returns null if not found. */
    public Student findStudent(String regNo) {
        int index = findStudentIndex(regNo);
        if (index == -1) {
            return null;
        }
        return students[index];
    }

    /** Linear search through the array. Returns the position, or -1 if not found. */
    private int findStudentIndex(String regNo) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getRegNo().equalsIgnoreCase(regNo)) {
                return i;
            }
        }
        return -1;
    }

    /** Replaces the details of the student who has the same registration number. */
    public boolean updateStudent(Student updated) {
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

    /** Deletes a student and shifts the remaining students one place to the left. */
    public boolean deleteStudent(String regNo) {
        int index = findStudentIndex(regNo);
        if (index == -1) {
            return false;
        }
        for (int i = index; i < studentCount - 1; i++) {
            students[i] = students[i + 1];
        }
        studentCount--;
        students[studentCount] = null;
        return true;
    }

    /** Returns a copy of the stored students (without the empty slots). */
    public Student[] getStudents() {
        Student[] copy = new Student[studentCount];
        for (int i = 0; i < studentCount; i++) {
            copy[i] = students[i];
        }
        return copy;
    }

    /** Search 1: students whose registration number, name or course code contains the keyword. */
    public Student[] searchStudents(String keyword) {
        String key = keyword.toLowerCase();
        Student[] found = new Student[studentCount];
        int n = 0;
        for (int i = 0; i < studentCount; i++) {
            Student s = students[i];
            if (s.getRegNo().toLowerCase().contains(key)
                    || s.getName().toLowerCase().contains(key)
                    || s.getCourseCode().toLowerCase().contains(key)) {
                found[n] = s;
                n++;
            }
        }
        return trim(found, n);
    }

    /** Search 2 (overloaded): students enrolled in a course whose average is at least minAverage. */
    public Student[] searchStudents(String courseCode, double minAverage) {
        Student[] found = new Student[studentCount];
        int n = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getCourseCode().equalsIgnoreCase(courseCode)
                    && students[i].getAverage() >= minAverage) {
                found[n] = students[i];
                n++;
            }
        }
        return trim(found, n);
    }

    /** Helper: copies the first n elements of an array into a new array of exactly n elements. */
    private Student[] trim(Student[] source, int n) {
        Student[] result = new Student[n];
        for (int i = 0; i < n; i++) {
            result[i] = source[i];
        }
        return result;
    }

    /** Bubble sort: highest average first. */
    public void sortByAverage() {
        for (int i = 0; i < studentCount - 1; i++) {
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getAverage() < students[j + 1].getAverage()) {
                    Student temp = students[j];
                    students[j] = students[j + 1];
                    students[j + 1] = temp;
                }
            }
        }
    }

    /** Bubble sort: alphabetical order of names (A to Z). */
    public void sortByName() {
        for (int i = 0; i < studentCount - 1; i++) {
            for (int j = 0; j < studentCount - 1 - i; j++) {
                if (students[j].getName().compareToIgnoreCase(students[j + 1].getName()) > 0) {
                    Student temp = students[j];
                    students[j] = students[j + 1];
                    students[j + 1] = temp;
                }
            }
        }
    }

    // =====================================================================
    //  STATISTICS
    // =====================================================================

    /** Recursive method: adds the averages of all students from position index to the end. */
    private double sumOfAverages(int index) {
        if (index >= studentCount) {          // halting condition
            return 0;
        }
        return students[index].getAverage() + sumOfAverages(index + 1);
    }

    /** Average mark of the whole class (0 if there are no students). */
    public double getClassAverage() {
        if (studentCount == 0) {
            return 0;
        }
        return sumOfAverages(0) / studentCount;
    }

    public int countPassed() {
        int passed = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStatus().equals("PASS")) {
                passed++;
            }
        }
        return passed;
    }

    /** How many students have the given letter grade. */
    public int countByGrade(String grade) {
        int count = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getGrade().equals(grade)) {
                count++;
            }
        }
        return count;
    }

    /** Builds a text report of class statistics for the GUI to display. */
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

        StringBuilder report = new StringBuilder();
        report.append("Total students      : ").append(studentCount).append("\n");
        report.append("Class average       : ").append(String.format("%.2f", getClassAverage())).append("\n");
        report.append("Highest average     : ").append(String.format("%.2f", students[best].getAverage()))
                .append("  (").append(students[best].getName()).append(")\n");
        report.append("Lowest average      : ").append(String.format("%.2f", students[worst].getAverage()))
                .append("  (").append(students[worst].getName()).append(")\n");
        report.append("Passed              : ").append(passed).append("\n");
        report.append("Failed              : ").append(studentCount - passed).append("\n\n");
        report.append("Grade distribution:\n");
        for (String grade : GRADE_LABELS) {          // enhanced for loop
            report.append("   ").append(grade).append("  :  ").append(countByGrade(grade)).append("\n");
        }
        return report.toString();
    }

    // =====================================================================
    //  COURSE OPERATIONS
    // =====================================================================

    public int getCourseCount() {
        return courseCount;
    }

    /** Adds a course. Returns false if the array is full or the course code already exists. */
    public boolean addCourse(Course course) {
        if (courseCount >= MAX_COURSES || findCourse(course.getCourseCode()) != null) {
            return false;
        }
        courses[courseCount] = course;
        courseCount++;
        return true;
    }

    public Course findCourse(String courseCode) {
        for (int i = 0; i < courseCount; i++) {
            if (courses[i].getCourseCode().equalsIgnoreCase(courseCode)) {
                return courses[i];
            }
        }
        return null;
    }

    /** Number of students enrolled in a course. */
    public int countEnrolled(String courseCode) {
        int count = 0;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getCourseCode().equalsIgnoreCase(courseCode)) {
                count++;
            }
        }
        return count;
    }

    /** Deletes a course only if no student is enrolled in it. */
    public boolean deleteCourse(String courseCode) {
        if (countEnrolled(courseCode) > 0) {
            return false;
        }
        int index = -1;
        for (int i = 0; i < courseCount; i++) {
            if (courses[i].getCourseCode().equalsIgnoreCase(courseCode)) {
                index = i;
            }
        }
        if (index == -1) {
            return false;
        }
        for (int i = index; i < courseCount - 1; i++) {
            courses[i] = courses[i + 1];
        }
        courseCount--;
        courses[courseCount] = null;
        return true;
    }

    /** Returns a copy of the stored courses (without the empty slots). */
    public Course[] getCourses() {
        Course[] copy = new Course[courseCount];
        for (int i = 0; i < courseCount; i++) {
            copy[i] = courses[i];
        }
        return copy;
    }

    // =====================================================================
    //  SAMPLE DATA (so the system is not empty when it first opens)
    // =====================================================================

    public void loadSampleData() {
        addCourse(new Course("CSC1102", "Structured and Object-Oriented Programming", 4, "Dr. Lillian Muyama"));
        addCourse(new Course("CSC1105", "Mathematics for Computer Science", 4, "To be assigned"));
        addCourse(new Course("BIT1101", "Introduction to Information Technology", 3, "To be assigned"));

        String[] names = { "Namukasa Grace", "Okello Brian", "Nakato Ruth", "Ssebunya David",
                           "Atim Patricia", "Mugisha Joel", "Nabirye Esther", "Kiiza Samuel" };
        String[] codes = { "CSC1102", "CSC1102", "CSC1105", "CSC1105",
                           "BIT1101", "CSC1102", "BIT1101", "CSC1105" };
        double[][] sampleMarks = {
            { 85, 78, 91 }, { 62, 58, 70 }, { 74, 69, 80 }, { 45, 52, 38 },
            { 90, 88, 94 }, { 55, 61, 49 }, { 67, 72, 64 }, { 30, 41, 35 }
        };

        for (int i = 0; i < names.length; i++) {
            String regNo = "25/U/00" + (101 + i) + "/EVE";
            addStudent(new Student(regNo, names[i], 18 + (i % 4), codes[i], sampleMarks[i]));
        }
    }
}
