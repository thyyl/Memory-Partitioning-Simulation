package sample;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecondController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private ChoiceBox<String> allocationTypeBox;

    @FXML
    private ChoiceBox<String> fitTypeBox;

    @FXML
    private ChoiceBox<String> jobQuantityBox;

    @FXML
    private ChoiceBox<String> memorySizeBox;

    ObservableList<String> allocationTypeList = FXCollections.observableArrayList("Fixed Memory Partition", "Dynamic Memory Partition");
    ObservableList<String> fitTypeList = FXCollections.observableArrayList("First-Fit", "Best-Fit", "Worst-Fit");
    ObservableList<String> jobQuantityList = FXCollections.observableArrayList("3", "5", "7", "10");
    ObservableList<String> memorySizeList = FXCollections.observableArrayList("20000", "30000", "40000", "50000");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allocationTypeBox.setItems(allocationTypeList);
        allocationTypeBox.setValue("Fixed Memory Partition");

        fitTypeBox.setItems(fitTypeList);
        fitTypeBox.setValue("First-Fit");

        jobQuantityBox.setItems(jobQuantityList);
        jobQuantityBox.setValue("3");

        memorySizeBox.setItems(memorySizeList);
        memorySizeBox.setValue("20000");
    }

    @FXML
    private void choiceBoxButtonPushed() {
        if (validation()) {
            ReadData.dataList.setAllocationType(allocationTypeBox.getValue());
            ReadData.dataList.setAlgorithmType(fitTypeBox.getValue());

            if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition"))
                ReadData.dataList.setJobListEnquire(Integer.parseInt(jobQuantityBox.getValue()));
            else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition"))
                ReadData.dataList.setDynamicMemory(Integer.parseInt(memorySizeBox.getValue()));

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
            Parent thirdView;
            thirdView = FXMLLoader.load(getClass().getResource("sceneThree.fxml"));
            Scene newScene = new Scene(thirdView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validation() {
        if (allocationTypeBox.getValue().equals("Allocation Type") || fitTypeBox.getValue().equals("Algorithm Type")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String context = "Please select all required fields";
            alert.setContentText(context);
            alert.showAndWait();

            return false;
        } else if (allocationTypeBox.getValue().equals("Fixed Memory Allocation") && jobQuantityBox.getValue().equals("Memory Blocks")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String context = "Please select the quantity of memory blocks";
            alert.setContentText(context);
            alert.showAndWait();

            return false;
        } else if (allocationTypeBox.getValue().equals("Dynamic Memory Allocation") && memorySizeBox.getValue().equals("Memory Size")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String context = "Please select the size of memory";
            alert.setContentText(context);
            alert.showAndWait();

            return false;
        }

        return true;
    }
}
