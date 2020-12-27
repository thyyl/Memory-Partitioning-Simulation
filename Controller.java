package sample;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    void left() {
        if (fileValidation()) {
            try {
                ReadData.readFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            makeFadeOut();
        }
    }

    private void makeFadeOut() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration((Duration.millis(1000)));
        fadeTransition.setNode(root);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        fadeTransition.setOnFinished((ActionEvent event) -> loadNextScene());

        fadeTransition.play();
    }

    private void loadNextScene() {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource("sceneTwo.fxml"));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void loadMemoryList() {
        try {
            ReadData.setMemoryFilePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadJobList() {
        try {
            ReadData.setJobFilePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileValidation() {
        if (ReadData.memoryFilePath.getText().isEmpty() || ReadData.jobFilePath.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String context = "Please select all required files";
            alert.setContentText(context);
            alert.showAndWait();

            return false;
        }

        return true;
    }
}
