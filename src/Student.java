import java.io.Serializable;

/**
 *
 * @author Index
 */
public class Student implements Serializable {
    
    private String adminNo;
    private String course;
    private String studentName;
    private char gender;
    
    public Student() {}
    
    public Student(String adminNo, String studentName, String course, char gender) {
        this.adminNo = adminNo;
        this.studentName = studentName;
        this.course = course;
        this.gender = gender;
    }
    
    public Student(Student s) {
        this.adminNo=s.adminNo;
        this.course=s.course;
        this.studentName=s.studentName;
        this.gender=s.gender;
    }
    
    public String getAdminNo() {
        return adminNo;
    }

    public void setAdminNo(String adminNo) {
        this.adminNo = adminNo;
    }
    
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
    
}
