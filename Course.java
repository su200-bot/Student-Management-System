/**
 * Course - represents one course unit offered by the university.
 * Demonstrates: private attributes, overloaded constructors, this keyword,
 * getters and setters, and a toString() method.
 */
public class Course {

    // ----- Attributes (encapsulated: private) -----
    private String courseCode;
    private String courseName;
    private int creditUnits;
    private String lecturer;

    // ----- Constructors (constructor overloading) -----

    /** Default constructor: assigns default values. */
    public Course() {
        this("N/A", "Untitled Course", 0, "Unassigned");
    }

    /** Parameterized constructor: initialises every attribute. */
    public Course(String courseCode, String courseName, int creditUnits, String lecturer) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditUnits = creditUnits;
        this.lecturer = lecturer;
    }

    // ----- Getters -----
    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCreditUnits() {
        return creditUnits;
    }

    public String getLecturer() {
        return lecturer;
    }

    // ----- Setters -----
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /** Credit units must be between 1 and 10; returns true if the value was accepted. */
    public boolean setCreditUnits(int creditUnits) {
        if (creditUnits >= 1 && creditUnits <= 10) {
            this.creditUnits = creditUnits;
            return true;
        }
        return false;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    /** Used by the course drop-down list in the GUI. */
    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
