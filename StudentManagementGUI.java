import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * StudentManagementGUI - the Swing user interface of the system.
 * Demonstrates: event-driven programming (ActionListeners written as lambdas),
 * creating and using objects of our own classes, and calling their methods.
 * All data is kept in the StudentManager (which stores the arrays).
 */
public class StudentManagementGUI {

    private static final Color NAVY = new Color(0x1F, 0x2A, 0x44);
    private static final Color GOLD = new Color(0xC9, 0xA2, 0x27);

    // ----- The object that stores all the data -----
    private final StudentManager manager;

    // ----- Main window and shared components -----
    private JFrame frame;
    private JLabel statusLabel;

    // ----- Students tab components -----
    private JTextField regNoField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField searchField;
    private JTextField[] markFields = new JTextField[Student.NUM_SUBJECTS];
    private JComboBox<Course> courseBox;
    private DefaultTableModel studentModel;
    private JTable studentTable;

    // ----- Courses tab components -----
    private JTextField courseCodeField;
    private JTextField courseNameField;
    private JTextField creditField;
    private JTextField lecturerField;
    private DefaultTableModel courseModel;
    private JTable courseTable;

    /** Builds the whole interface and shows the window. */
    public StudentManagementGUI() {
        manager = new StudentManager();
        manager.loadSampleData();
        buildInterface();
        refreshCourseBox();
        refreshCourseTable();
        showStudents(manager.getStudents());
        setStatus("Welcome! " + manager.getStudentCount() + " sample students loaded.");
        frame.setVisible(true);
    }

    // =====================================================================
    //  BUILDING THE INTERFACE
    // =====================================================================

    private void buildInterface() {
        frame = new JFrame("Student Management System - CSC 1102 Practical Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Banner
        JLabel banner = new JLabel("  STUDENT MANAGEMENT SYSTEM", JLabel.LEFT);
        banner.setOpaque(true);
        banner.setBackground(NAVY);
        banner.setForeground(GOLD);
        banner.setFont(new Font("SansSerif", Font.BOLD, 22));
        banner.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        frame.add(banner, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        frame.add(tabs, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        frame.add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ---- Form ----
        regNoField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        courseBox = new JComboBox<Course>();
        for (int i = 0; i < markFields.length; i++) {
            markFields[i] = new JTextField();
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student details"));
        // row 0: reg no, name, age
        place(form, new JLabel("Reg. No:"), 0, 0, 1, 0);
        place(form, regNoField, 1, 0, 1, 1);
        place(form, new JLabel("Full name:"), 2, 0, 1, 0);
        place(form, nameField, 3, 0, 1, 2);
        place(form, new JLabel("Age:"), 4, 0, 1, 0);
        place(form, ageField, 5, 0, 1, 0.5);
        // row 1: the three marks
        for (int i = 0; i < markFields.length; i++) {
            place(form, new JLabel("Mark " + (i + 1) + " (0-100):"), i * 2, 1, 1, 0);
            place(form, markFields[i], i * 2 + 1, 1, 1, 1);
        }
        // row 2: course drop-down (wide so the full course name is visible)
        place(form, new JLabel("Course:"), 0, 2, 1, 0);
        place(form, courseBox, 1, 2, 5, 1);

        // ---- Buttons ----
        JButton addBtn = new JButton("Add Student");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton clearBtn = new JButton("Clear Form");
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton sortAvgBtn = new JButton("Sort by Average");
        JButton sortNameBtn = new JButton("Sort by Name");
        JButton statsBtn = new JButton("Statistics");
        searchField = new JTextField(16);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row1.add(addBtn);
        row1.add(updateBtn);
        row1.add(deleteBtn);
        row1.add(clearBtn);
        row1.add(statsBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row2.add(new JLabel("Search (reg no, name or course):"));
        row2.add(searchField);
        row2.add(searchBtn);
        row2.add(showAllBtn);
        row2.add(sortAvgBtn);
        row2.add(sortNameBtn);

        JPanel buttons = new JPanel(new GridLayout(2, 1));
        buttons.add(row1);
        buttons.add(row2);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        // ---- Table ----
        String[] columns = { "No.", "Reg. No", "Name", "Age", "Course", "Mark 1", "Mark 2", "Mark 3",
                             "Total", "Average", "Grade", "Remark", "Status" };
        studentModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(studentModel);
        studentTable.setDefaultEditor(Object.class, null);      // cells cannot be edited by typing
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(24);
        studentTable.getTableHeader().setBackground(NAVY);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(11).setPreferredWidth(110);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // ---- Events ----
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(e -> searchStudents());
        searchField.addActionListener(e -> searchStudents());
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            showStudents(manager.getStudents());
            setStatus("Showing all " + manager.getStudentCount() + " students.");
        });
        sortAvgBtn.addActionListener(e -> {
            manager.sortByAverage();
            showStudents(manager.getStudents());
            setStatus("Students sorted by average (highest first).");
        });
        sortNameBtn.addActionListener(e -> {
            manager.sortByName();
            showStudents(manager.getStudents());
            setStatus("Students sorted alphabetically by name.");
        });
        statsBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(frame, manager.getStatistics(), "Class Statistics",
                    JOptionPane.INFORMATION_MESSAGE));
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });
        return panel;
    }

    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        courseCodeField = new JTextField();
        courseNameField = new JTextField();
        creditField = new JTextField();
        lecturerField = new JTextField();

        JPanel form = new JPanel(new GridLayout(1, 8, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Course details"));
        form.add(new JLabel("Course code:"));
        form.add(courseCodeField);
        form.add(new JLabel("Course name:"));
        form.add(courseNameField);
        form.add(new JLabel("Credit units:"));
        form.add(creditField);
        form.add(new JLabel("Lecturer:"));
        form.add(lecturerField);

        JButton addCourseBtn = new JButton("Add Course");
        JButton deleteCourseBtn = new JButton("Delete Selected Course");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.add(addCourseBtn);
        buttons.add(deleteCourseBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = { "Code", "Course name", "Credit units", "Lecturer", "Students enrolled" };
        courseModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(courseModel);
        courseTable.setDefaultEditor(Object.class, null);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setRowHeight(24);
        courseTable.getTableHeader().setBackground(NAVY);
        courseTable.getTableHeader().setForeground(Color.WHITE);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(360);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        addCourseBtn.addActionListener(e -> addCourse());
        deleteCourseBtn.addActionListener(e -> deleteCourse());
        return panel;
    }

    // =====================================================================
    //  STUDENT ACTIONS
    // =====================================================================

    /** Reads and validates the form. Returns a Student object, or null after showing an error. */
    private Student readStudentForm() {
        String regNo = Validator.cleanText(regNoField.getText()).toUpperCase();
        if (regNo.length() == 0 || regNo.contains(" ")) {
            return showError("Enter a registration number without spaces, e.g. 24/U/03151/EVE.");
        }

        String name = Validator.cleanText(nameField.getText());
        if (!Validator.isValidName(name)) {
            return showError("Enter a valid full name (letters only).");
        }

        String ageText = Validator.cleanText(ageField.getText());
        if (!Validator.isWholeNumber(ageText, 3) || !Validator.isInRange(Integer.parseInt(ageText), 16, 100)) {
            return showError("Age must be a whole number between 16 and 100.");
        }

        Course course = (Course) courseBox.getSelectedItem();
        if (course == null) {
            return showError("Add a course first (Courses tab), then select it.");
        }

        double[] marks = new double[Student.NUM_SUBJECTS];
        for (int i = 0; i < marks.length; i++) {
            String text = Validator.cleanText(markFields[i].getText());
            if (!Validator.isDecimalNumber(text) || !Validator.isInRange(Double.parseDouble(text), 0, 100)) {
                return showError("Mark " + (i + 1) + " must be a number between 0 and 100.");
            }
            marks[i] = Double.parseDouble(text);
        }
        return new Student(regNo, name, Integer.parseInt(ageText), course.getCourseCode(), marks);
    }

    private Student showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Invalid input", JOptionPane.WARNING_MESSAGE);
        return null;
    }

    private void addStudent() {
        Student student = readStudentForm();
        if (student == null) {
            return;
        }
        if (manager.isStudentListFull()) {
            showError("The system is full (" + StudentManager.MAX_STUDENTS + " students).");
        } else if (!manager.addStudent(student)) {
            showError("A student with registration number " + student.getRegNo() + " already exists.");
        } else {
            searchField.setText("");
            showStudents(manager.getStudents());
            refreshCourseTable();
            clearForm();
            setStatus(student.getName() + " was added successfully.");
        }
    }

    private void updateStudent() {
        if (studentTable.getSelectedRow() < 0) {
            showError("Select a student in the table first.");
            return;
        }
        Student student = readStudentForm();
        if (student == null) {
            return;
        }
        if (manager.updateStudent(student)) {
            showStudents(manager.getStudents());
            refreshCourseTable();
            setStatus(student.getName() + " was updated.");
        } else {
            showError("Student not found.");
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showError("Select a student in the table first.");
            return;
        }
        String regNo = studentModel.getValueAt(row, 1).toString();
        int choice = JOptionPane.showConfirmDialog(frame, "Delete student " + regNo + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            manager.deleteStudent(regNo);
            clearForm();
            showStudents(manager.getStudents());
            refreshCourseTable();
            setStatus("Student " + regNo + " was deleted.");
        }
    }

    private void searchStudents() {
        String keyword = Validator.cleanText(searchField.getText());
        if (keyword.length() == 0) {
            showStudents(manager.getStudents());
            setStatus("Showing all " + manager.getStudentCount() + " students.");
            return;
        }
        Student[] results = manager.searchStudents(keyword);
        showStudents(results);
        setStatus(results.length + " student(s) found for \"" + keyword + "\".");
    }

    private void clearForm() {
        regNoField.setText("");
        regNoField.setEditable(true);
        nameField.setText("");
        ageField.setText("");
        for (int i = 0; i < markFields.length; i++) {
            markFields[i].setText("");
        }
        studentTable.clearSelection();
    }

    /** When a table row is clicked, copy that student's details into the form. */
    private void fillFormFromSelectedRow() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Student s = manager.findStudent(studentModel.getValueAt(row, 1).toString());
        if (s == null) {
            return;
        }
        regNoField.setText(s.getRegNo());
        regNoField.setEditable(false);          // the registration number identifies the student
        nameField.setText(s.getName());
        ageField.setText(String.valueOf(s.getAge()));
        double[] marks = s.getMarks();
        for (int i = 0; i < marks.length; i++) {
            markFields[i].setText(format(marks[i]));
        }
        for (int i = 0; i < courseBox.getItemCount(); i++) {
            if (courseBox.getItemAt(i).getCourseCode().equalsIgnoreCase(s.getCourseCode())) {
                courseBox.setSelectedIndex(i);
            }
        }
    }

    /** Fills the students table with the given array of students. */
    private void showStudents(Student[] list) {
        studentModel.setRowCount(0);
        for (int i = 0; i < list.length; i++) {
            Student s = list[i];
            double[] m = s.getMarks();
            studentModel.addRow(new Object[] {
                i + 1, s.getRegNo(), s.getName(), s.getAge(), s.getCourseCode(),
                format(m[0]), format(m[1]), format(m[2]),
                format(s.getTotal()), String.format("%.1f", s.getAverage()),
                s.getGrade(), s.getRemark(), s.getStatus()
            });
        }
    }

    // =====================================================================
    //  COURSE ACTIONS
    // =====================================================================

    private void addCourse() {
        String code = Validator.cleanText(courseCodeField.getText()).toUpperCase();
        String name = Validator.cleanText(courseNameField.getText());
        String creditText = Validator.cleanText(creditField.getText());
        String lecturer = Validator.cleanText(lecturerField.getText());

        if (code.length() == 0 || code.contains(" ")) {
            showError("Enter a course code without spaces, e.g. CSC1102.");
        } else if (name.length() == 0) {
            showError("Enter the course name.");
        } else if (!Validator.isWholeNumber(creditText, 2)
                || !Validator.isInRange(Integer.parseInt(creditText), 1, 10)) {
            showError("Credit units must be a whole number from 1 to 10.");
        } else {
            if (lecturer.length() == 0) {
                lecturer = "To be assigned";
            }
            Course course = new Course(code, name, Integer.parseInt(creditText), lecturer);
            if (manager.addCourse(course)) {
                refreshCourseBox();
                refreshCourseTable();
                courseCodeField.setText("");
                courseNameField.setText("");
                creditField.setText("");
                lecturerField.setText("");
                setStatus("Course " + code + " was added.");
            } else {
                showError("The course list is full or the code " + code + " already exists.");
            }
        }
    }

    private void deleteCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            showError("Select a course in the table first.");
            return;
        }
        String code = courseModel.getValueAt(row, 0).toString();
        if (manager.deleteCourse(code)) {
            refreshCourseBox();
            refreshCourseTable();
            setStatus("Course " + code + " was deleted.");
        } else {
            showError("Course " + code + " cannot be deleted because students are enrolled in it.");
        }
    }

    private void refreshCourseBox() {
        courseBox.removeAllItems();
        Course[] list = manager.getCourses();
        for (int i = 0; i < list.length; i++) {
            courseBox.addItem(list[i]);
        }
    }

    private void refreshCourseTable() {
        courseModel.setRowCount(0);
        Course[] list = manager.getCourses();
        for (int i = 0; i < list.length; i++) {
            Course c = list[i];
            courseModel.addRow(new Object[] {
                c.getCourseCode(), c.getCourseName(), c.getCreditUnits(), c.getLecturer(),
                manager.countEnrolled(c.getCourseCode())
            });
        }
    }

    // =====================================================================
    //  SMALL HELPERS
    // =====================================================================

    /** Places a component in a GridBagLayout cell (column x, row y, spanning w columns). */
    private void place(JPanel panel, Component c, int x, int y, int w, double weightX) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = w;
        g.weightx = weightX;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        panel.add(c, g);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /** Shows 85.0 as "85" and 72.5 as "72.5". */
    private String format(double value) {
        return (value == (long) value) ? String.valueOf((long) value) : String.valueOf(value);
    }
}
