/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.coursework;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ntu-user
 */
public class RegisterController {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button registerBtn;

    @FXML
    private Button backLoginBtn;

    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passPasswordField;

    @FXML
    private PasswordField rePassPasswordField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    // @FXML
    // private Text fileText;
    // <Label fx:id="fileText" layoutX="77.0" layoutY="343.0" text="Select File." />
    @FXML
    private Text fileText;

    @FXML
    private Button selectBtn;

    @FXML
    private void selectBtnHandler(ActionEvent event) throws IOException {
        Stage primaryStage = (Stage) selectBtn.getScene().getWindow();
        primaryStage.setTitle("Select a File");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            //fileText.setText((String) selectedFile.getCanonicalPath());
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

//    @FXML
//    private void registerBtnHandler(ActionEvent event) {
//        Stage secondaryStage = new Stage();
//        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            UserManagement myObj = new UserManagement();
//            if (passPasswordField.getText().equals(rePassPasswordField.getText())) {
//                myObj.user_signups(userTextField.getText(), passPasswordField.getText(), emailTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText());
//                dialogue("Adding information to the database", "Successful!");
//                String[] credentials = {userTextField.getText(), passPasswordField.getText(), emailTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText() };
//                loader.setLocation(getClass().getResource("secondary.fxml"));
//                Parent root = loader.load();
//                Scene scene = new Scene(root, 640, 480);
//                secondaryStage.setScene(scene);
//                SecondaryController controller = loader.getController();
//                secondaryStage.setTitle("Show users");
//                controller.initialise(credentials);
//                String msg = "some data sent from Register Controller";
//                secondaryStage.setUserData(msg);
//            } else {
//                loader.setLocation(getClass().getResource("register.fxml"));
//                Parent root = loader.load();
//                Scene scene = new Scene(root, 640, 480);
//                secondaryStage.setScene(scene);
//                secondaryStage.setTitle("Register a new User");
//            }
//            secondaryStage.show();
//            primaryStage.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void registerBtnHandler(ActionEvent event) {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();

            DbConnection myObj = new DbConnection();

            String password = passPasswordField.getText();
            String confirm = rePassPasswordField.getText();
            String email = emailTextField.getText();
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String userName = userTextField.getText();

            if (password.isEmpty() || confirm.isEmpty() || email.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || userName.isEmpty()) {
                dialogue("Incomplete fields", "All fields are required!");
            } else if(myObj.checkUserExists(userName)){
                dialogue("User exists", "Username in use!");
            } else if(myObj.checkEmailExists(email)){
                dialogue("User exists", "Email in use!");
            }else if (password.equals(confirm)) {
                Boolean created = myObj.createUser(userName, password, firstName, lastName, email, 1);

                
                if(created) {

                    dialogue("Success", "User Created, go back to login page to login");
                
                }else {
                    dialogue("Internal Server Error", "Error creating user");
                }
            } else {
                dialogue("Invalid Data", "Passwords don't match!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void backLoginBtnHandler(ActionEvent event) {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) backLoginBtn.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);
            secondaryStage.setScene(scene);
            secondaryStage.setTitle("Login");
            secondaryStage.show();
            primaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}