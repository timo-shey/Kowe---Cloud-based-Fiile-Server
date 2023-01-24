package com.mycompany.coursework;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


// <TableView fx:id="dataTableView" prefHeight="200.0" prefWidth="200.0" />


public class CopyFileController {
    
    private User user;
    
   
    @FXML
    private TextField oldfilePathTextField;
    
    @FXML
    private TextField newFilePathTextField;

    @FXML
    private Button secondaryButton;
    
    @FXML
    private Spinner spinner;
    
    /**
     * @brief handle copy button click event and copy files from one folder to another
     * @param event
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException 
     */
    
    @FXML
    private void copyBtnHandler(ActionEvent event) throws SQLException , IOException, ClassNotFoundException, InterruptedException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");
        String name = spinner.getValue().toString();
        String newPath = newFilePathTextField.getText();
        
        if(name.isEmpty() || newPath.isEmpty()){
            dialogue("Incomplete Data","Select a file to delete");
        }else {
            FileManagement fileManager = new FileManagement(connection);
            try{
                fileManager.copyFile(name, newPath, this.user.getUsername());
                dialogue("Success","File Deleted");
            } catch(IOException e) {
                dialogue("Internal Server Error","IO Exception ");
            } catch (SQLException e) {
                dialogue("Internal Server Error","SQL Exception ");
            } catch (IllegalArgumentException e) {
                dialogue("Internal Server Error","File not found ");
            }
        }
        
    }
    
    /**
     *@brief  Display message dialogue
     *@param headerMsg,
     *@param contentMsg
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
    public void initialise(User userdata) throws SQLException, ClassNotFoundException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");
        this.user = userdata;
        FileManagement fileManager = new FileManagement(connection);
        
        ObservableList<String> files = FXCollections.observableArrayList(fileManager.getFilesForUser(this.user.getUsername()));
        
        // ObservableList<String> files = FXCollections.observableArrayList("timileyin.txt","sandy.txt","seun.txt");
        
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(files);
    
        spinner.setValueFactory(valueFactory);
    
    }
}
