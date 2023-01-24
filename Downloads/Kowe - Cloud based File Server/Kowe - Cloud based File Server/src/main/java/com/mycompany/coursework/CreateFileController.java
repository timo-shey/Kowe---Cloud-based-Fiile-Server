package com.mycompany.coursework;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


// <TableView fx:id="dataTableView" prefHeight="200.0" prefWidth="200.0" />


public class CreateFileController {
    
    private User user;
    
   
    @FXML
    private TextField fileNameTextField;
    
    @FXML
    private TextField fileContentTextField;

    @FXML
    private Button secondaryButton;
    
    /**
     * @brief save new File to server
     * @param event
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    
    @FXML
    private void saveBtnHandler(ActionEvent event) throws SQLException , IOException, ClassNotFoundException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");
        String name = fileNameTextField.getText();
        String content = fileContentTextField.getText();
        
        // ||
        if(name.isEmpty() || content.isEmpty()){
            dialogue("Incomplete Data","Both Fields are required");
        }else {
            FileManagement fileManager = new FileManagement(connection);
            try{
                fileManager.createFile(name, this.user.getUsername(), content);
                dialogue("Success","File Created");
            } catch(SQLException e) {
                dialogue("Internal Server Error","Something went wrong");
            }
        }
        
    }
    
    /**
     * @brief Display message dialogue
     * @param headerMsg
     * @param contentMsg 
     */
    
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
    
     /**
     * @brief change to home screen
     */
    @FXML
    private void switchToSecondary(){
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

    /**
     * @brief initialize data into controller
     * @param userdata
     **/
    public void initialise(User userdata) {
        this.user = userdata;
    }
}
