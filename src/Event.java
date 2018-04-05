import java.io.Serializable;

public class Event implements Serializable {
    
    private String eventTitle;
    private LinkList projectList;

    public Event(String title) {
        this.eventTitle = title;
        this.projectList = new LinkList();
    }
    
    public Event(String title, LinkList projects) {
        this.eventTitle = title;
        this.projectList = projects;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public int getNoOfProjects() {
        return projectList.getNoOfElement();
    }
    
    public LinkList getProjectList() {
        return projectList;
    }
    
    public void addProject(Project project) {
        projectList.addLast(project);
    }
    
    public void removeProject(Project project) {
        projectList.remove(getProjectNo(project));
    }
    
    public boolean checkIfProjectExists(Project project) {
        return (getProjectNo(project)!=-1);
    }
    
    public int getProjectNo(Project project) {
        for (int i = 0; i < projectList.getNoOfElement(); i++) {
            Project p = (Project) projectList.get(i);
            if (p.getProjectTitle().equals(project.getProjectTitle()))
                return i;
        }
        return -1;
    }
}
