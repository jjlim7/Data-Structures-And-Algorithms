import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenuController implements Initializable {
    
    @FXML private ImageView imageViewProjects, imageViewEvents, imageViewOptions;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // There is nothing to initialize, but there will only be mouse clicked events on this code
    }
    
    @FXML
    public void handleOnMouseClicked(MouseEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader();
            
            //Gets the preference theme, if there is no preference theme. the default value will be blue
            Preferences pref = Preferences.userNodeForPackage(Loader.class);
            String themeDir = pref.get("PREF_THEME", "blue");
            
            if(e.getSource().equals(imageViewProjects)) {
                loader.setLocation(getClass().getResource("ProjectWindow.fxml"));
            }
            else if(e.getSource().equals(imageViewEvents)) {
                loader.setLocation(getClass().getResource("EventWindow.fxml"));
            }
            else if(e.getSource().equals(imageViewOptions)) {
                loader.setLocation(getClass().getResource("OptionsWindow.fxml"));
            }
            
            Parent root = loader.load();
            //The stage is to set the loader and show the .FXML in a new window
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("css/"+themeDir+".css");
            
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);//To prevent users from selecting outside during adding a project
            stage.showAndWait();
        }
        catch (IOException ex) {
            System.out.println(ex+". Failed to load window.");
        }
    }
    
}
