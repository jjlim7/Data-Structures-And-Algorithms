/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.CharacterStringConverter;

public class AddProjectController implements Initializable {

    //AddProject.fxml widgets
    @FXML private Button buttonAddProj, buttonAdd, buttonMinus;
    @FXML private TextField textFieldTitle,textFieldSchool,textFieldSupervisorName;
    @FXML private TableView table;
    
    //Columns used for the TableView table
    private final TableColumn adminNoCol = new TableColumn("Admin No");
    private final TableColumn studentNameCol = new TableColumn("Student Name");
    private final TableColumn courseCol = new TableColumn("Course");
    private final TableColumn genderCol = new TableColumn("Gender");
    
    private ArrayList<Student> projectStudents = new ArrayList<Student>();
    private ArrayList<Project> addedProjects=new ArrayList<Project>();
    
    private void initializeTable() {
        //Add all the columns in the Student table
        table.getColumns().addAll(adminNoCol, studentNameCol, courseCol, genderCol);
        //Disable column reorder
        table.getColumns().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                c.next();
          if(c.wasReplaced()) {
              table.getColumns().clear();
              table.getColumns().addAll(adminNoCol, studentNameCol, courseCol, genderCol);
                }
            }
        });
        
        //Sets the column corresponding to the variable in Student
        adminNoCol.setCellValueFactory(new PropertyValueFactory<Student,String>("adminNo"));
        //Sets it as a textfield cell
        adminNoCol.setCellFactory(TextFieldTableCell.forTableColumn());
        //After when the column has been edited, it will set the value to the current Project that is focused on
        adminNoCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                event.getTablePosition().getRow())
                ).setAdminNo(event.getNewValue().replace(",", ""));
                table.refresh();
            }
        });
  
        studentNameCol.setCellValueFactory(new PropertyValueFactory<Student,String>("studentName"));
        studentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        studentNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                event.getTablePosition().getRow())
                ).setStudentName(event.getNewValue().replace(",", ""));
                //the replace method is to remove commas from value to prevent the conflict with the project file structure
                table.refresh();
            }
        });
        
        courseCol.setCellValueFactory(new PropertyValueFactory<Student,String>("course"));
        courseCol.setCellFactory(TextFieldTableCell.forTableColumn());
        courseCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Student, String> event) {
                ((Student) event.getTableView().getItems().get(
                event.getTablePosition().getRow())
                ).setCourse(event.getNewValue().replace(",", ""));
                table.refresh();
            }
        });
        
        genderCol.setCellValueFactory(new PropertyValueFactory<Student,Character>("gender"));
        //Sets a ComboBox cell, which only allow the users to pick either M or F
        genderCol.setCellFactory(ComboBoxTableCell.forTableColumn(new CharacterStringConverter(),'M','F'));
        genderCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, Character>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Student, Character> event) {
                ((Student) event.getTableView().getItems().get(
                event.getTablePosition().getRow())
                ).setGender(event.getNewValue());
            }
        });
        
        //Because the default width for TableColumn is 80, and there is no fixed set Width property,
        //We have to use min/max width. Min width is for >80 and Max width is for <80
        adminNoCol.setResizable(false);
        adminNoCol.setMinWidth(80);
        adminNoCol.setSortable(false);
        
        studentNameCol.setResizable(false);
        studentNameCol.setMinWidth(135);
        studentNameCol.setSortable(false);
        
        courseCol.setResizable(false);
        courseCol.setMinWidth(60);
        courseCol.setSortable(false);
        
        genderCol.setResizable(false);
        genderCol.setMaxWidth(60);
        genderCol.setSortable(false);
        
        //Allows the table to be editable
        table.setEditable(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Initializes the table for the first time run
        initializeTable();
    }    
    
    @FXML
    private void validationChecking() {
        //Disable the save button if any of the fields are empty, or contains any non-alphabets
        if(textFieldTitle.getText().contains(",") || textFieldSchool.getText().contains(",") || 
                textFieldSupervisorName.getText().contains(",") || table.getSelectionModel().getTableView().getItems().isEmpty())
            buttonAddProj.setDisable(true);
        else
            buttonAddProj.setDisable(false);
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) { 
        Alert alertConfirmation = new Alert(AlertType.CONFIRMATION);
        alertConfirmation.setTitle("Create Project");
        alertConfirmation.setContentText("Are you sure you want to create a project?");
        
        Alert alertResult = new Alert(AlertType.INFORMATION);
        alertResult.setTitle("Project Added");
        alertResult.setContentText("Project "+textFieldTitle.getText()+" has been added. Please exit the window to take effect.");
        
        Optional<ButtonType> result = alertConfirmation.showAndWait(); //Prompts the user if he wants to create the project
        if(result.get()==ButtonType.OK) {
            //Adds the project inside the ArrayList<Projects> addedProjects, which will eventually be inserted in the parent window
            addedProjects.add(new Project(textFieldTitle.getText(), textFieldSchool.getText(), textFieldSupervisorName.getText(), projectStudents.size(), projectStudents));
            alertResult.showAndWait(); //Tells the user that the project has been added
            clearItems(); //Clears all the textfields and tables
        }
    }
    
    @FXML
    private void handleButtonTableAction(ActionEvent event) {
        //If a project is selected, it will allow the user to either add or remove a row
        if (event.getSource().equals(buttonAdd)) 
        {
            projectStudents.add(new Student("P0000000","Name","DMIT",'M')); //Adds a new record in a table
        }
        else if (event.getSource().equals(buttonMinus)) {
            try{
                if (table.getSelectionModel().getSelectedIndex() != -1) {
                    projectStudents.remove(table.getSelectionModel().getSelectedIndex()); //Remove the student that the user has selected
                    table.getSelectionModel().select(-1);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
        }
        //Refreshes the table
        table.setItems(FXCollections.observableArrayList(projectStudents));
    } 
    
    private void clearItems() {
        //This method is to clear all the textfields and table
        //Clearing textfields
        textFieldTitle.clear();
        textFieldSchool.clear();
        textFieldSupervisorName.clear();
        //The projectStudents have to re-instantiate a new ArrayList<Student>
        projectStudents=new ArrayList<Student>();
        //Refreshes the table
        table.setItems(FXCollections.observableArrayList(projectStudents));
    }
    
    public ArrayList<Project> getProjects() {
        //This method will be called in the parent(caller) window, which will return the projects that the user wants to add.
        return addedProjects;
    }
}