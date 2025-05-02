import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class GradeManagementGUI extends JFrame{
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Course> courses = new HashMap<>();
    private final JTextArea outputArea = new JTextArea();

    public GradeManagementGUI(){
        configureGUI();
        setupMenu();
    }

    private void configureGUI(){
        setTitle("Student Grade Management System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupMenu(){
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttonLabels = {
            "Add Student", "Add Course", "Enroll Student", 
            "Assign Grade", "Calculate GPA", "Generate Report",
            "Filter Students", "Filter Courses", "Exit"
        };

        for (String label : buttonLabels){
            JButton button = new JButton(label);
            button.addActionListener(this::handleButtonClick);
            menuPanel.add(button);
        }

        add(menuPanel, BorderLayout.WEST);
    }

    private void handleButtonClick(ActionEvent e){
        switch (e.getActionCommand()) {
            case "Add Student" -> addStudent();
            case "Add Course" -> addCourse();
            case "Enroll Student" -> enrollStudent();
            case "Assign Grade" -> assignGrade();
            case "Calculate GPA" -> calculateGPA();
            case "Generate Report" -> generateReport();
            case "Filter Students" -> filterStudents();
            case "Filter Courses" -> filterCourses();
            case "Exit" -> System.exit(0);
        }
    }

    private void addStudent(){
        String id = showInputDialog("Enter Student ID:");
        if (id == null || students.containsKey(id)){
            showError("Invalid or duplicate student ID");
            return;
        }

        String name = showInputDialog("Enter Student Name:");
        if (name == null || name.isBlank()){
            showError("Invalid student name");
            return;
        }

        students.put(id, new Student(id, name));
        outputArea.append("Added student: " + id + " - " + name + "\n");
    }

    private void addCourse(){
        String code = showInputDialog("Enter Course Code:");
        if (code == null || courses.containsKey(code)){
            showError("Invalid or duplicate course code");
            return;
        }

        String name = showInputDialog("Enter Course Name:");
        if (name == null || name.isBlank()) {
            showError("Invalid course name");
            return;
        }

        String credits = showInputDialog("Enter Credit Hours:");
        try {
            int hours = Integer.parseInt(credits);
            courses.put(code, new Course(code, name, hours));
            outputArea.append("Added course: " + code + " - " + name + " (" + hours + " cr)\n");
        } catch (NumberFormatException e) {
            showError("Invalid credit hours");
        }
    }

    private void enrollStudent(){
        if (checkEmptyCollections()) return;

        String studentId = showSelectionDialog("Select Student:", new ArrayList<>(students.keySet()));
        String courseCode = showSelectionDialog("Select Course:", new ArrayList<>(courses.keySet()));

        if (studentId != null && courseCode != null){
            students.get(studentId).enrollCourse(courses.get(courseCode));
            outputArea.append("Enrolled " + studentId + " in " + courseCode + "\n");
        }
    }

    private void assignGrade(){
        if (checkEmptyCollections()) return;

        String studentId = showSelectionDialog("Select Student:", new ArrayList<>(students.keySet()));
        if (studentId == null) return;

        Student student = students.get(studentId);
        List<Course> enrolled = new ArrayList<>(student.getEnrolledCourses().keySet());
        
        if (enrolled.isEmpty()){
            showError("Student not enrolled in any courses");
            return;
        }

        Course course = (Course) JOptionPane.showInputDialog(
            this, "Select Course:", "Assign Grade",
            JOptionPane.QUESTION_MESSAGE, null,
            enrolled.toArray(), enrolled.get(0));

        if (course == null) return;

        String[] grades = {"A", "B", "C", "D", "F"};
        String grade = (String) JOptionPane.showInputDialog(
            this, "Select Grade:", "Assign Grade",
            JOptionPane.QUESTION_MESSAGE, null, grades, grades[0]);

        if (grade != null) {
            student.assignGrade(course, grade);
            outputArea.append("Assigned grade " + grade + " to " + studentId + 
                           " for " + course.getCourseCode() + "\n");
        }
    }

    private void calculateGPA(){
        if (students.isEmpty()){
            showError("No students available");
            return;
        }

        String studentId = showSelectionDialog("Select Student:", new ArrayList<>(students.keySet()));
        if (studentId == null) return;

        double gpa = students.get(studentId).calculateGPA();
        outputArea.append("\nGPA for " + studentId + ": " + String.format("%.2f", gpa) + "\n\n");
    }

    private void generateReport(){
        if (students.isEmpty()){
            showError("No students available");
            return;
        }

        String studentId = showSelectionDialog("Select Student:", new ArrayList<>(students.keySet()));
        if (studentId == null) return;

        outputArea.append("\n" + students.get(studentId).generateReportCard() + "\n\n");
    }

    private void filterStudents(){
        if (students.isEmpty()){
            showError("No students available");
            return;
        }

        String term = showInputDialog("Enter student name to search:");
        if (term == null || term.isBlank()) return;

        outputArea.append("\n=== Students matching '" + term + "' ===\n");
        students.values().stream()
            .filter(s -> s.getName().toLowerCase().contains(term.toLowerCase()))
            .forEach(s -> outputArea.append(s.getStudentId() + " - " + s.getName() + "\n"));
        outputArea.append("============================\n\n");
    }

    private void filterCourses(){
        if (courses.isEmpty()){
            showError("No courses available");
            return;
        }

        String term = showInputDialog("Enter course name to search:");
        if (term == null || term.isBlank()) return;

        outputArea.append("\n=== Courses matching '" + term + "' ===\n");
        courses.values().stream()
            .filter(c -> c.getCourseName().toLowerCase().contains(term.toLowerCase()))
            .forEach(c -> outputArea.append(c.getCourseCode() + " - " + c.getCourseName() + "\n"));
        outputArea.append("============================\n\n");
    }

    private boolean checkEmptyCollections(){
        if (students.isEmpty()){
            showError("No students available");
            return true;
        }
        if (courses.isEmpty()){
            showError("No courses available");
            return true;
        }
        return false;
    }

    private String showInputDialog(String message){
        return JOptionPane.showInputDialog(this, message);
    }

    private String showSelectionDialog(String title, List<String> options){
        return (String) JOptionPane.showInputDialog(
            this, title, "Selection",
            JOptionPane.QUESTION_MESSAGE, null,
            options.toArray(), options.get(0));
    }

    private void showError(String message){
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new GradeManagementGUI().setVisible(true));
    }
}