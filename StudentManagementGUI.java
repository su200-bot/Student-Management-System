import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Provides the Swing graphical user interface for the Student Management System.
 * <p>
 * Demonstrates event-driven programming using lambda {@code ActionListener}s,
 * MVC-style separation by delegating data operations to {@link StudentManager},
 * and custom Swing table/form layouts.
 *
 * @author Your Name
 * @version 1.1
 */
public class StudentManagementGUI {

    // ----- Theme Colors & Fonts -----
    private static final Color NAVY = new Color(0x1F, 0x2A, 0x44);
    private static final Color GOLD = new Color(0xC9, 0xA2, 0x27);
    private static final Color PASS_COLOR = new Color(0x1E, 0x7E, 0x34);
    private static final Color FAIL_COLOR = new Color(0xC8, 0x23, 0x33);
    private static final Font BANNER_FONT = new Font("SansSerif", Font.BOLD, 22);

    // ----- Data Controller -----
    private final StudentManager manager;

    // ----- Main Window & Shared Components -----
    private JFrame frame;
    private JLabel statusLabel;

    // ----- Students Tab Components -----
    private JTextField regNoField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField searchField;
    private final JTextField[] markFields = new JTextField[Student.NUM_SUBJECTS];
    private JComboBox<Course> courseBox;
    private DefaultTableModel studentModel;
    private JTable studentTable;

    private String selectedRegNo = null;
    private boolean refreshingTable = false;

    // ----- Courses Tab Components -----
    private JTextField courseCodeField;
    private JTextField courseNameField;
    private JTextField creditField;
    private JTextField lecturerField;
    private DefaultTableModel courseModel;
    private JTable courseTable;

    /**
     * Initializes the manager, loads sample records, builds the UI, and displays the window.
     */
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
        frame.setMinimumSize(new Dimension(950, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Banner
        JLabel banner = new JLabel("  STUDENT MANAGEMENT SYSTEM", JLabel.LEFT);
        banner.setOpaque(true);
        banner.setBackground(NAVY);
        banner.setForeground(GOLD);
        banner.setFont(BANNER_FONT);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        frame.add(banner, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        frame.add(tabs, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        frame.add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ---- Form ----
        regNoField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        courseBox = new JComboBox<>();
        for (int i = 0; i < markFields.length; i++) {
            markFields[i] = new JTextField();
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));

        // Row 0: Reg No, Name, Age
        place(form, new JLabel("Reg. No:"), 0, 0, 1, 0);
        place(form, regNoField, 1, 0, 1, 1);
        place(form, new JLabel("Full Name:"), 2, 0, 1, 0);
        place(form, nameField, 3, 0, 1, 2);
        place(form, new JLabel("Age:"), 4, 0, 1, 0);
        place(form, ageField, 5, 0, 1, 0.5);

        // Row 1: Subject Marks
        for (int i = 0; i < markFields.length; i++) {
            place(form, new JLabel("Mark " + (i + 1) + " (0-100):"), i * 2, 1, 1, 0);
            place(form, markFields[i], i * 2 + 1, 1, 1, 1);
        }

        // Row 2: Course Dropdown
        place(form, new JLabel("Course:"), 0, 2, 1, 0);
        place(form, courseBox, 1, 2, 5, 1);

        // ---- Action Buttons ----
        JButton addBtn = createStyledButton("Add Student", NAVY, Color.WHITE);
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton clearBtn = new JButton("Clear Form");
        JButton statsBtn = new JButton("Statistics");

        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton sortAvgBtn = new JButton("Sort by Average");
        JButton sortNameBtn = new JButton("Sort by Name");
        searchField = new JTextField(16);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row1.add(addBtn);
        row1.add(updateBtn);
        row1.add(deleteBtn);
        row1.add(clearBtn);
        row1.add(statsBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row2.add(new JLabel("Search (Reg No, Name, or Course):"));
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
        String[] columns = {
            "No.", "Reg. No", "Name", "Age", "Course",
            "Mark 1", "Mark 2", "Mark 3", "Total", "Average", "Grade", "Remark", "Status"
        };
        studentModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(studentModel);
        styleTable(studentTable);
        configureStudentTableColumns();
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // ---- Event Listeners ----
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearForm());
        statsBtn.addActionListener(e -> showStatistics());
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

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !refreshingTable) {
                loadSelectedStudentIntoForm();
            }
        });

        return panel;
    }

    // =====================================================================
    //  UI HELPER METHODS
    // =====================================================================

    private void styleTable(JTable table) {
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void configureStudentTableColumns() {
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(11).setPreferredWidth(110);

        // Color-code the Status column (index 12) for PASS / FAIL
        studentTable.getColumnModel().getColumn(12).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    if ("PASS".equalsIgnoreCase(value.toString())) {
                        c.setForeground(PASS_COLOR);
                    } else if ("FAIL".equalsIgnoreCase(value.toString())) {
                        c.setForeground(FAIL_COLOR);
                    } else {
                        c.setForeground(table.getForeground());
                    }
                }
                return c;
            }
        });
    }

    // =====================================================================
    //  COURSES TAB
    // =====================================================================

    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        courseCodeField = new JTextField();
        courseNameField = new JTextField();
        creditField = new JTextField();
        lecturerField = new JTextField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Course Details"));

        place(form, new JLabel("Course Code:"), 0, 0, 1, 0);
        place(form, courseCodeField, 1, 0, 1, 1);
        place(form, new JLabel("Course Name:"), 2, 0, 1, 0);
        place(form, courseNameField, 3, 0, 3, 2);

        place(form, new JLabel("Credits:"), 0, 1, 1, 0);
        place(form, creditField, 1, 1, 1, 1);
        place(form, new JLabel("Lecturer:"), 2, 1, 1, 0);
        place(form, lecturerField, 3, 1, 3, 1);

        JButton addCourseBtn = createStyledButton("Add Course", NAVY, Color.WHITE);
        JButton deleteCourseBtn = new JButton("Delete Course");
        JButton clearCourseBtn = new JButton("Clear Form");

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.add(addCourseBtn);
        row.add(deleteCourseBtn);
        row.add(clearCourseBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(row, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Name", "Credits", "Lecturer", "Enrolled Students"};
        courseModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(courseModel);
        styleTable(courseTable);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        addCourseBtn.addActionListener(e -> addCourse());
        deleteCourseBtn.addActionListener(e -> deleteCourse());
        clearCourseBtn.addActionListener(e -> clearCourseForm());

        return panel;
    }

    private void refreshCourseBox() {
        if (courseBox == null) return;
        courseBox.removeAllItems();
        Course[] courses = manager.getCourses();
        for (Course c : courses) {
            courseBox.addItem(c);
        }
    }

    private void refreshCourseTable() {
        if (courseModel == null) return;
        courseModel.setRowCount(0);
        Course[] courses = manager.getCourses();
        for (Course c : courses) {
            int enrolled = manager.countEnrolled(c.getCourseCode());
            courseModel.addRow(new Object[]{
                c.getCourseCode(),
                c.getCourseName(),
                c.getCreditUnits(),
                c.getLecturer(),
                enrolled
            });
        }
    }

    private void addCourse() {
        try {
            String code = courseCodeField.getText();
            String name = courseNameField.getText();
            int credits = Integer.parseInt(creditField.getText());
            String lecturer = lecturerField.getText();

            Course course = new Course(code, name, credits, lecturer);
            if (manager.addCourse(course)) {
                refreshCourseBox();
                refreshCourseTable();
                setStatus("Course added successfully: " + code);
                clearCourseForm();
            } else {
                JOptionPane.showMessageDialog(frame, "Error: Course code already exists or list is full.", "Add Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number for credit units.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a course to delete from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String code = (String) courseModel.getValueAt(selectedRow, 0);
        if (manager.deleteCourse(code)) {
            refreshCourseBox();
            refreshCourseTable();
            setStatus("Course deleted: " + code);
        } else {
            JOptionPane.showMessageDialog(frame, "Cannot delete course because students are currently enrolled.", "Delete Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCourseForm() {
        courseCodeField.setText("");
        courseNameField.setText("");
        creditField.setText("");
        lecturerField.setText("");
    }

    // =====================================================================
    //  STUDENT ACTIONS & TABLE UPDATES
    // =====================================================================

    private void showStudents(Student[] students) {
        refreshingTable = true;
        studentModel.setRowCount(0);
        for (int i = 0; i < students.length; i++) {
            Student s = students[i];
            double[] marks = s.getMarks();
            studentModel.addRow(new Object[]{
                (i + 1),
                s.getRegNo(),
                s.getName(),
                s.getAge(),
                s.getCourseCode(),
                marks.length > 0 ? marks[0] : 0.0,
                marks.length > 1 ? marks[1] : 0.0,
                marks.length > 2 ? marks[2] : 0.0,
                s.getTotal(),
                String.format("%.1f", s.getAverage()),
                s.getGrade(),
                s.getRemark(),
                s.getStatus()
            });
        }
        refreshingTable = false;
        selectedRegNo = null;
        studentTable.clearSelection();
    }

    private void addStudent() {
        try {
            String regNo = regNoField.getText();
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            Course selectedCourse = (Course) courseBox.getSelectedItem();
            String courseCode = (selectedCourse != null) ? selectedCourse.getCourseCode() : "N/A";

            double[] marks = new double[Student.NUM_SUBJECTS];
            for (int i = 0; i < Student.NUM_SUBJECTS; i++) {
                marks[i] = Double.parseDouble(markFields[i].getText());
            }

            Student s = new Student(regNo, name, age, courseCode, marks);
            if (manager.addStudent(s)) {
                showStudents(manager.getStudents());
                refreshCourseTable();
                setStatus("Student added successfully: " + regNo);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(frame, "Registration number already exists or list is full.", "Add Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for age and marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (selectedRegNo == null) {
            JOptionPane.showMessageDialog(frame, "Please select a student from the table to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String regNo = regNoField.getText();
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            Course selectedCourse = (Course) courseBox.getSelectedItem();
            String courseCode = (selectedCourse != null) ? selectedCourse.getCourseCode() : "N/A";

            double[] marks = new double[Student.NUM_SUBJECTS];
            for (int i = 0; i < Student.NUM_SUBJECTS; i++) {
                marks[i] = Double.parseDouble(markFields[i].getText());
            }

            Student updated = new Student(regNo, name, age, courseCode, marks);
            if (manager.updateStudent(updated)) {
                showStudents(manager.getStudents());
                refreshCourseTable();
                setStatus("Student updated successfully: " + regNo);
            } else {
                JOptionPane.showMessageDialog(frame, "Student record could not be updated.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for age and marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (selectedRegNo == null) {
            JOptionPane.showMessageDialog(frame, "Please select a student from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete student: " + selectedRegNo + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.deleteStudent(selectedRegNo)) {
                showStudents(manager.getStudents());
                refreshCourseTable();
                setStatus("Student deleted: " + selectedRegNo);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(frame, "Student could not be found for deletion.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        regNoField.setText("");
        nameField.setText("");
        ageField.setText("");
        for (JTextField field : markFields) {
            field.setText("");
        }
        if (courseBox.getItemCount() > 0) {
            courseBox.setSelectedIndex(0);
        }
        studentTable.clearSelection();
        selectedRegNo = null;
        regNoField.setEnabled(true);
    }

    private void searchStudents() {
        String query = searchField.getText();
        Student[] results = manager.searchStudents(query);
        showStudents(results);
        setStatus("Found " + results.length + " matching student(s).");
    }

    private void showStatistics() {
        String stats = manager.getStatistics();
        JOptionPane.showMessageDialog(frame, stats, "Class Statistics & Performance Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSelectedStudentIntoForm() {
        int row = studentTable.getSelectedRow();
        if (row == -1) return;
        selectedRegNo = (String) studentModel.getValueAt(row, 1);
        Student s = manager.findStudent(selectedRegNo);
        if (s != null) {
            regNoField.setText(s.getRegNo());
            regNoField.setEnabled(false); // Prevent modifying primary key ID during edit
            nameField.setText(s.getName());
            ageField.setText(String.valueOf(s.getAge()));
            
            double[] marks = s.getMarks();
            for (int i = 0; i < markFields.length; i++) {
                markFields[i].setText(String.valueOf(marks[i]));
            }

            for (int i = 0; i < courseBox.getItemCount(); i++) {
                Course c = courseBox.getItemAt(i);
                if (c.getCourseCode().equalsIgnoreCase(s.getCourseCode())) {
                    courseBox.setSelectedIndex(i);
                    break;
                }
            }
            setStatus("Loaded student: " + s.getName());
        }
    }

    // =====================================================================
    //  LAYOUT HELPERS
    // =====================================================================

    private void place(JPanel panel, Component comp, int x, int y, int width, double weightx) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.weightx = weightx;
        gbc.weighty = 0.0;
        panel.add(comp, gbc);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(" " + message);
        }
    }
}