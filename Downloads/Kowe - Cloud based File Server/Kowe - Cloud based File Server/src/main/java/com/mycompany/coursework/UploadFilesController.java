package com.mycompany.coursework;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// <TableView fx:id="dataTableView" prefHeight="200.0" prefWidth="200.0" />
public class UploadFilesController {

    
    private User user;

    private File selectedFile;

    @FXML
    private Button secondaryButton;

    @FXML
    private Label fileText;

    @FXML
    private TextField customTextField;

    @FXML
    private Button selectBtn;

    @FXML
    private void RefreshBtnHandler(ActionEvent event) {
        Stage primaryStage = (Stage) customTextField.getScene().getWindow();
        customTextField.setText((String) primaryStage.getUserData());
    }

    @FXML
    private void uploadBtnHandler(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");
        if (this.selectedFile != null) {
            try {
                FileManagement fileManager = new FileManagement(connection);

                fileManager.uploadFile(this.selectedFile.getName(), this.user.getUsername());
            } catch(IOException e) {
                System.out.println(e);
                dialogue("Internal Server Error","IO Exception ");
            }  catch (IllegalArgumentException e) {
                dialogue("Internal Server Error","File not found ");
            } 

        } else {
            dialogue("No File", "Choose a file");
        }
    }

    private void dialogue(String headerMsg, String contentMsg) {
        Stage secondaryStage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.DARKGRAY);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(headerMsg);
        alert.setContentText(contentMsg);
        Optional<ButtonType> result = alert.showAndWait();
    }

    @FXML
    private void selectBtnHandler(ActionEvent event) throws IOException {
        Stage primaryStage = (Stage) selectBtn.getScene().getWindow();
        primaryStage.setTitle("Select a File");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        this.selectedFile = selectedFile;
        if (selectedFile != null) {
            fileText.setText((String) selectedFile.getCanonicalPath());
        }

    }

    @FXML
    private void switchToSecondary() {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) secondaryButton.getScene().getWindow();
        try {
            DbConnection myObj = new DbConnection();
            //myObj.logoutUser(this.user);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);
            secondaryStage.setScene(scene);
            SecondaryController controller = loader.getController();
            controller.initialise(user);

            secondaryStage.setTitle("Welcome");
            secondaryStage.show();
            primaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialise(User userdata) {
        this.user = userdata;
    }
}
