import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Provides the Swing graphical user interface for the Student Management System.
 */
public class StudentManagementGUI {

    private static final Color NAVY = new Color(0x1F, 0x2A, 0x44);
    private static final Color GOLD = new Color(0xC9, 0xA2, 0x27);
    private static final Font BANNER_FONT = new Font("SansSerif", Font.BOLD, 22);

    private final StudentManager manager;
    private JFrame frame;
    private JLabel statusLabel;

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

    private JTextField courseCodeField;
    private JTextField courseNameField;
    private JTextField creditField;
    private JTextField lecturerField;
    private DefaultTableModel courseModel;
    private JTable courseTable;

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

    private void buildInterface() {
        frame = new JFrame("Student Management System - CSC 1102 Practical Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 700);
        frame.setMinimumSize(new Dimension(950, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel banner = new JLabel("  STUDENT MANAGEMENT SYSTEM", JLabel.LEFT);
        banner.setOpaque(true);
        banner.setBackground(NAVY);
        banner.setForeground(GOLD);
        banner.setFont(BANNER_FONT);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        frame.add(banner, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        frame.add(tabs, BorderLayout.CENTER);

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

        regNoField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        courseBox = new JComboBox<>();
        for (int i = 0; i < markFields.length; i++) {
            markFields[i] = new JTextField();
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));

        place(form, new JLabel("Reg. No:"), 0, 0, 1, 0);
        place(form, regNoField, 1, 0, 1, 1);
        place(form, new JLabel("Full Name:"), 2, 0, 1, 0);
        place(form, nameField, 3, 0, 1, 2);
        place(form, new JLabel("Age:"), 4, 0, 1, 0);
        place(form, ageField, 5, 0, 1, 0.5);

        for (int i = 0; i < markFields.length; i++) {
            place(form, new JLabel("Mark " + (i + 1) + " (0-100):"), i * 2, 1, 1, 0);
            place(form, markFields[i], i * 2 + 1, 1, 1, 1);
        }

        place(form, new JLabel("Course:"), 0, 2, 1, 0);
        place(form, courseBox, 1, 2, 5, 1);

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

        String[] columns = {"No.", "Reg. No", "Name", "Age", "Course", "Mark 1", "Mark 2", "Mark 3", "Total", "Average", "Grade", "Remark", "Status"};
        studentModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(studentModel);
        styleTable(studentTable);
        configureStudentTableColumns();
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

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

    private JPanel buildCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
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
        place(form, courseNameField, 3, 0, 1, 2);
        place(form, new JLabel("Credits:"), 4, 0, 1, 0);
        place(form, creditField, 5, 0, 1, 0.5);
        place(form, new JLabel("Lecturer:"), 0, 1, 1, 0);
        place(form, lecturerField, 1, 1, 5, 1);

        JButton addCourseBtn = createStyledButton("Add Course", NAVY, Color.WHITE);
        JButton updateCourseBtn = new JButton("Update Selected");
        JButton deleteCourseBtn = new JButton("Delete Selected");
        JButton clearCourseBtn = new JButton("Clear Form");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actions.add(addCourseBtn);
        actions.add(updateCourseBtn);
        actions.add(deleteCourseBtn);
        actions.add(clearCourseBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.NORTH);
        top.add(actions, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = {"Code", "Course Name", "Credits", "Lecturer"};
        courseModel = new DefaultTableModel(columns, 0);
        courseTable = new JTable(courseModel);
        styleTable(courseTable);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        addCourseBtn.addActionListener(e -> addCourse());
        updateCourseBtn.addActionListener(e -> updateCourse());
        deleteCourseBtn.addActionListener(e -> deleteCourse());
        clearCourseBtn.addActionListener(e -> clearCourseForm());

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCourseIntoForm();
            }
        });

        return panel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        return btn;
    }

    private void place(JPanel panel, Component component, int x, int y, int width, double weightX) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = weightX;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        panel.add(component, gbc);
    }

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

        studentTable.getColumnModel().getColumn(12).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    if ("PASS".equals(value.toString())) {
                        setForeground(new Color(0x1E, 0x7E, 0x34));
                    } else {
                        setForeground(new Color(0xC8, 0x23, 0x33));
                    }
                }
                return c;
            }
        });
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void refreshCourseBox() {
        if (courseBox == null) {
            return;
        }
        courseBox.removeAllItems();
        for (Course course : manager.getCourses()) {
            courseBox.addItem(course);
        }
        if (courseBox.getItemCount() > 0) {
            courseBox.setSelectedIndex(0);
        }
    }

    private void refreshCourseTable() {
        if (courseModel == null) {
            return;
        }
        courseModel.setRowCount(0);
        for (Course course : manager.getCourses()) {
            courseModel.addRow(new Object[] {
                course.getCourseCode(),
                course.getCourseName(),
                course.getCreditUnits(),
                course.getLecturer()
            });
        }
    }

    private void clearForm() {
        regNoField.setText("");
        nameField.setText("");
        ageField.setText("");
        for (JTextField field : markFields) {
            field.setText("");
        }
        selectedRegNo = null;
        refreshCourseBox();
        studentTable.clearSelection();
        setStatus("Form cleared.");
    }

    private void clearCourseForm() {
        courseCodeField.setText("");
        courseNameField.setText("");
        creditField.setText("");
        lecturerField.setText("");
        courseTable.clearSelection();
        setStatus("Course form cleared.");
    }

    private void showStudents(Student[] students) {
        refreshingTable = true;
        studentModel.setRowCount(0);
        for (int i = 0; i < students.length; i++) {
            Student s = students[i];
            double[] marks = s.getMarks();
            studentModel.addRow(new Object[] {
                i + 1,
                s.getRegNo(),
                s.getName(),
                s.getAge(),
                s.getCourseCode(),
                marks[0],
                marks[1],
                marks[2],
                s.getTotal(),
                s.getAverage(),
                s.getGrade(),
                s.getRemark(),
                s.getStatus()
            });
        }
        refreshingTable = false;
    }

    private void addStudent() {
        Student student = buildStudentFromForm();
        if (student == null) {
            return;
        }
        if (manager.addStudent(student)) {
            showStudents(manager.getStudents());
            refreshCourseBox();
            clearForm();
            setStatus("Student added successfully.");
        } else {
            setStatus("Unable to add student. Check registration number or capacity.");
        }
    }

    private void updateStudent() {
        if (selectedRegNo == null || selectedRegNo.isBlank()) {
            setStatus("Select a student from the table to update.");
            return;
        }

        Student updated = buildStudentFromForm();
        if (updated == null) {
            return;
        }
        updated.setRegNo(selectedRegNo);
        if (manager.updateStudent(updated)) {
            showStudents(manager.getStudents());
            setStatus("Student updated successfully.");
        } else {
            setStatus("Could not update the selected student.");
        }
    }

    private void deleteStudent() {
        if (selectedRegNo == null || selectedRegNo.isBlank()) {
            setStatus("Select a student to delete.");
            return;
        }
        if (manager.deleteStudent(selectedRegNo)) {
            showStudents(manager.getStudents());
            clearForm();
            setStatus("Student deleted.");
        } else {
            setStatus("Selected student could not be deleted.");
        }
    }

    private void searchStudents() {
        String keyword = searchField.getText();
        Student[] matches = manager.searchStudents(keyword);
        showStudents(matches);
        setStatus("Search results: " + matches.length + " student(s) found.");
    }

    private void loadSelectedStudentIntoForm() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        String regNo = studentModel.getValueAt(row, 1).toString();
        Student student = manager.findStudent(regNo);
        if (student == null) {
            return;
        }
        selectedRegNo = regNo;
        regNoField.setText(student.getRegNo());
        nameField.setText(student.getName());
        ageField.setText(String.valueOf(student.getAge()));
        double[] marks = student.getMarks();
        for (int i = 0; i < markFields.length; i++) {
            markFields[i].setText(String.valueOf(marks[i]));
        }
        for (int i = 0; i < courseBox.getItemCount(); i++) {
            Course course = courseBox.getItemAt(i);
            if (course.getCourseCode().equalsIgnoreCase(student.getCourseCode())) {
                courseBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(frame, manager.getStatistics(), "Class Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private Student buildStudentFromForm() {
        String regNo = Validator.cleanText(regNoField.getText());
        String name = Validator.cleanText(nameField.getText());
        String ageText = Validator.cleanText(ageField.getText());

        if (regNo.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
            setStatus("Please fill in Reg. No, Name, and Age.");
            return null;
        }

        if (!Validator.isWholeNumber(ageText, 3)) {
            setStatus("Age must be a whole number between 16 and 100.");
            return null;
        }

        int age = Integer.parseInt(ageText);
        if (age < 16 || age > 100) {
            setStatus("Age must be between 16 and 100.");
            return null;
        }

        if (!Validator.isValidName(name)) {
            setStatus("Name contains invalid characters.");
            return null;
        }

        double[] marks = new double[Student.NUM_SUBJECTS];
        for (int i = 0; i < markFields.length; i++) {
            String raw = Validator.cleanText(markFields[i].getText());
            if (!Validator.isDecimalNumber(raw)) {
                setStatus("Mark " + (i + 1) + " must be a valid number between 0 and 100.");
                return null;
            }
            double mark = Double.parseDouble(raw);
            if (mark < Student.MIN_MARK || mark > Student.MAX_MARK) {
                setStatus("Each mark must be between 0 and 100.");
                return null;
            }
            marks[i] = mark;
        }

        Course selectedCourse = (Course) courseBox.getSelectedItem();
        String courseCode = (selectedCourse == null) ? "N/A" : selectedCourse.getCourseCode();
        return new Student(regNo, name, age, courseCode, marks);
    }

    private void addCourse() {
        String courseCode = Validator.cleanText(courseCodeField.getText());
        String courseName = Validator.cleanText(courseNameField.getText());
        String creditText = Validator.cleanText(creditField.getText());
        String lecturer = Validator.cleanText(lecturerField.getText());

        if (courseCode.isEmpty() || courseName.isEmpty() || creditText.isEmpty() || lecturer.isEmpty()) {
            setStatus("Please complete all course fields.");
            return;
        }

        if (!Validator.isWholeNumber(creditText, 2)) {
            setStatus("Credit units must be a whole number between 1 and 10.");
            return;
        }

        int credits = Integer.parseInt(creditText);
        if (credits < 1 || credits > 10) {
            setStatus("Credit units must be between 1 and 10.");
            return;
        }

        Course course = new Course(courseCode, courseName, credits, lecturer);
        if (manager.addCourse(course)) {
            refreshCourseBox();
            refreshCourseTable();
            clearCourseForm();
            setStatus("Course added successfully.");
        } else {
            setStatus("Course code already exists or the list is full.");
        }
    }

    private void updateCourse() {
        String courseCode = Validator.cleanText(courseCodeField.getText());
        if (courseCode.isEmpty()) {
            setStatus("Select a course first to update.");
            return;
        }

        Course course = new Course(
                courseCode,
                Validator.cleanText(courseNameField.getText()),
                Integer.parseInt(Validator.cleanText(creditField.getText())),
                Validator.cleanText(lecturerField.getText())
        );

        if (manager.updateCourse(course)) {
            refreshCourseBox();
            refreshCourseTable();
            setStatus("Course updated successfully.");
        } else {
            setStatus("Unable to update the selected course.");
        }
    }

    private void deleteCourse() {
        String courseCode = Validator.cleanText(courseCodeField.getText());
        if (courseCode.isEmpty()) {
            setStatus("Select a course to delete.");
            return;
        }
        if (manager.deleteCourse(courseCode)) {
            refreshCourseBox();
            refreshCourseTable();
            clearCourseForm();
            setStatus("Course deleted.");
        } else {
            setStatus("Course could not be deleted.");
        }
    }

    private void loadSelectedCourseIntoForm() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        String code = courseModel.getValueAt(row, 0).toString();
        Course course = manager.findCourse(code);
        if (course == null) {
            return;
        }
        courseCodeField.setText(course.getCourseCode());
        courseNameField.setText(course.getCourseName());
        creditField.setText(String.valueOf(course.getCreditUnits()));
        lecturerField.setText(course.getLecturer());
    }
}
