///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
// */
//package com.mycompany.coursework;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.security.spec.InvalidKeySpecException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// *
// * @author ntu-user
// */
//public class FileManagementTest {
//
//    Connection connection = DriverManager.getConnection("jdbc:sqlite:users_db");
//
//    UserManagement userManagement = new UserManagement();
//    FileManagement fileManagement = new FileManagement(connection);
//
//    private String firstName = "Seun1";
//    private String lastName = "Sanni1";
//    private String email = "sample11@email.com";
//    private String username = "Sweez11";
//    private String pass = "password";
//    private String fileName = "test_file.txt";
//    private String filePath = "/home/ntu-user/userFiles/";
//    private String destinationPathMove = "/home/ntu-user/userFiles/Move";
//    private String newContent = "This is new content.";
//
//    public FileManagementTest() throws SQLException, InvalidKeySpecException, ClassNotFoundException, IOException {
//        this.connection = DriverManager.getConnection("jdbc:sqlite:users_db");
//    }
//
//    public void cleanup() throws SQLException {
//        PreparedStatement stmt = null;
//        stmt = connection.prepareStatement("DELETE FROM files WHERE file_name = ?");
//        stmt.setString(1, fileName);
//        stmt.executeUpdate();
//
//        stmt = connection.prepareStatement("DELETE FROM chunks WHERE file_name = ?");
//        stmt.setString(1, fileName);
//        stmt.executeUpdate();
//
//        stmt = connection.prepareStatement("DELETE FROM users WHERE username = ?");
//        stmt.setString(1, username);
//        stmt.executeUpdate();
//    }
//
//    /**
//     * Test of createFile method, of class FileManagement.
//     *
//     * @throws java.io.IOException
//     * @throws java.security.spec.InvalidKeySpecException
//     * @throws java.sql.SQLException
//     */
//    @org.junit.jupiter.api.Test
//    public void testCreateFile() throws IOException, InvalidKeySpecException, SQLException, ClassNotFoundException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, email, username, pass);
//            userManagement.userLogin(username, pass);
//            fileManagement.createFile(fileName, username, newContent);
//
//            File file = new File(filePath + fileName);
//            assertTrue(file.exists());
//
//            file.delete();
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testGetFilesForUser() throws SQLException, IOException, ClassNotFoundException, InvalidKeySpecException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sample10@email.com", "Sweez10", pass);
//            userManagement.userLogin("Sweez10", pass);
//            fileManagement.createFile("test_file1.txt", "Sweez10", newContent);
//            File file = new File(filePath + "test_file1.txt");
//            List<String> files = fileManagement.getFilesForUser("Sweez10");
//
//            assertTrue(files.contains("test_file1.txt"));
//            file.delete();
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testUploadFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sample2@email.com", "Sweez2", pass);
//            userManagement.userLogin("Sweez2", pass);
//            fileManagement.createFile("test_file2.txt", "Sweez2", newContent);
//            fileManagement.uploadFile("test_file2.txt", "Sweez2");
//
//            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
//            stmt.setString(1, "test_file2.txt");
//            ResultSet rs = stmt.executeQuery();
//            assertEquals("test_file2.txt", rs.getString("file_name"));
//
//            stmt = connection.prepareStatement("SELECT * FROM chunks WHERE file_name = ?");
//            stmt.setString(1, "test_file2.txt");
//            rs = stmt.executeQuery();
//
//            assertEquals("test_file2.txt", rs.getString("file_name"));
//            assertEquals("test_file2.txt" + "_chunk_0.txt", rs.getString("chunk_filename"));
//
//            stmt = connection.prepareStatement("DELETE FROM files WHERE file_name = ?");
//            stmt.setString(1, "test_file2.txt");
//            stmt.executeUpdate();
//            stmt = connection.prepareStatement("DELETE FROM chunks WHERE file_name = ?");
//            stmt.setString(1, "test_file2.txt");
//            stmt.executeUpdate();
//            stmt = connection.prepareStatement("DELETE FROM users WHERE username = ?");
//            stmt.setString(1, "Sweez2");
//            stmt.executeUpdate();
//            String filePath2 = "/home/ntu-user/userFiles/";
//
//            File file = new File(filePath2 + "test_file2.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//        }
//        connection.close();
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testDownloadFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sample20@email.com", "Sweez9", pass);
//            userManagement.userLogin("Sweez9", pass);
//            fileManagement.createFile("test_file9.txt", "Sweez9", newContent);
//            fileManagement.uploadFile("test_file9.txt", "Sweez9");
//
//            File file = fileManagement.downloadFile("test_file9.txt", "Swee9");
//
//            String content = Files.readString(file.toPath());
//            assertEquals(newContent, content);
//
//            file.delete();
//            File fileToDelete = new File(filePath + "test_file9.txt");
//            fileToDelete.delete();
//            cleanup();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testShareFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        List<String> recipientUsernames = new ArrayList<>();
//        recipientUsernames.add("recipient1");
//        recipientUsernames.add("recipient2");
//        try {
//            userManagement.createUser(firstName, lastName, "sample9@email.com", "Sweez8", pass);
//            userManagement.userLogin("Sweez8", pass);
//            fileManagement.createFile("test_file8.txt", "Sweez8", newContent);
//            fileManagement.uploadFile("test_file8.txt", "Sweez8");
//            fileManagement.shareFile("test_file8.txt", "Sweez8", recipientUsernames, true, false);
//
//            stmt = connection.prepareStatement("SELECT * FROM file_permission WHERE file_name = ?");
//            stmt.setString(1, "test_file8.txt");
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                assertEquals(recipientUsernames, rs.getString("recipient_user_id"));
//                assertTrue(rs.getInt("read_permission") == 1);
//                assertTrue(rs.getInt("write_permission") == 0);
//            }
//
//            stmt = connection.prepareStatement("DELETE FROM file_permission WHERE file_name = ?");
//            stmt.setString(1, "test_file8.txt");
//            stmt.executeUpdate();
//
//            File file = new File(filePath + "test_file8.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testDeleteFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sampl8@email.com", "Sweez6", pass);
//            userManagement.userLogin("Sweez6", pass);
//            fileManagement.createFile("test_file6.txt", "Sweez6", newContent);
//            fileManagement.uploadFile("test_file6.txt", "Sweez6");
//            fileManagement.deleteFile("test_file6.txt", "Sweez6");
//
//            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
//            stmt.setString(1, "test_file6.txt");
//            ResultSet rs = stmt.executeQuery();
//            assertFalse(rs.next());
//
//            stmt = connection.prepareStatement("SELECT * FROM chunks WHERE file_name = ?");
//            stmt.setString(1, "test_file6.txt");
//            rs = stmt.executeQuery();
//            assertFalse(rs.next());
//
//            stmt = connection.prepareStatement("DELETE FROM users WHERE username = ?");
//            stmt.setString(1, "Sweez6");
//            stmt.executeUpdate();
//
//            File file = new File(filePath + "test_file6.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testRestoreFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sampl9@email.com", "Sweez7", pass);
//            userManagement.userLogin("Sweez7", pass);
//            fileManagement.createFile("test_file7.txt", "Sweez7", newContent);
//            fileManagement.uploadFile("test_file7.txt", "Sweez7");
//            fileManagement.deleteFile("test_file7.txt", "Sweez7");
//            fileManagement.restoreFile(fileName, "Sweez7");
//
//            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND deleted_at IS NULL");
//            stmt.setString(1, fileName);
//            ResultSet rs = stmt.executeQuery();
//            assertTrue(rs.next());
//
//            File file = new File(filePath + "test_file7.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testRenameFile() throws IOException, SQLException, InterruptedException, InvalidKeySpecException, ClassNotFoundException {
//        String oldName = "test_file5.txt";
//        String newName = "renamedTestFile";
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sampl5@email.com", "Sweez5", pass);
//            userManagement.userLogin("Sweez5", pass);
//            fileManagement.createFile("test_file5.txt", "Sweez5", newContent);
//
//            File file = new File(filePath + "test_file5.txt");
//            assertTrue(file.exists());
//
//            try {
//                fileManagement.renameFile(oldName, newName, "Sweez5");
//                fail("Expected IllegalArgumentException for user not logged in.");
//            } catch (IllegalArgumentException e) {
//                assertEquals("User is not logged in. Cannot rename file.", e.getMessage());
//            }
//
//            try {
//                fileManagement.renameFile(oldName, newName, "Sweez5");
//                fail("Expected IllegalArgumentException for file not existing.");
//            } catch (IllegalArgumentException e) {
//                assertEquals("File does not exist. Cannot rename file.", e.getMessage());
//            }
//
//            try {
//                fileManagement.renameFile(oldName, newName, "Sweez5");
//                fail("Expected IllegalArgumentException for new file name already in use.");
//            } catch (IllegalArgumentException e) {
//                assertEquals("New file name is already in use. Please choose a different file name.", e.getMessage());
//            }
//
//            fileManagement.renameFile(oldName, newName, "Sweez5");
//            File renamedFile = new File("./uploads/" + newName);
//            assertTrue(renamedFile.exists());
//            assertFalse(new File("./uploads/" + oldName).exists());
//            
//
//            File file2 = new File(filePath + newName);
//            if (file2.exists()) {
//                file2.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testCopyFile() throws InvalidKeySpecException, SQLException, ClassNotFoundException, IOException, InterruptedException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "sampl4@email.com", "Sweez4", pass);
//            userManagement.userLogin("Sweez4", pass);
//            fileManagement.createFile("test_file4.txt", "Sweez4", newContent);
//            fileManagement.uploadFile("test_file4.txt", "Sweez4");
//            fileManagement.downloadFile("test_file4.txt", "Sweez4");
//            fileManagement.copyFile("test_file4.txt", filePath, "Sweez4");
//            File file = new File(filePath + "test_file4-copy.txt");
//            assertTrue(file.exists());
//            
//            if (file.exists()) {
//                file.delete();
//            }
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//            connection.close();
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testMoveFile() throws IOException, InvalidKeySpecException, SQLException, ClassNotFoundException {
//        PreparedStatement stmt = null;
//        try {
//            userManagement.createUser(firstName, lastName, "samp3@email.com", "Sweez3", pass);
//            userManagement.userLogin("Sweez3", pass);
//            fileManagement.createFile("test_file3.txt", "Sweez3", newContent);
//            fileManagement.moveFile("test_file3.txt", destinationPathMove, "Sweez3");
//
//            File file2 = new File(filePath + "test_file3.txt");
//            assertTrue(file2.exists());
//            file2.delete();
//            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
//            stmt.setString(1, "test_file3.txt");
//            ResultSet rs = stmt.executeQuery();
//            assertTrue(rs.next());
//            assertEquals(destinationPathMove, rs.getString("file_path"));
//            cleanup();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (stmt != null) {
//                stmt.close();
//            }
//        }
//        connection.close();
//    }
//}
