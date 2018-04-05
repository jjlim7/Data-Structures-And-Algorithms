import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Index
 */
public class Project implements Serializable{
    
    private String projectTitle;
    private String school;
    private String supervisorName;
    private int noOfStudents;
    private ArrayList<Student> students;
    
    public Project(String projectTitle, String school, String supervisorName, int noOfStudents, ArrayList<Student> students) {
        this.projectTitle = projectTitle;
        this.school = school;
        this.supervisorName = supervisorName;
        this.noOfStudents = noOfStudents;
        this.students = students;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public int getNoOfStudents() {
        return noOfStudents;
    }

    public void setNoOfStudents(int noOfStudents) {
        this.noOfStudents = noOfStudents;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

}
