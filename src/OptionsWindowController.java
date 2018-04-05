/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class OptionsWindowController implements Initializable {
    
    Stage stage;
    Preferences pref;
    @FXML TextField textFieldProject, textFieldEvent;
    @FXML Button buttonProject, buttonEvent, buttonUpdate, buttonCancel, buttonProjectClear, buttonEventClear;
    @FXML ComboBox comboBoxTheme;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Sets the values for theme combo box
        comboBoxTheme.getItems().addAll("blue","green");

        //Sets all the values, and these values are obtained from preference
        pref = Preferences.userNodeForPackage(Loader.class);
        textFieldProject.setText(pref.get("PREF_PROJECTDIR", ""));
        textFieldEvent.setText(pref.get("PREF_EVENTDIR", ""));
        comboBoxTheme.getSelectionModel().select(pref.get("PREF_THEME", "blue"));
    }
    
    @FXML
    private void handleDirAction(ActionEvent event) {
        //File chooser opens a GUI for navigating the file system, which allows the user to choose their preference projects.txt or events.ser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        if(event.getSource().equals(buttonProject)) {
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if(selectedFile!=null)
                textFieldProject.setText(selectedFile.toString());
        }
        else if(event.getSource().equals(buttonEvent)) {
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Serializable Files", "*.ser"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if(selectedFile!=null)
                textFieldEvent.setText(selectedFile.toString());
        }
    }
    
    //When the user wishes to clear the directory
    @FXML
    private void handleButtonClearAction(MouseEvent event) {
        if(event.getSource().equals(buttonProjectClear))
            textFieldProject.setText("projects.txt");
        else if(event.getSource().equals(buttonEventClear))
            textFieldEvent.setText("events.ser");
    }
    
    //When the user updates the preference, set all the preferences and display alert to show it is saved
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if(event.getSource().equals(buttonUpdate)) {
            pref.put("PREF_PROJECTDIR", textFieldProject.getText());
            pref.put("PREF_EVENTDIR", textFieldEvent.getText());
            pref.put("PREF_THEME", comboBoxTheme.getSelectionModel().getSelectedItem().toString());
            
            Alert alertResult = new Alert(Alert.AlertType.INFORMATION);
            alertResult.setTitle("Options updated");
            alertResult.setContentText("Options have been updated successfully. Please restart the window to take effect");
            alertResult.showAndWait();
        }
        //Closes window
        else if(event.getSource().equals(buttonCancel)) {
            stage = (Stage) buttonCancel.getScene().getWindow();
            stage.close();
        }
    }
    
}
