package com.mycompany.coursework;

import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// <TableView fx:id="dataTableView" prefHeight="200.0" prefWidth="200.0" />
public class UpdateUserController {

    private User user;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private PasswordField passPasswordField;

    @FXML
    private PasswordField rePassPasswordField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button secondaryButton;

    @FXML
    private Button refreshBtn;

    @FXML
    private TextField customTextField;

    @FXML
    private void RefreshBtnHandler(ActionEvent event) {
        Stage primaryStage = (Stage) customTextField.getScene().getWindow();
        customTextField.setText((String) primaryStage.getUserData());
    }

    @FXML
    private void switchToPrimary() {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) secondaryButton.getScene().getWindow();
        try {
            DbConnection myObj = new DbConnection();
            System.out.println(this.user);
            myObj.logoutUser(this.user);

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
    private void updateBtnHandler() throws InvalidKeySpecException, ClassNotFoundException {

        String password = passPasswordField.getText();
        String confirm = rePassPasswordField.getText();
        String email = emailTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        DbConnection myObj = new DbConnection();

        if (email.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            dialogue("Incomplete fields", "All non-password fields are required!");
        }else if(!(password.isEmpty() && confirm.isEmpty()) && ((!password.isEmpty() && confirm.isEmpty()) || (password.isEmpty() && !confirm.isEmpty()))){
            dialogue("Incomplete fields", "Supply both password fields or leave them empty");
        }else if(!(password.isEmpty() && confirm.isEmpty()) && !password.equals(confirm)){
            dialogue("Invalid Data", "Passwords don't match!");
        }else {
            String inPassword = this.user.getPassword();
            
            if(password.isEmpty()){
                inPassword = myObj.generateSecurePassword(password);
            }
            
            Boolean update = myObj.updateUser(this.user.getUsername(), inPassword, firstName, lastName, email);
        
            if(update) {
                this.user.setPassword(inPassword);
                this.user.setEmail(email);
                this.user.setFirstsname(firstName);
                this.user.setLastname(lastName);
                passPasswordField.setText("");
                rePassPasswordField.setText("");
                
                dialogue("Success", "User Info Updated Successfully");
            }else{
                dialogue("Failure", "Something went wrong!");
            }
        }

    }

    public void initialise(User userdata) {
        this.user = userdata;
        // this.user=
        DbConnection myObj = new DbConnection();
        // UserManagement myObj = new UserManagement();
        ObservableList<User> data;

        firstNameTextField.setText(this.user.getFirstname());
        lastNameTextField.setText(this.user.getLastname());
        emailTextField.setText(this.user.getEmail());
    }
}
