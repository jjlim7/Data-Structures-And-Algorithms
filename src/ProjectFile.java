import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.control.Alert;

/**
 *
 * @author LIM JING JIE
 */
public class ProjectFile {

    private File projectFile; //tells the directory of the file
    private File serializedFile;
    //private ArrayList<Project> projects; //object containing all projects in an ArrayList
    
    public ProjectFile(File projectFile, File serializedFile) {
        this.projectFile = projectFile;
        this.serializedFile = serializedFile;
    }
    
    public ProjectFile(File serializedFile) {
        this.serializedFile = serializedFile;
    }
    
    //Reads the file for projects.txt
    public ArrayList<Project> readFile() {
        ArrayList<Student> studentList;
        int noOfStudents = 1; int noOfProjects = 0;
        int count = 1;
        ArrayList<Project> projects = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new FileReader(projectFile));
            while (scan.hasNextLine()) {
                studentList = new ArrayList<>();
                String line;

                //Check if its the first line of the text file
                //Which returns the number of projects or extra lines in the text file
                if (count == 1) {
                    line = scan.nextLine();
                    noOfProjects = Integer.parseInt(line);
                }

                line = scan.nextLine();
                
                //Gets an array of String of Project info
                //Splits info into Project Info and a String of student list array
                String[] projectInfo = line.split(",", 5);
                noOfStudents = Integer.parseInt(projectInfo[3]);
                String[] studentInfo = projectInfo[4].split(","); //Get array of list of student info

                Student student;

                for (int k = 0; k < studentInfo.length; k+=4) {
                    //new Student(AdminNo, Name, Course, Gender)
                    student = new Student(studentInfo[k], studentInfo[k+1], studentInfo[k+2], studentInfo[k+3].charAt(0));
                    studentList.add(student); //Add new Student to List
                }

                String title = projectInfo[0]; //[0] -> projectTitle
                String school = projectInfo[1]; //[1] -> school
                String supervisor = projectInfo[2]; // [2} -> supervisor
                Project project = new Project(title, school, supervisor, noOfStudents, studentList);

                count++;
                projects.add(project);
            }
        }
        catch(FileNotFoundException e) {
            Alert alertResult = new Alert(Alert.AlertType.WARNING);
            alertResult.setTitle("Projects text file not found");
            alertResult.setContentText("The projects text file is not found. You can create at the Projects Window.");
            alertResult.showAndWait();
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("Projects text file corrupted");
            alertResult.setContentText("The projects text file is corrupted. Please change your directory in the Options Window.");
            alertResult.showAndWait();
            
            return null;
        }
        return projects;
    }

    //Exports the projects to output.txt
    public void exportProjects(Project project) {
        try {
            //FileWriter is to write character data to a file. It also allows appending of a file
            FileWriter fw = new FileWriter("output.txt",true);
            //BufferedWriter allows the efficient writing of single characters, arrays and strings
            BufferedWriter bw = new BufferedWriter(fw);
            //With PrintWriter, it allows us to use the print line command. It Prints formatted representations of objects to a text-output stream
            PrintWriter pw = new PrintWriter(bw);
            pw.println("Title: " + project.getProjectTitle());
            pw.println("School: "+ project.getSchool());
            pw.println("Supervisor: "+project.getSupervisorName());
            pw.print("Students: ");
            
            ArrayList<Student> studentList = project.getStudents();
            for(int i=0; i<studentList.size(); i++)
            {
                pw.print(studentList.get(i).getStudentName());
                if(i!=studentList.size()-1)
                    pw.print(" ==> ");
            }
            
            pw.println("\r\n"+new String(new char[15]).replace('\0', '-'));
            pw.close();
        }
        catch (IOException e) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("Output cannot be saved");
            alertResult.setContentText("The output text file cannot be saved. Ensure that it is not used by programs and can be edited.");
            alertResult.showAndWait();
        }
    }
    
    //Saves the projects to projects.txt (or overwrites the old file)
    public void saveProjects(ArrayList<Project> projects) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(projectFile));
            
            //Prints the number of projects on the first line
            pw.println(projects.size());
            
            //Prints each project info according to the format given
            for(Project p : projects)
            {
                pw.print(p.getProjectTitle()+",");
                pw.print(p.getSchool()+",");
                pw.print(p.getSupervisorName()+",");
                pw.print(p.getNoOfStudents()+",");
                
                //Prints the list of student info for each project accordingly
                ArrayList<Student> studentList = p.getStudents();
                for(int i=0;i<studentList.size();i++)
                {
                    pw.print(studentList.get(i).getAdminNo()+",");
                    pw.print(studentList.get(i).getStudentName()+",");
                    pw.print(studentList.get(i).getCourse()+",");
                    pw.print(studentList.get(i).getGender());
                    
                    //Checks if its the last student in the list
                    //If its the last student, it stops printing
                    if(i != studentList.size()-1)
                        pw.print(",");
                }
                pw.println();
            }
            
            pw.close();
        } catch (FileNotFoundException ex) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("Projects file cannot be saved");
            alertResult.setContentText("The projects file cannot be saved.");
            alertResult.showAndWait();
        }
    }
    
    //Reads the serialized file for events.ser
    public EventCollection readSerialized() {
        EventCollection events;
        try {
            ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(serializedFile));
            events = (EventCollection)inStream.readObject();
            inStream.close();
            return events;
        } catch(FileNotFoundException e) {
            Alert alertResult = new Alert(Alert.AlertType.WARNING);
            alertResult.setTitle("events.ser not found");
            alertResult.setContentText("The file 'events.ser' is not found. Please create one in the events Window");
            alertResult.showAndWait();
            events = new EventCollection();
            return events;
        } catch (IOException | ClassNotFoundException e) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("events.ser is corrupted");
            alertResult.setContentText("The file 'events.ser' is corrupted. Please change your directory in the Options Window.");
            alertResult.showAndWait();
            return null;
        }
    }
    
    //Saves the serialized to events.ser (or overwrites the old file)
    public void updateSerialized(EventCollection eventCollection) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(serializedFile));
            outStream.writeObject(eventCollection);
            outStream.close();
        } catch (FileNotFoundException e) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("Projects file cannot be saved.");
            alertResult.setContentText("The projects file cannot be saved.");
            alertResult.showAndWait();
        } catch (IOException e) {
            Alert alertResult = new Alert(Alert.AlertType.ERROR);
            alertResult.setTitle("Events cannot be saved");
            alertResult.setContentText("The Events serializable file cannot be saved. Ensure that it is not used by programs and can be edited.");
            alertResult.showAndWait();
        }
    }

    //When the user wishes to search the project based on title
    public Project searchByTitle(String search, ArrayList<Project> projectList) {
        for (Project project: projectList) {
            if (project.getProjectTitle().equalsIgnoreCase(search))
                return project;
        }
        return null;
    }
    
}
