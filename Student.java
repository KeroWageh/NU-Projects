import java.util.HashMap;
import java.util.Map;

public class Student{
    private String studentId;
    private String name;
    private Map<Course, String> enrolledCourses;

    public Student(String studentId, String name){
        this.studentId = studentId;
        this.name = name;
        this.enrolledCourses = new HashMap<>();
    }

    public String getStudentId(){
        return studentId;
    }

    public String getName(){
        return name;
    }

    public void enrollCourse(Course course){
        if (!enrolledCourses.containsKey(course)){
            enrolledCourses.put(course, null);
        }
    }

    public void assignGrade(Course course, String grade){
        if (enrolledCourses.containsKey(course)){
            enrolledCourses.put(course, grade.toUpperCase());
        }
    }

    public Map<Course, String> getEnrolledCourses(){
        return new HashMap<>(enrolledCourses);
    }

    public double calculateGPA(){
        if (enrolledCourses.isEmpty()){
            return 0.0;
        }

        double totalQualityPoints = 0;
        int totalCreditHours = 0;

        for (Map.Entry<Course, String> entry : enrolledCourses.entrySet()){
            Course course = entry.getKey();
            String grade = entry.getValue();

            if (grade != null){
                double gradePoints = convertLetterGradeToPoints(grade);
                totalQualityPoints += gradePoints * course.getCreditHours();
                totalCreditHours += course.getCreditHours();
            }
        }

        return totalCreditHours > 0 ? totalQualityPoints / totalCreditHours : 0.0;
    }

    private double convertLetterGradeToPoints(String grade){
        switch (grade.toUpperCase()) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }

    public String generateReportCard(){
        StringBuilder reportCard = new StringBuilder();
        reportCard.append("=== REPORT CARD ===\n");
        reportCard.append("Student ID: ").append(studentId).append("\n");
        reportCard.append("Name: ").append(name).append("\n\n");
        reportCard.append("COURSES AND GRADES:\n");

        for (Map.Entry<Course, String> entry : enrolledCourses.entrySet()){
            Course course = entry.getKey();
            String grade = entry.getValue();
            reportCard.append(String.format("%-8s", course.getCourseCode()))
                     .append(" - ")
                     .append(String.format("%-30s", course.getCourseName()))
                     .append(" (").append(course.getCreditHours()).append(" cr) - Grade: ")
                     .append(grade != null ? grade : "Pending").append("\n");
        }

        reportCard.append("\nGPA: ").append(String.format("%.2f", calculateGPA()));
        reportCard.append("\n===================");
        return reportCard.toString();
    }
}