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
