import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

public class EventWindowController implements Initializable {
    
    private ProjectFile serializedFile; //Class used for reading File, updating File and exporting of output
    
    @FXML private TextField textFieldEventTitle;
    @FXML private Button buttonAddEvent, buttonRemoveEvent, buttonUpdateEvent, buttonSort;
    @FXML private ListView listViewEvents;
    @FXML private TableView table;
    
    //Columns used for the TableView table
    private final TableColumn titleCol = new TableColumn("Title");
    private final TableColumn schoolCol = new TableColumn("School");
    private final TableColumn supervisorNameCol = new TableColumn("Supervisor Name");
    private final TableColumn noOfStudentsCol = new TableColumn("No. Of Students");
    
    private EventCollection events;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Gets the preference, which you want to obtain the directory for the events.ser
        //If there is no preference, events.ser will be the default one
        Preferences pref = Preferences.userNodeForPackage(Loader.class);
        String eventDir = pref.get("PREF_EVENTDIR", "events.ser");

        serializedFile = new ProjectFile(new File(eventDir)); //Lists out the file directory
        events = serializedFile.readSerialized(); //Imports the file, getting the EventCollection object in exchange
        //Loads the events in the listView which will display the list of events
        loadListViewEvents();
        
        initializeTable();
        initializeListViewEvents();
    }
    
    private void initializeTable() {
        //Add all the columns in the Student table
        table.getColumns().addAll(titleCol, schoolCol, supervisorNameCol, noOfStudentsCol);
        //Disable column reordering
        table.getColumns().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                c.next();
                if(c.wasReplaced()) {
                    table.getColumns().clear();
                    table.getColumns().addAll(titleCol, schoolCol, supervisorNameCol, noOfStudentsCol);
                }
            }
        });
        
        //Sets the column corresponding to the variable in Project
        titleCol.setCellValueFactory(new PropertyValueFactory<Project,String>("projectTitle"));
        schoolCol.setCellValueFactory(new PropertyValueFactory<Project,String>("school"));
        supervisorNameCol.setCellValueFactory(new PropertyValueFactory<Project,String>("supervisorName"));
        noOfStudentsCol.setCellValueFactory(new PropertyValueFactory<Project,Integer>("noOfStudents"));
        
        //Because the default width for TableColumn is 80, and there is no fixed set Width property,
        //We have to use min/max width. Min width is for >80 and Max width is for <80
        titleCol.setResizable(false);
        titleCol.setMinWidth(120);
        
        schoolCol.setResizable(false);
        schoolCol.setMinWidth(80);
        
        supervisorNameCol.setResizable(false);
        supervisorNameCol.setMinWidth(150);
        
        noOfStudentsCol.setResizable(false);
        noOfStudentsCol.setMinWidth(120);
    }
    
    private void initializeListViewEvents() {
        //Adding of the list view columns will be by objects
        //With cell factory, we bind the object and allow them to display as event titles
        listViewEvents.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            public void updateItem(Event e, boolean empty) {
                super.updateItem(e, empty);
                if(!empty)
                    setText(e.getEventTitle());
                else
                    setText(null);
            }
        });
        
        //When the selected index changes in listViewEvents, it will display the following event details
        listViewEvents.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (Integer.parseInt(newValue.toString()) != -1) { //If the selected index is not -1, meaning that the user has selected any of the projects
                    Event event = events.getEvent(Integer.parseInt(newValue.toString()));
                    textFieldEventTitle.setText(event.getEventTitle());
                    
                    LinkList projects = event.getProjectList();
                    table.getItems().clear(); //clear table before inserting
                    for(int i=0; i<projects.getNoOfElement();i++) {
                        Project project = (Project) projects.get(i);
                        //each project is placed inside table
                        table.getItems().add(project);
                    }
                }
            }
        });
    }
    
    private void loadListViewEvents() {
        //Clears the events title in listView
        listViewEvents.getItems().clear();
        //Gets the title for each event
        for(int i=0;i<events.getNoOfEvents();i++) {
            Event event = events.getEvent(i);
            listViewEvents.getItems().add(event);
        }
    }
    
    //Sorts the events alphabetically
    @FXML
    private void handleSortAction(ActionEvent event) {
        events.getEventsList().mergeSort(events.getHeadNode());
        loadListViewEvents();
    }
    
    //When the user wants to add an event
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(buttonAddEvent)) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Event");
            dialog.setHeaderText("Enter an Event Name");
            dialog.setContentText("Event Name: ");
            Optional<String> inputEventName = dialog.showAndWait();
            
            //If there is input
            if(inputEventName.isPresent()) {
                String eventName = inputEventName.get();
                
                //Check if any events have the same name as the user entered
                if(!events.checkIfEventExists(eventName)) {
                    Event newEvent = new Event(eventName);
                    events.addEvents(newEvent);
                    serializedFile.updateSerialized(events);
                    loadListViewEvents();
                }
                else
                {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Event Error");
                    alertError.setHeaderText(null);
                    alertError.setContentText("Event Names must not be the same");
                    alertError.show();
                }
            }
        }
        else if(event.getSource().equals(buttonRemoveEvent)) {
            //If the user wants to remove the want, it will remove from list view, update the serialization file and reload the list view
            events.removeEvent((Event) listViewEvents.getSelectionModel().getSelectedItem());
            serializedFile.updateSerialized(events);
            loadListViewEvents();
        }
        else if (event.getSource().equals(buttonUpdateEvent)) {
            if (listViewEvents.getSelectionModel().getSelectedIndex() != -1) {
                //If the user wants to remove the want, it will remove from list view, update the serialization file and reload the list view
                events.getEvent(listViewEvents.getSelectionModel().getSelectedIndex()).setEventTitle(textFieldEventTitle.getText());
                serializedFile.updateSerialized(events);

                //Alert to show that it has been saved
                Alert alertResult = new Alert(Alert.AlertType.INFORMATION);
                alertResult.setTitle("Event saved");
                alertResult.setContentText("Event has been saved successfully.");
                alertResult.showAndWait();
                loadListViewEvents();
            }
        }
    }
}
