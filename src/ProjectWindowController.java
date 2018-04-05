import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.CharacterStringConverter;

public class ProjectWindowController implements Initializable {

    private ProjectFile files; //Class used for reading File, updating File and exporting of output + events

    //Main.fxml widgets
    @FXML private TextField textFieldTitle, textFieldSchool, textFieldSupervisorName, textFieldFilter, studentFilter;
    @FXML private Label labelNoOfStudents, labelNoOfProjects, labelEdit;
    @FXML private ImageView imageViewSchool;
    @FXML private TableView table;
    @FXML private ListView listViewProjects;
    @FXML private Button buttonExport, buttonSave, buttonAddProject, buttonDeleteProject, buttonAdd, buttonMinus, buttonRegisterEvent, buttonUnregisterEvent, buttonUpdateEvent;
    @FXML private MenuItem menuItemClose;
    @FXML private CheckMenuItem checkMenuItemEdit;

    //Columns used for the TableView table
    private final TableColumn adminNoCol = new TableColumn("Admin No");
    private final TableColumn studentNameCol = new TableColumn("Student Name");
    private final TableColumn courseCol = new TableColumn("Course");
    private final TableColumn genderCol = new TableColumn("Gender");
    private ArrayList<Project> projects;
    //this variables are used for the edit feature.
    //If menu bar is ticked, isEdited will be true, which will enable edit of textfields, table, save and disable export
    //If the textfield/table is edited, the isEdited will be enabled
    private boolean isEdited = false, EDITMODE = false;

    ArrayList<String> projectsTitle = new ArrayList<String>();
    private ArrayList<Student> projectStudents, revertProjectStudents;
    //Issue: The table items are binded by the ArrayList<Student>, if you edit any value in a table, the values will be automatically saved in the ArrayList<Student>, making it unable
    //to undo back the previous value. The solution is to create an ArrayList<Student> variable and set it back to the default one, which revertProjectStudents is created.

    //CA2 update
    private EventCollection events; //Used for importing the events from projectfile variable files
    //These two events must be split so that both the unregistered and registered listview can work easily through having the same index.
    //For instance, listview 3rd index is also registeredevents 3rd index
    private EventCollection registeredEvents = new EventCollection();
    private EventCollection unregisteredEvents = new EventCollection();
    @FXML private ListView listViewRegisteredEvents;
    @FXML private ListView listViewUnregisteredEvents;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //imports the project and events from the following files
        Preferences pref = Preferences.userNodeForPackage(Loader.class);
        String projectDir = pref.get("PREF_PROJECTDIR", "projects.txt");
        String eventDir = pref.get("PREF_EVENTDIR", "events.ser");
        
        files = new ProjectFile(new File(projectDir), new File(eventDir)); //Lists out the file directory
        projects = files.readFile(); //Imports the file, getting the ArrayList<Project> object in exchange
        events = files.readSerialized();
        
        if(projects==null || events==null) {
            Stage stage;
            stage = (Stage) listViewProjects.getScene().getWindow();
            stage.close();
        }

        //Loads the projects in the listView which will display the list of projects
        loadListViewProjects();

        //Initializes the list view of projects and tables for the first time run
        initializelistViewProjectsHandler(); //Handler is to manage what to do when a list view index changes
        initializeTable(); //Initialize the table

        initializeListViewEvents(); //This is to initialize the listViewEvents for binding objects strings

        //The label on the top right will show how many projects are loaded
        labelNoOfProjects.setText(Integer.toString(projects.size()));

        //Sets up the ImageView and sets image to the SP logo
        File fileImage = new File("src/images/index.png");
        imageViewSchool.setImage(new Image(fileImage.toURI().toString()));
    }

    private void initializeTable() {
        //Add all the columns in the Student table
        table.getColumns().addAll(adminNoCol, studentNameCol, courseCol, genderCol);
        //Disable column reordering
        table.getColumns().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                c.next();
                if (c.wasReplaced()) {
                    table.getColumns().clear();
                    table.getColumns().addAll(adminNoCol, studentNameCol, courseCol, genderCol);
                }
            }
        });

        //Sets the column corresponding to the variable in Student
        adminNoCol.setCellValueFactory(new PropertyValueFactory<Student, String>("adminNo"));
        //Sets it as a textfield cell
        adminNoCol.setCellFactory(TextFieldTableCell.forTableColumn());
        //After when the column has been edited, it will set the value to the current Project that is focused on
        adminNoCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, String>>() {
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())).setAdminNo(event.getNewValue().replace(",", ""));
                //the replace method is to remove commas from value to prevent the conflict with the project file structure
                table.refresh();
                handleOnKeyTyped();
                //sets the isEdited to true
            }
        });

        studentNameCol.setCellValueFactory(new PropertyValueFactory<Student, String>("studentName"));
        studentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        studentNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, String>>() {
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())).setStudentName(event.getNewValue().replace(",", ""));
                table.refresh();
                handleOnKeyTyped();
            }
        });

        courseCol.setCellValueFactory(new PropertyValueFactory<Student, String>("course"));
        courseCol.setCellFactory(TextFieldTableCell.forTableColumn());
        courseCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, String>>() {
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())).setCourse(event.getNewValue().replace(",", ""));
                table.refresh();
                handleOnKeyTyped();
            }
        });

        genderCol.setCellValueFactory(new PropertyValueFactory<Student, Character>("gender"));
        //Sets a ComboBox cell, which only allow the users to pick either M or F
        genderCol.setCellFactory(ComboBoxTableCell.forTableColumn(new CharacterStringConverter(), 'M', 'F'));
        genderCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, Character>>() {
            @Override
            public void handle(CellEditEvent<Student, Character> event) {
                ((Student) event.getTableView().getItems().get(
                        event.getTablePosition().getRow())).setGender(event.getNewValue());
                handleOnKeyTyped();
            }
        });

        //Because the default width for TableColumn is 80, and there is no fixed set Width property,
        //We have to use min/max width. Min width is for >80 and Max width is for <80
        adminNoCol.setResizable(false);
        adminNoCol.setMinWidth(80);

        studentNameCol.setResizable(false);
        studentNameCol.setMinWidth(180);

        courseCol.setResizable(false);
        courseCol.setMinWidth(130);

        genderCol.setResizable(false);
        genderCol.setMaxWidth(80);
    }

    private void initializelistViewProjectsHandler() {
        listViewProjects.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            //This method will call when the selected index of the list View changes
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (Integer.parseInt(newValue.toString()) != -1) { //If the selected index is not -1, meaning that the user has selected any of the projects
                    //If edit mode is enabled, when the user selects another project, the save button will be disabled
                    //The save button will be enabled again if the user makes any changes in the record

                    //Load the events in both list views when a project is selected
                    loadListViewEvents(projects.get(Integer.parseInt(newValue.toString())));

                    if (EDITMODE) {
                        buttonSave.setDisable(true);
                        //Sometimes, the user will forget to save and select another project
                        //There the alert is called, prompting them if they want to save the project

                        if (isEdited) {
                            Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
                            alertConfirm.setTitle("Save Project");
                            alertConfirm.setHeaderText(null);
                            alertConfirm.setContentText("Do you want to update the project that you have just edited?");
                            //showAndWait prevents the user from clicking outside during the alert prompt
                            Optional<ButtonType> result = alertConfirm.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                //if user agrees, project will save
                                updateProject(Integer.parseInt(oldValue.toString()));
                            } else {
                                //if user disagrees, any changes made will be reverted back to normal
                                projects.get(Integer.parseInt(oldValue.toString())).setStudents(revertProjectStudents);
                            }
                        }
                    }

                    //When the user selects another project, the project details from the ArrayList<Project> projects will fill in the information to the textField
                    textFieldTitle.setText(projects.get(Integer.parseInt(newValue.toString())).getProjectTitle());
                    textFieldSchool.setText(projects.get(Integer.parseInt(newValue.toString())).getSchool());
                    textFieldSupervisorName.setText(projects.get(Integer.parseInt(newValue.toString())).getSupervisorName());
                    labelNoOfStudents.setText(Integer.toString(projects.get(Integer.parseInt(newValue.toString())).getNoOfStudents()));

                    //Creates a ArrayList<Student> and then retrieves the list of students from the project that the user selected
                    //After that, the arraylist is converted to ObservableCollection then inserted in the table.
                    projectStudents = new ArrayList<Student>();
                    projectStudents = projects.get(Integer.parseInt(newValue.toString())).getStudents();
                    table.setItems(FXCollections.observableArrayList(projectStudents));

                    //Replaces the image to what school the user has chosen in the listview.
                    //No validation is needed if the file is not found
                    File fileImage = new File("src/images/" + textFieldSchool.getText() + ".png");
                    imageViewSchool.setImage(new Image(fileImage.toURI().toString()));

                    //Since user selects a new project, the boolean isEdited will set to false as the user has not edited anything in the start
                    isEdited = false;
                    //Clones the copy of projectStudents to revertProjectStudents. Explaination will be shown at the top (variable declaration)
                    revertProjectStudents = cloneStudents(projectStudents);
                } else if (Integer.parseInt(newValue.toString()) == -1) //This will only trigger if the user unchecks the Edit Mode
                //If the user edits a project and unchecks the Edit mode under the same project that he is selected, it will revert back to the normal one
                {
                    projects.get(Integer.parseInt(oldValue.toString())).setStudents(revertProjectStudents);
                }

                table.refresh();
            }
        });
    }

    private void loadListViewProjects() {
        //Clears the list of projects, then adds the project titles from each of the element in the ArrayList<Project> projects
        projectsTitle.clear();
        for (Project p : projects) {
            projectsTitle.add(p.getProjectTitle());
        }
        //Refreshes the list view, which new items may be added
        listViewProjects.setItems(FXCollections.observableArrayList(projectsTitle));
    }

    private void initializeListViewEvents() {
        //This method is for binding the listView object to the string Event Title
        listViewRegisteredEvents.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            public void updateItem(Event e, boolean empty) {
                super.updateItem(e, empty);
                if (!empty) {
                    setText(e.getEventTitle());
                } else {
                    setText(null);
                }
            }
        });

        listViewUnregisteredEvents.setCellFactory(listView -> new ListCell<Event>() {
            @Override
            public void updateItem(Event e, boolean empty) {
                super.updateItem(e, empty);
                if (!empty) {
                    setText(e.getEventTitle());
                } else {
                    setText(null);
                }
            }
        });
    }

    private void loadListViewEvents(Project selectedProject) {
        //clear the list view for registered and unregistered events
        listViewRegisteredEvents.getItems().clear();
        listViewUnregisteredEvents.getItems().clear();

        //loop to check all the events, to see if the project is involved in each event
        for (int i = 0; i < events.getNoOfEvents(); i++) {
            Event e = events.getEvent(i);
            if (e.checkIfProjectExists(selectedProject)) {
                //If the event is registered, add the event to registeredEvents and also display it in the listview
                //The registeredevents/unregisteredevents will work closely with the listView, so each event has the same index value
                registeredEvents.addEvents(e);
                listViewRegisteredEvents.getItems().add(e);
            } else {
                unregisteredEvents.addEvents(e);
                listViewUnregisteredEvents.getItems().add(e);
            }
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource().equals(buttonAddProject)) { //if the user wants to add a project
                //Gets the .fxml file that will be loaded in the Parent
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProject.fxml"));
                Parent root = loader.load();

                //The stage is to set the loader and show the .FXML in a new window
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);//To prevent users from selecting outside during adding a project
                stage.setScene(new Scene(root));
                stage.showAndWait();

                //The controller will get from the child window, and then it allows to get the projects that the user wants to add
                AddProjectController controller = loader.getController();
                for (Project p : controller.getProjects()) {
                    projects.add(p);
                }
                //Reloads the list view Projects
                loadListViewProjects();
                //Save the projects in projects.txt
                files.saveProjects(projects);
                //Updates the number of projects
                labelNoOfProjects.setText(Integer.toString(projects.size()));
                
        } else //if not, these are used for the Export button and save button. The following codes below will be checked whether the user has selected a project or not
        if (listViewProjects.getSelectionModel().getSelectedIndex() == -1) {
            //we want the user to prevent them from saving/exporting a project that is not selected
            Alert alertError = new Alert(AlertType.ERROR);
            alertError.setTitle("Undefined Project Error");
            alertError.setHeaderText(null);
            alertError.setContentText("Please select a project first!");
            alertError.show();
        } else if (event.getSource().equals(buttonExport)) {
            //the export projects method is called to export out the project that they want
            files.exportProjects(projects.get(listViewProjects.getSelectionModel().getSelectedIndex()));
        } else if (event.getSource().equals(buttonSave)) {
            //if the user is in edit mode and want to save the things that he just edited
            isEdited = false;
            buttonSave.setDisable(true);
            updateProject(listViewProjects.getSelectionModel().getSelectedIndex());
        } else if (event.getSource().equals(buttonDeleteProject)) {
            Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
            alertConfirm.setTitle("Delete Project");
            alertConfirm.setHeaderText(null);
            alertConfirm.setContentText("Are you sure you want to delete the project? This cannot be undone.");
            //showAndWait prevents the user from clicking outside during the alert prompt
            Optional<ButtonType> result = alertConfirm.showAndWait();
            if (result.get() == ButtonType.OK) {
                //if user agrees, project will delete
                projects.remove(listViewProjects.getSelectionModel().getSelectedIndex());
                //Reloads the list view Projects
                listViewProjects.getSelectionModel().selectPrevious();
                loadListViewProjects();

                //Save the projects in projects.txt
                files.saveProjects(projects);
                //Updates the number of projects
                labelNoOfProjects.setText(Integer.toString(projects.size()));
            }
        }
    }

    @FXML
    private void handleButtonClearAction(MouseEvent event) {
        //When the user wants to clear the filter input that he has given
        textFieldFilter.clear();
        //Readds the project titles in listView
        loadListViewProjects();
    }

    @FXML
    private void handleMenuItemAction(ActionEvent event) {
        //This method is for the menu item(on top of the window)
        //When the user wants to close the program
        if (event.getSource().equals(menuItemClose)) {
            System.exit(0);
        } //If the edit mode is checked
        else if (event.getSource().equals(checkMenuItemEdit)) {
            if (checkMenuItemEdit.isSelected()) {
                //EDITMODE will be enabled to let the system know that it is now in edit mode
                EDITMODE = true;

                //Allows all the textField and table to be editable, and disables the export button
                textFieldTitle.setMouseTransparent(false);
                textFieldSchool.setMouseTransparent(false);
                textFieldSupervisorName.setMouseTransparent(false);
                studentFilter.setDisable(true);
                studentFilter.clear();
                buttonDeleteProject.setDisable(false);
                buttonExport.setDisable(true);
                table.setEditable(true);
                //Clones the copy of projectStudents to revertProjectStudents. Explaination will be shown at the top (variable declaration)
                if (listViewProjects.getSelectionModel().getSelectedIndex() != -1) {
                    revertProjectStudents = cloneStudents(projectStudents);
                }
                //The label and buttons for Students table will be visible
                labelEdit.setVisible(true);
                buttonAdd.setVisible(true);
                buttonMinus.setVisible(true);
            } else if (!checkMenuItemEdit.isSelected()) {
                //EDITMODE will be enabled to let the system know that it is not in edit mode
                EDITMODE = false;
                //To show that it has not been edited
                isEdited = false;

                //refreshes all the value, which reset the modified one to the original
                int projectNo = listViewProjects.getSelectionModel().getSelectedIndex();
                listViewProjects.getSelectionModel().select(-1);
                listViewProjects.getSelectionModel().select(projectNo);

                //Prevents the textFIeld and table to be editable, and disables the Save button, enables the export button
                textFieldTitle.setMouseTransparent(true);
                textFieldSchool.setMouseTransparent(true);
                textFieldSupervisorName.setMouseTransparent(true);
                studentFilter.setDisable(false);
                buttonDeleteProject.setDisable(true);
                buttonExport.setDisable(false);
                buttonSave.setDisable(true);
                table.setEditable(false);

                //If the user edits halfway and wants to disable Edit mode, there is an issue that the table can still be edited as it is cell is selected
                //It can be prevented by focusing on the listView Projects
                listViewProjects.requestFocus();

                //The label and buttons for Students table will not be visible
                labelEdit.setVisible(false);
                buttonAdd.setVisible(false);
                buttonMinus.setVisible(false);
            }
        }
    }

    @FXML
    private void handleOnKeyTyped() {
        //If the user has typed anything during edit mode, the Save button will be enabled and boolean isEdited will be true
        buttonSave.setDisable(false);
        if (listViewProjects.getSelectionModel().getSelectedIndex() != -1) {
            isEdited = true;
        }
    }

    @FXML
    private void handleOnFilterProjectsTyped(KeyEvent event) {
        //If the user has typed anything in the filter textField, the list of projects will be filtered accordingly
        FilteredList<String> filteredData = new FilteredList<>(FXCollections.observableArrayList(projectsTitle), s -> true);
        textFieldFilter.textProperty().addListener(observable -> {
            String filter = textFieldFilter.getText().toLowerCase();
            if (filter == null || filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            } else {
                filteredData.setPredicate(s -> s.contains(filter));
            }
        });
        listViewProjects.setItems(filteredData);
    }

    @FXML
    private void handleOnFilterStudentsTyped(KeyEvent event) {
        // Get FilteredList from ObservableList (Displays all data intially)
        FilteredList<Student> filteredData = new FilteredList<>(FXCollections.observableArrayList(revertProjectStudents), s -> true);
        // Set the filter predicate whenever filter changes. (Event listener)
        studentFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (student.getStudentName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches student name.
                } else if (student.getAdminNo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches student admin number.
                } else if (student.getCourse().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches student courses.
                }
                return false; // Does not match.
            });
        });

        // Instantiate SortedList object using FilteredList
        SortedList<Student> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList to the TableView
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // Set sorted and filtered data in table.
        table.setItems(sortedData);

    }

    @FXML
    private void handleButtonTableAction(ActionEvent event) {
        //This is for handling the add and remove buttons for the students table in Edit Mode
        if (listViewProjects.getSelectionModel().getSelectedIndex() != -1) { //If a project is selected, it will allow the user to either add or remove a row
            if (event.getSource().equals(buttonAdd)) {
                projectStudents.add(new Student("P0000000", "Name", "DMIT", 'M')); //Adds a new record in a table
                labelNoOfStudents.setText(Integer.toString(Integer.parseInt(labelNoOfStudents.getText()) + 1)); //Increases the No. Of Students
                buttonSave.setDisable(false);
            } else if (event.getSource().equals(buttonMinus)) {
                try {
                    if (table.getSelectionModel().getSelectedIndex() != -1) {
                        projectStudents.remove(table.getSelectionModel().getSelectedIndex()); //Remove the student that the user has selected
                        table.getSelectionModel().select(-1);
                        buttonSave.setDisable(false);

                        if (Integer.parseInt(labelNoOfStudents.getText()) != 0) //To prevent the No. Of Students label to go <0, it will stop there
                        {
                            labelNoOfStudents.setText(Integer.toString(Integer.parseInt(labelNoOfStudents.getText()) - 1));
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
            //Shows that any addition/remove of any row is considered Edited
            isEdited = true;
            //Refreshes the table
            table.setItems(FXCollections.observableArrayList(projectStudents));
        }
    }

    @FXML
    private void handleButtonEventAction(ActionEvent event) {
        //This is for handling the add/remove project to an event button
        if (event.getSource().equals(buttonRegisterEvent) && listViewUnregisteredEvents.getSelectionModel().getSelectedIndex() != -1) {
            Event e = (Event) listViewUnregisteredEvents.getSelectionModel().getSelectedItem();
            Project selectedProject = projects.get(listViewProjects.getSelectionModel().getSelectedIndex());
            e.addProject(selectedProject);

            listViewUnregisteredEvents.getItems().remove(e);
            listViewRegisteredEvents.getItems().add(e);

            buttonUpdateEvent.setDisable(false);

        } else if (event.getSource().equals(buttonUnregisterEvent) && listViewRegisteredEvents.getSelectionModel().getSelectedIndex() != -1) {
            Event e = (Event) listViewRegisteredEvents.getSelectionModel().getSelectedItem();
            Project selectedProject = projects.get(listViewProjects.getSelectionModel().getSelectedIndex());
            e.removeProject(selectedProject);

            listViewRegisteredEvents.getItems().remove(e);
            listViewUnregisteredEvents.getItems().add(e);

            buttonUpdateEvent.setDisable(false);

        } else if (event.getSource().equals(buttonUpdateEvent)) {
            files.updateSerialized(events);

            Alert alertResult = new Alert(AlertType.INFORMATION);
            alertResult.setTitle("Events Updated");
            alertResult.setContentText("All events has been updated successfully.");
            alertResult.showAndWait();

            buttonUpdateEvent.setDisable(true);
        } else {
            Alert alertError = new Alert(AlertType.ERROR);
            alertError.setTitle("Event Error");
            alertError.setHeaderText(null);
            alertError.setContentText("Please select an event from the list view!");
            alertError.show();
        }
    }

    //Updates the project by setting the project into the ArrayList, where the ArrayList No. is the getSelectedIndex of the listview.
    //If case 2: the user did not click save button and goes to the other user and it prompts, use the oldValue from the listener to get the ArrayList.
    private void updateProject(int projectNo) {
        //Omits all commas that has been made by the user and retreives all the information from the edited Project
        String newProjectTitle = textFieldTitle.getText().replace(",", "");
        String newSchool = textFieldSchool.getText().replace(",", "");
        String newSupervisorName = textFieldSupervisorName.getText().replace(",", "");
        int newNoOfStudents = Integer.parseInt(labelNoOfStudents.getText());

        //Gets the items from the table
        ArrayList<Student> newProjectStudents = new ArrayList<Student>(table.getItems());

        //Creates a project object which will put in all the information and then sets it in the ArrayList<Project> projects
        Project savedProject = new Project(newProjectTitle, newSchool, newSupervisorName, newNoOfStudents, newProjectStudents);
        projects.set(projectNo, savedProject);

        //Saves the project in the project File.
        files.saveProjects(projects);
        //Updates the new list of projects(if the user has changed the title of any project)
        listViewProjects.getItems().set(projectNo, newProjectTitle);
    }

    //Clones the ArrayList<Student>, which is used for revertProjectStudents in the edit mode
    private ArrayList<Student> cloneStudents(ArrayList<Student> students) {
        ArrayList<Student> clonedStudents = new ArrayList<Student>();
        for (Student s : students) {
            clonedStudents.add(new Student(s));
        }
        return clonedStudents;
    }

}
