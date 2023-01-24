/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.coursework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ntu-user
 */
public class Test {

    public static void main(String[] args) throws InvalidKeySpecException, SQLException, ClassNotFoundException, FileNotFoundException, IOException, InterruptedException {

        UserManagement user = new UserManagement();

        String username = "Seun";
        List<String> recipientUsername = Arrays.asList("test-user");
        String password = "password";
        String firstName = "Oluwaseun";       
        String lastName = "Sofunde";
        String email = "seun@gmail.com";
        String content = "suscipit adipiscing bibendum est ultricies integer quis auctor elit sed vulputate mi sit amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero nunc consequat interdum varius sit amet mattis vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor posuere ac ut consequat semper viverra nam libero justo laoreet sit amet cursus sit amet dictum sit amet justo donec enim diam vulputate ut pharetra sit amet aliquam id diam maecenas ultricies mi eget mauris pharetra et ultrices neque ornare aenean euismod elementum nisi quis eleifend quam adipiscing vitae proin sagittis nisl rhoncus mattis rhoncus urna neque viverra justo nec ultrices dui sapien eget mi proin sed libero enim sed faucibus turpis in eu mi bibendum neque egestas congue quisque egestas diam in arcu cursus euismod quis viverra nibh cras pulvinar mattis nunc sed blandit libero volutpat sed cras ornare arcu dui vivamus arcu felis bibendum ut tristique et egestas quis ipsum suspendisse ultrices gravida dictum fusce ut placerat orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in pellentesque massa placerat duis ultricies lacus sed turpis tincidunt id aliquet risus feugiat in ante metus dictum at tempor commodo ullamcorper a lacus vestibulum sed arcu non odio euismod lacinia at quis risus sed vulputate odio ut enim blandit volutpat maecenas volutpat blandit aliquam etiam erat velit scelerisque in dictum non consectetur a erat nam at lectus urna duis convallis convallis tellus id interdum velit laoreet id donec ultrices tincidunt arcu non sodales neque sodales ut etiam sit amet nisl purus in mollis nunc sed id semper risus in hendrerit gravida rutrum quisque non tellus orci ac auctor augue mauris augue neque gravida in fermentum et sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor aliquam nulla facilisi cras fermentum odio eu feugiat pretium nibh ipsum consequat nisl vel pretium lectus quam id leo in vitae turpis massa sed elementum tempus egestas sed sed risus pretium quam vulputate dignissim suspendisse in est ante in nibh mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing commodo elit at imperdiet dui accumsan sit amet nulla facilisi morbi tempus iaculis urna id volutpat lacus laoreet non curabitur gravida arcu ac tortor dignissim convallis aenean et tortor at risus viverra adipiscing at in tellus integer feugiat scelerisque varius morbi enim nunc faucibus a pellentesque sit amet porttitor eget dolor morbi non arcu risus quis varius quam quisque id diam vel quam elementum pulvinar etiam non quam lacus suspendisse faucibus interdum posuere lorem ipsum dolor sit amet consectetur adipiscing elit duis tristique sollicitudin nibh sit amet commodo nulla facilisi nullam vehicula ipsum a arcu cursus vitae congue mauris rhoncus aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas maecenas pharetra convallis posuere morbi leo urna molestie at elementum eu facilisis sed odio morbi quis commodo odio aenean sed adipiscing";
        String content2 = "Sample content. Let's see.";
        Boolean readPermission = true;
        Boolean writePermission = true;
        
        
        user.createUser(firstName, lastName, email, username, password);
//        user.updateUser(1, firstName, lastName, email, username, password, "1", "0");
//        user.deleteUser(username);
//        user.userLogin("test-user", password);
//        user.userLogin(username, password);
//        user.userLogout(username);


// Establish a connection to the database
        Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");

// Create an instance of the FileManagement class
        FileManagement fileManagement = new FileManagement(connection);
        
        

        // Read the input file into an InputStream
        String fileName = "TestFile3";
        String fileName2 = "myFile.txt";
        String fileName3 = "Sweet";
        String newFileName  = "SampleTest.txt";
        String filePath = "/home/ntu-user/userFiles/";
        String copyFilePath = "/home/ntu-user/userFiles/Copy/";
        String moveFilePath = "/home/ntu-user/userFiles/Move/";
        
        
       // //Create the file.
       //fileManagement.createFile(fileName, username, content);
           
        // Get Files For User
//        fileManagement.getFilesForUser(username);

        // Upload File
        //fileManagement.uploadFile(fileName, username);    
        
        //Download File
//        fileManagement.downloadFile("renamed-example", username);
        
        //Share File
//        fileManagement.shareFile(fileName, username, recipientUsername, readPermission, writePermission);
        
        //Delete a file.
//        fileManagement.deleteFile(fileName, username);

        //Restore a deleted file.
//        fileManagement.restoreFile(fileName, username);   
        
        //Rename a file.
//        fileManagement.renameFile(fileName, "renamed-example", username);

        //Copy a file.
//        fileManagement.copyFile("renamed-example", copyFilePath, username);

        //Move a file.
//        fileManagement.moveFile("renamed-example", moveFilePath, username);

    }

}

