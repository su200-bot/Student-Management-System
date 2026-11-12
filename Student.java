import java.util.Arrays;

/**
 * Represents a university student and tracks their performance across three subjects.
 * <p>
 * Demonstrates core Java and OOP concepts:
 * <ul>
 *   <li>Encapsulation using private fields, defensive array copying, and validated setters</li>
 *   <li>Constructor chaining via the {@code this} keyword and method overloading ({@code setMarks})</li>
 *   <li>Control flow using loops, {@code if-else-if} ladders, {@code switch} fall-through, and the ternary operator</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.1
 */
public class Student {

    // ----- Constants -----
    public static final int NUM_SUBJECTS = 3;
    public static final double PASS_MARK = 50.0;
    public static final double MIN_MARK = 0.0;
    public static final double MAX_MARK = 100.0;

    // ----- Attributes -----
    private String regNo;
    private String name;
    private int age;
    private String courseCode;
    private double[] marks;

    // ----- Constructors -----

    /**
     * Default constructor. Initializes attributes with safe placeholder defaults.
     */
    public Student() {
        this("N/A", "Unknown", 18, "N/A", new double[NUM_SUBJECTS]);
    }

    /**
     * Parameterized constructor. Initializes all student attributes with validation.
     *
     * @param regNo      The student's registration number
     * @param name       The student's full name
     * @param age        The student's age (must be between 16 and 100)
     * @param courseCode The code of the enrolled course
     * @param marks      An array of three subject marks (each between 0 and 100)
     */
    public Student(String regNo, String name, int age, String courseCode, double[] marks) {
    setRegNo(regNo);
    setName(name); // Uses validation logic
    setAge(age);   // Uses validation logic
    setCourseCode(courseCode);
    
    this.marks = new double[NUM_SUBJECTS];
    if (!setMarks(marks)) {
        // Fallback or default marks if invalid array passed
        java.util.Arrays.fill(this.marks, 0.0);
    }
}

    // ----- Getters -----

    public String getRegNo() {
        return regNo;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Returns a defensive copy of the marks array to protect internal state.
     *
     * @return A copy of the student's marks
     */
    public double[] getMarks() {
        return Arrays.copyOf(marks, NUM_SUBJECTS);
    }

    // ----- Setters -----

    public boolean setRegNo(String regNo) {
        if (regNo != null && !regNo.isBlank()) {
            this.regNo = regNo.trim();
            return true;
        }
        return false;
    }

    public boolean setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
            return true;
        }
        return false;
    }

    public boolean setAge(int age) {
        if (age >= 16 && age <= 100) {
            this.age = age;
            return true;
        }
        return false;
    }

    public boolean setCourseCode(String courseCode) {
        if (courseCode != null && !courseCode.isBlank()) {
            this.courseCode = courseCode.trim();
            return true;
        }
        return false;
    }

    /**
     * Updates all three subject marks if the array is valid and all scores are within 0–100.
     *
     * @param newMarks Array containing exactly three valid marks
     * @return {@code true} if marks were updated; {@code false} otherwise
     */
    public boolean setMarks(double[] newMarks) {
        if (newMarks == null || newMarks.length != NUM_SUBJECTS) {
            return false;
        }
        for (double mark : newMarks) {
            if (mark < MIN_MARK || mark > MAX_MARK) {
                return false;
            }
        }
        this.marks = Arrays.copyOf(newMarks, NUM_SUBJECTS);
        return true;
    }

    /**
     * Overloaded setter to update marks using three individual arguments.
     *
     * @param mark1 First subject mark
     * @param mark2 Second subject mark
     * @param mark3 Third subject mark
     * @return {@code true} if all marks are valid and updated; {@code false} otherwise
     */
    public boolean setMarks(double mark1, double mark2, double mark3) {
        return setMarks(new double[]{mark1, mark2, mark3});
    }

    // ----- Calculations -----

    /**
     * Calculates the sum of all subject marks.
     *
     * @return Total marks scored
     */
    public double getTotal() {
        double total = 0;
        for (double mark : marks) {
            total += mark;
        }
        return total;
    }

    /**
     * Calculates the arithmetic mean of the subject marks.
     *
     * @return Average mark
     */
    public double getAverage() {
        return getTotal() / NUM_SUBJECTS;
    }

    /**
     * Determines the letter grade using the Makerere University grading scale.
     *
     * @return Letter grade from A to F
     */
    public String getGrade() {
        double avg = getAverage();
        if (avg >= 80) return "A";
        if (avg >= 75) return "B+";
        if (avg >= 70) return "B";
        if (avg >= 65) return "C+";
        if (avg >= 60) return "C";
        if (avg >= 55) return "D+";
        if (avg >= 50) return "D";
        if (avg >= 45) return "E";
        return "F";
    }

    /**
     * Maps the student's letter grade to a descriptive academic remark.
     *
     * @return Performance remark
     */
    public String getRemark() {
        switch (getGrade()) {
            case "A":
                return "Excellent";
            case "B+":
                return "Very Good";
            case "B":
                return "Good";
            case "C+":
            case "C":
                return "Fair";
            case "D+":
            case "D":
                return "Pass";
            case "E":
                return "Weak";
            default:
                return "Fail";
        }
    }

    /**
     * Determines whether the student passed or failed overall.
     *
     * @return {@code "PASS"} if average is at least {@link #PASS_MARK}, otherwise {@code "FAIL"}
     */
    public String getStatus() {
        return (getAverage() >= PASS_MARK) ? "PASS" : "FAIL";
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | Avg: %.1f (%s - %s)",
                regNo, name, courseCode, getAverage(), getGrade(), getStatus());
    }
}
