 /**
 * Student - represents one student and his/her marks in three subjects.
 * Demonstrates: encapsulation (private attributes + getters/setters),
 * constructor overloading with the this keyword, method overloading (setMarks),
 * an array attribute (marks), loops, if-else-if, switch and the ? : operator.
 */
public class Student {
    // ----- Constants (static final) -----
    public static final int NUM_SUBJECTS = 3;
    public static final double PASS_MARK = 50.0;

    // ----- Attributes (encapsulated: private) -----
    private String regNo;
    private String name;
    private int age;
    private String courseCode;
    private double[] marks;          // marks in the three subjects (0 - 100)
    // ----- Constructors -----

    /** Default constructor: assigns default values (calls the other constructor using this). */
    public Student() {
        this("N/A", "Unknown", 0, "N/A", new double[NUM_SUBJECTS]);
    }

    /** Parameterized constructor: initialises all attributes. */
    public Student(String regNo, String name, int age, String courseCode, double[] marks) {
        this.regNo = regNo;
        this.name = name;
        this.age = age;
        this.courseCode = courseCode;
        this.marks = new double[NUM_SUBJECTS];
        // copy the marks into our own array so nobody outside can change them by accident
        for (int i = 0; i < NUM_SUBJECTS && i < marks.length; i++) {
            this.marks[i] = marks[i];
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

    /** Returns a COPY of the marks array so the original stays protected (encapsulation). */
    public double[] getMarks() {
        double[] copy = new double[NUM_SUBJECTS];
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            copy[i] = marks[i];
        }
        return copy;
    }

    // ----- Setters (the ones that validate return true/false) -----
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public boolean setName(String name) {
        if (name != null && name.trim().length() > 0) {
            this.name = name;
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

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /** Sets all three marks from an array. Every mark must be between 0 and 100. */
    public boolean setMarks(double[] newMarks) {
        if (newMarks == null || newMarks.length != NUM_SUBJECTS) {
            return false;
        }
        for (int i = 0; i < newMarks.length; i++) {
            if (newMarks[i] < 0 || newMarks[i] > 100) {
                return false;
            }
        }
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            marks[i] = newMarks[i];
        }
        return true;
    }

    /** Overloaded version: sets the three marks from three separate values. */
    public boolean setMarks(double mark1, double mark2, double mark3) {
        return setMarks(new double[] { mark1, mark2, mark3 });
    }

    // ----- Calculations -----

    /** Total of the three marks (uses a loop). */
    public double getTotal() {
        double total = 0;
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            total += marks[i];
        }
        return total;
    }

    /** Average of the three marks. */
    public double getAverage() {
        return getTotal() / NUM_SUBJECTS;
    }

    /** Letter grade worked out with an if-else-if ladder (Makerere-style scale). */
    public String getGrade() {
        double avg = getAverage();
        if (avg >= 80) {
            return "A";
        } else if (avg >= 75) {
            return "B+";
        } else if (avg >= 70) {
            return "B";
        } else if (avg >= 65) {
            return "C+";
        } else if (avg >= 60) {
            return "C";
        } else if (avg >= 55) {
            return "D+";
        } else if (avg >= 50) {
            return "D";
        } else if (avg >= 45) {
            return "E";
        } else {
            return "F";
        }
    }

    /** Remark for the grade, worked out with a switch statement (with fall-through). */
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

    /** PASS if the average reaches the pass mark, otherwise FAIL (conditional operator). */
    public String getStatus() {
        return (getAverage() >= PASS_MARK) ? "PASS" : "FAIL";
    }

    @Override
    public String toString() {
        return regNo + " | " + name + " | " + courseCode;
    }
}
