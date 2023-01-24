/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.coursework;

import java.io.BufferedReader;

/**
 *
 * @author ntu-user
 * N1076622
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManagement {

    private Connection connection;

    public FileManagement(Connection connection) {
        this.connection = connection;
    }

    DbConnection con = new DbConnection();

    /**
     * @brief method to create file and append content to the file.
     * @param fileName
     * @param username
     * @param content
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void createFile(String fileName, String username, String content) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:users_db");

            String filePath = "/home/ntu-user/userFiles/";

            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not loggedin. Cannot create a File.");
            }
            int userId = rs.getInt("id");

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            ResultSet rs2 = stmt.executeQuery();
            if (rs2.next()) {
                throw new IllegalArgumentException("File already exists. Please choose a different file name.");
            }

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            File file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();

                int fileSize = (int) file.length();

                stmt = connection.prepareStatement("INSERT INTO files (file_name, file_path, file_size, user_id) VALUES (?, ?, ?, ?)");
                stmt.setString(1, fileName);
                stmt.setString(2, filePath);
                stmt.setInt(3, fileSize);
                stmt.setInt(4, userId);
                stmt.executeUpdate();
            }

            this.appendToFile(fileName, filePath, content);
        } catch (SQLException e) {
//            con.logger(username, FileManagement.class.getName(), "createFile", (Level.SEVERE).toString(), "", e);
            e.printStackTrace();
        } finally {
            stmt.close();
            connection.close();
        }
    }

    /**
     * @brief to append content to a file.
     * @param fileName
     * @param filePath
     * @param newContent
     * @throws IOException
     * @throws SQLException
     */
    public void appendToFile(String fileName, String filePath, String newContent) throws IOException, SQLException {

        if (!fileName.contains(".txt")) {
            fileName = fileName + ".txt";
        }

        try ( FileWriter fileWriter = new FileWriter(filePath + fileName, true)) {

            fileWriter.write(newContent);
        }

        File file = new File(filePath + fileName);
        long fileSize = file.length();

        PreparedStatement stmt = connection.prepareStatement("UPDATE files SET file_size = ? WHERE file_name = ?");
        stmt.setLong(1, fileSize);
        stmt.setString(2, fileName);
        stmt.executeUpdate();
    }

    /**
     * @brief method to get all files of a user
     * @param username
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<String> getFilesForUser(String username) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = null;
        List<String> fileNames = new ArrayList<>();

        try {
            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not loggedin. Cannot retrieve list of files.");
            }

            int userId = rs.getInt("id");

            stmt = connection.prepareStatement("SELECT file_name FROM files WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs2 = stmt.executeQuery();

            while (rs2.next()) {
                fileNames.add(rs2.getString("file_name"));
            }

            if (fileNames.isEmpty()) {
                throw new IllegalArgumentException("No files found for the user.");
            }

        } catch (SQLException e) {
//            con.logger(username, FileManagement.class.getName(), "getFilesForUser", (Level.SEVERE).toString(), "", e);
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            connection.close();
        }

        System.out.println("Files for user " + username + ": " + fileNames);

        return fileNames;
    }

    /**
     * @brief method to upload a file.
     * @param fileName
     * @param username
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void uploadFile(String fileName, String username) throws IOException, ClassNotFoundException {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not logged in. Cannot upload file.");
            }

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
            stmt.setString(1, fileName);
            ResultSet rs2 = stmt.executeQuery();

            if (rs2.next()) {
                throw new IllegalArgumentException("File already exists. Please choose a different file name.");
            }

            int userId = rs.getInt("id");

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            Path filePath = Paths.get("/home/ntu-user/userFiles/" + fileName);
            String content = Files.readString(filePath);

            List<String> chunks = splitIntoChunks(content);
            List<String> chunkFilenames = new ArrayList<>();

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < chunks.size(); i++) {
                final int chunkIndex = i;
                final String chunkFilename = fileName + "_chunk_" + chunkIndex + ".txt";
                chunkFilenames.add(chunkFilename);
                final String fileName1 = fileName;

                executor.execute(() -> {
                    try {
                        Path chunkPath = Paths.get("./uploads/" + chunkFilename);
                        Files.writeString(chunkPath, chunks.get(chunkIndex));

                        final String containerIpAddress;

                        switch (chunkIndex) {
                            case 1:
                                containerIpAddress = "172.18.0.5";
                                break;
                            case 2:
                                containerIpAddress = "172.18.0.6";
                                break;
                            case 3:
                                containerIpAddress = "172.18.0.3";
                                break;
                            default:
                                containerIpAddress = "172.18.0.4";
                                break;
                        }

                        String chunkFilePath = "./uploads/" + chunkFilename;
                        String containerFilePath = "/userFiles";

                        ProcessBuilder pb = new ProcessBuilder("scp", "-P", "22", chunkFilePath, "root@" + containerIpAddress + ":" + containerFilePath);
                        pb.inheritIO();
                        Process p = pb.start();
                        p.waitFor();

                        try ( PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO chunks (file_name, chunk_filename, user_Id, container_index) VALUES (?, ?, ?, ?)")) {
                            stmt2.setString(1, fileName1);
                            stmt2.setString(2, chunkFilenames.get(chunkIndex));
                            stmt2.setInt(3, userId);
                            stmt2.setInt(4, chunkIndex);
                            stmt2.executeUpdate();
                        }
                    } catch (IOException | InterruptedException | SQLException e) {
                        System.out.println("Exception in thread \"" + Thread.currentThread().getName() + "\" " + chunkFilename + ": " + e.getMessage());
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
            }

            String filePath2 = "/home/ntu-user/userFiles/";

            File file = new File(filePath2 + fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (SQLException e) {
//            con.logger(username, FileManagement.class.getName(), "uploadFile", (Level.SEVERE).toString(), "", e);
        }
    }

    /**
     * @brief method to split files into chunks to be uploaded to the separate
     * containers.
     * @param content
     * @return
     */
    private List<String> splitIntoChunks(String content) {
        List<String> chunks = new ArrayList<>();
        int chunkSize = content.length() / 4;

        for (int i = 0; i < 4; i++) {
            int startIndex = i * chunkSize;
            int endIndex = startIndex + chunkSize;
            if (i == 3) {
                endIndex = content.length();
            }
            chunks.add(content.substring(startIndex, endIndex));
        }
        return chunks;
    }

    /**
     * @brief method to download file from the containers.
     * @param fileName
     * @param username
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public File downloadFile(String fileName, String username) throws IOException, SQLException {
        // Validate user access details
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);

        ResultSet rs = stmt.executeQuery();

        if (rs.getInt("isLoggedIn") == 0) {
            throw new IllegalArgumentException("User is not logged in. Cannot download file.");
        }

        if (!fileName.contains(".txt")) {
            fileName = fileName + ".txt";
        }

        stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
        stmt.setString(1, fileName);
        ResultSet rs2 = stmt.executeQuery();

        if (!rs2.next()) {
            throw new IllegalArgumentException("File does not exist. Please choose a different file name.");
        }

        stmt = connection.prepareStatement("SELECT * FROM chunks WHERE file_name = ?");
        stmt.setString(1, fileName);
        ResultSet rs3 = stmt.executeQuery();

        List<String> chunkFilenames = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        while (rs3.next()) {
            String chunkFilename = rs3.getString("chunk_filename");
            String containerIndex = rs3.getString("container_index");
            chunkFilenames.add(chunkFilename);

            String chunkContent;
            if (containerIndex != null) {
                final String containerIpAddress;
                switch (containerIndex) {
                    case "1":
                        containerIpAddress = "172.18.0.5";
                        break;
                    case "2":
                        containerIpAddress = "172.18.0.6";
                        break;
                    case "3":
                        containerIpAddress = "172.18.0.3";
                        break;
                    default:
                        containerIpAddress = "172.18.0.4";
                        break;
                }
                String containerFilePath = "/userFiles/" + chunkFilename;
                ProcessBuilder pb = new ProcessBuilder("ssh", "-p", "22", "root@" + containerIpAddress + ":" + containerFilePath);
                pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                chunkContent = output.toString();
            } else {
                throw new IllegalArgumentException("The file wasn't uploaded.");
            }

            sb.append(chunkContent);
        }

        Collections.sort(chunkFilenames);

        StringBuilder sb1 = new StringBuilder();
        for (String chunkFilename : chunkFilenames) {
            String chunkFilePath = "./uploads/" + chunkFilename;
            String chunkContent = Files.readString(Paths.get(chunkFilePath));
            sb1.append(chunkContent);
        }
        String combinedContent = sb1.toString();

        Path filePath = Paths.get("/home/ntu-user/userFiles/Downloads/" + fileName);

        Files.writeString(filePath, combinedContent);

        return filePath.toFile();
    }

    /**
     * @brief a method to share files among other users, specifying the read and
     * write permissions.
     * @param fileName
     * @param ownerUsername
     * @param recipientUsernames
     * @param readPermission
     * @param writePermission
     * @throws SQLException
     */
    public void shareFile(String fileName, String ownerUsername, List<String> recipientUsernames, boolean readPermission, boolean writePermission) throws SQLException {

        PreparedStatement stmt;
        stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, ownerUsername);

        ResultSet rs = stmt.executeQuery();

        if (rs.getInt("isLoggedIn") == 0) {
            throw new IllegalArgumentException("User is not logged in. Cannot download file.");
        }

        String recipientUsername1 = String.join(",", Collections.nCopies(recipientUsernames.size(), "?"));
        String sql = "SELECT * FROM users WHERE username IN (" + recipientUsername1 + ")";
        stmt = connection.prepareStatement(sql);
        for (int i = 0; i < recipientUsernames.size(); i++) {
            stmt.setString(i + 1, recipientUsernames.get(i));
        }

        ResultSet rs2 = stmt.executeQuery();

        if (!rs2.next()) {
            throw new IllegalArgumentException("Recipient User does not exist");
        }

        if (!fileName.contains(".txt")) {
            fileName = fileName + ".txt";
        }

        stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
        stmt.setString(1, fileName);
        ResultSet rs3 = stmt.executeQuery();

        if (!rs3.next()) {
            throw new IllegalArgumentException("File does not exist. Please choose a different file name.");
        }

        int ownerUserId = rs.getInt("id");

        for (String recipientUsername : recipientUsernames) {
            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, recipientUsername);
            ResultSet rs4 = stmt.executeQuery();

            if (!rs4.next()) {
                throw new IllegalArgumentException("Recipient user does not exist. Please choose a different recipient.");
            }

            int recipientUserId = rs4.getInt("id");
            int readPermissionInt = readPermission ? 1 : 0;
            int writePermissionInt = writePermission ? 1 : 0;

            stmt = connection.prepareStatement("INSERT INTO file_permission (file_name, owner_user_id, recipient_user_id, read_permission, write_permission) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, fileName);
            stmt.setInt(2, ownerUserId);
            stmt.setInt(3, recipientUserId);
            stmt.setInt(4, readPermissionInt);
            stmt.setInt(5, writePermissionInt);

            stmt.executeUpdate();
        }
    }

    /**
     * @brief method to delete files from the containers. Files will be deleted
     * permanently after 30 days of being deleted.
     * @param fileName
     * @param username
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteFile(String fileName, String username) throws SQLException, IOException, InterruptedException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not loggedin. Cannot delete a file.");
            }

            int userId = rs.getInt("id");

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            ResultSet rs2 = stmt.executeQuery();

            if (!rs2.next()) {
                throw new IllegalArgumentException("User does not have permission to delete the file/Incorrect file name.");
            }

            String filePath = rs2.getString("file_path");
            Timestamp deletedAt = rs2.getTimestamp("deleted_at");

            File file = new File(filePath + fileName);
            if (file.exists()) {
                file.delete();
            }

            stmt = connection.prepareStatement("UPDATE files SET deleted_at = CURRENT_TIMESTAMP WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("DELETE FROM chunks WHERE file_name = ? AND user_Id = ? AND deleted_at < DATETIME('now', '-30 days')");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("DELETE FROM files WHERE file_name = ? AND user_Id = ? AND deleted_at < DATETIME('now', '-30 days')");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("UPDATE chunks SET deleted_at = CURRENT_TIMESTAMP WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("DELETE FROM file_permission WHERE file_name = ? AND owner_user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("SELECT * FROM chunks WHERE file_name = ? AND user_Id = ? AND deleted_at > DATETIME('now', '-30 days')");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            ResultSet rs3 = stmt.executeQuery();

            while (rs3.next()) {
                String chunkFilename = rs3.getString("chunk_filename");
                int containerIndex = rs3.getInt("container_index");
                final String containerIpAddress;

                switch (containerIndex) {
                    case 1:
                        containerIpAddress = "172.18.0.5";
                        break;
                    case 2:
                        containerIpAddress = "172.18.0.6";
                        break;
                    case 3:
                        containerIpAddress = "172.18.0.3";
                        break;
                    default:
                        containerIpAddress = "172.18.0.4";
                        break;
                }

                ProcessBuilder pb = new ProcessBuilder("ssh", "-p", "22", "root@" + containerIpAddress, "rm", "/userFiles/" + chunkFilename);

                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                p.waitFor();
            }

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void restoreFile(String fileName, String username) throws InterruptedException, FileNotFoundException, IOException, SQLException {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new IllegalArgumentException("User does not exist. Cannot restore file.");
            }

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not logged in. Cannot restore file.");
            }

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND deleted_at IS NOT NULL");
            stmt.setString(1, fileName);
            ResultSet rs2 = stmt.executeQuery();

            if (!rs2.next()) {
                throw new IllegalArgumentException("File does not exist or has not been deleted. Cannot restore file.");
            }

            stmt = connection.prepareStatement("SELECT deleted_at FROM files WHERE file_name = ?");
            stmt.setString(1, fileName);
            ResultSet rs3 = stmt.executeQuery();

            if (rs3.next()) {
                Timestamp deletedAt = rs3.getTimestamp("deleted_at");

                if (deletedAt.before(new Timestamp(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000))) {
                    stmt = connection.prepareStatement("UPDATE files SET deleted_at = null WHERE file_name = ?");
                    stmt.setString(1, fileName);
                    stmt.executeUpdate();
                } else {
                    throw new SQLException("File cannot be restored because it has been deleted over 30 days.");
                }

            } else {
                throw new SQLException("File not found.");
            }

            int userId = rs.getInt("id");
            String filePath = "/home/ntu-user/Downloads/" + fileName;

            List<String> chunks = new ArrayList<>();
            List<String> chunkFilenames = new ArrayList<>();
            final String filename = fileName;

            stmt = connection.prepareStatement("SELECT chunk_filename FROM chunks WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);
            ResultSet rs4 = stmt.executeQuery();

            while (rs4.next()) {
                chunkFilenames.add(rs4.getString("chunk_filename"));
            }

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < chunkFilenames.size(); i++) {
                int chunkIndex = i;
                String chunkFilename = chunkFilenames.get(i);
                executor.execute(() -> {
                    try {
                        PreparedStatement stmt2 = connection.prepareStatement("SELECT deleted_at FROM chunks WHERE file_name = ? AND user_id = ? AND deleted_at IS NOT NULL");
                        stmt2.setString(1, filename);
                        stmt2.setInt(2, userId);
                        ResultSet rs5 = stmt2.executeQuery();
                        if (rs5.next()) {
                            Timestamp chunkDeletedAt = rs5.getTimestamp("deleted_at");
                            if (chunkDeletedAt.before(new Timestamp(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000))) {

                                final String containerIpAddress;
                                switch (chunkIndex) {
                                    case 1:
                                        containerIpAddress = "172.18.0.5";
                                        break;
                                    case 2:
                                        containerIpAddress = "172.18.0.6";
                                        break;
                                    case 3:
                                        containerIpAddress = "172.18.0.3";
                                        break;
                                    default:
                                        containerIpAddress = "172.18.0.4";
                                        break;
                                }
                                String containerFilePath = "/userFiles/" + chunkFilename;
                                String chunkFilePath = "./uploads/" + chunkFilename;

                                ProcessBuilder pb = new ProcessBuilder("scp", "-P", "22", chunkFilePath, "root@" + containerIpAddress + ":" + containerFilePath);
                                pb.inheritIO();
                                Process p = pb.start();
                                p.waitFor();

                                stmt2 = connection.prepareStatement("UPDATE chunks SET deleted_at = null WHERE file_name = ? AND user_id = ? AND chunk_filename = ?");
                                stmt2.setString(1, filename);
                                stmt2.setInt(2, userId);
                                stmt2.setString(3, chunkFilename);
                                stmt2.executeUpdate();

                                chunks.add(chunkFilename);
                            } else {
                                throw new SQLException("File chunk cannot be restored because it has been deleted over 30 days.");
                            }
                        }
                    } catch (SQLException e) {
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(FileManagement.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            Collections.sort(chunkFilenames);

            StringBuilder sb = new StringBuilder();
            for (String chunkFilename : chunkFilenames) {
                String chunkFilePath = "./uploads/" + chunkFilename;
                String chunkContent = Files.readString(Paths.get(chunkFilePath));
                sb.append(chunkContent);
            }

            String combinedContent = sb.toString();
            String restoredFilePath = "/home/ntu-user/Downloads/" + fileName;

            Files.writeString(Paths.get(restoredFilePath), combinedContent);

            stmt = connection.prepareStatement("UPDATE files SET deleted_at = null WHERE file_name = ?");
            stmt.setString(1, fileName);
            stmt.executeUpdate();

        } catch (SQLException e) {
        }
    }

    /**
     * @brief method to rename a file.
     * @param oldName
     * @param newName
     * @param username
     * @throws IOException
     * @throws SQLException
     * @throws InterruptedException
     */
    public void renameFile(String oldName, String newName, String username) throws IOException, SQLException, InterruptedException {

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.getInt("isLoggedIn") == 0) {
            throw new IllegalArgumentException("User is not logged in. Cannot rename file.");
        }

        if (!newName.contains(".txt")) {
            newName = newName + ".txt";
        }

        if (!oldName.contains(".txt")) {
            oldName = oldName + ".txt";
        }

        stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
        stmt.setString(1, oldName);
        ResultSet rs2 = stmt.executeQuery();
        if (!rs2.next()) {
            throw new IllegalArgumentException("File does not exist. Cannot rename file.");
        }

        stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ?");
        stmt.setString(1, newName);
        ResultSet rs3 = stmt.executeQuery();
        if (rs3.next()) {
            throw new IllegalArgumentException("New file name is already in use. Please choose a different file name.");
        }

        stmt = connection.prepareStatement("SELECT chunk_filename FROM chunks WHERE file_name = ?");
        stmt.setString(1, oldName);
        ResultSet rs4 = stmt.executeQuery();
        List<String> chunkFilenames = new ArrayList<>();
        while (rs4.next()) {
            chunkFilenames.add(rs4.getString("chunk_filename"));
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < chunkFilenames.size(); i++) {
            final int chunkIndex = i;
            final String chunkFilename = chunkFilenames.get(i);
            final String newChunkFilename = newName + "_chunk_" + chunkIndex + ".txt";
            final String containerIpAddress;
            final String newname = newName;

            stmt = connection.prepareStatement("SELECT container_index FROM chunks WHERE chunk_filename = ?");
            stmt.setString(1, chunkFilename);
            ResultSet rs5 = stmt.executeQuery();
            int containerIndex = rs5.getInt("container_index");
            switch (containerIndex) {
                case 1:
                    containerIpAddress = "172.18.0.5";
                    break;
                case 2:
                    containerIpAddress = "172.18.0.6";
                    break;
                case 3:
                    containerIpAddress = "172.18.0.3";
                    break;
                default:
                    containerIpAddress = "172.18.0.4";
                    break;
            }

            executor.execute(() -> {
                try {
                    Path file = Paths.get("./uploads/" + chunkFilename);
                    Path newFile = Paths.get("./uploads/" + newChunkFilename);
                    Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);

                    String chunkFilePath = "./uploads/" + newChunkFilename;
                    String containerFilePath = "/userFiles/" + newChunkFilename;

                    ProcessBuilder pb = new ProcessBuilder("scp", "-P", "22", chunkFilePath, "root@" + containerIpAddress + ":" + containerFilePath);

                    pb.inheritIO();
                    Process p = pb.start();
                    p.waitFor();

                    ProcessBuilder pb2 = new ProcessBuilder("ssh", "-p", "22", "root@" + containerIpAddress, "rm", "/userFiles/" + chunkFilename);

                    Process p2 = pb2.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    p2.waitFor();

                    PreparedStatement stmt2 = connection.prepareStatement("UPDATE chunks SET file_name = ?, chunk_filename = ? WHERE chunk_filename = ?");
                    stmt2.setString(1, newname);
                    stmt2.setString(2, newChunkFilename);
                    stmt2.setString(3, chunkFilename);
                    stmt2.executeUpdate();
                } catch (IOException | InterruptedException | SQLException e) {
                    System.out.println("Exception in thread \"" + Thread.currentThread().getName() + "\": " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        PreparedStatement stmt2 = connection.prepareStatement("UPDATE files SET file_name = ? WHERE file_name = ?");
        stmt2.setString(1, newName);
        stmt2.setString(2, oldName);
        stmt2.executeUpdate();
    }

    /**
     * @brief method to copy file from a source path to a destination path.
     * @param fileName
     * @param destinationPath
     * @param username
     * @throws SQLException
     * @throws IOException
     */
    public void copyFile(String fileName, String destinationPath, String username) throws SQLException, IOException {
        PreparedStatement stmt = null;
        try {

            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not loggedin. Cannot copy the file.");
            }

            String des_fileName = fileName + "-copy";

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            if (!des_fileName.contains(".txt")) {
                des_fileName = des_fileName + ".txt";
            }

            int userId = rs.getInt("id");

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);

            ResultSet rs2 = stmt.executeQuery();

            if (!rs2.next()) {
                throw new IllegalArgumentException("User does not have permission to copy the file/Incorrect file name.");
            }

            stmt = connection.prepareStatement("SELECT file_path, file_size FROM files WHERE file_name = ?");
            stmt.setString(1, fileName);
            ResultSet rs3 = stmt.executeQuery();

            if (!rs3.next()) {
                throw new IllegalArgumentException("The file does not exist.");
            }

            String filePath1 = "/home/ntu-user/userFiles/Downloads/" + fileName;
            int fileSize = rs3.getInt("file_size");
            String destinationPath2 = destinationPath + des_fileName;

            try {
                File sourceFile = new File(filePath1);
                File destinationFile = new File(destinationPath2);
                Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String fileContent = Files.readString(sourceFile.toPath());
                Files.writeString(destinationFile.toPath(), fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            stmt = connection.prepareStatement("INSERT INTO files (file_name, file_path, file_size, user_id) VALUES (?, ?, ?, ?)");
            stmt.setString(1, des_fileName);
            stmt.setString(2, destinationPath);
            stmt.setInt(3, fileSize);
            stmt.setInt(4, userId);

            stmt.executeUpdate();

            Path filePath = Paths.get("/home/ntu-user/userFiles/Downloads/" + fileName);
            String content = Files.readString(filePath);

            List<String> chunks = splitIntoChunks(content);
            List<String> chunkFilenames = new ArrayList<>();

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < chunks.size(); i++) {
                final int chunkIndex = i;
                final String chunkFilename = des_fileName + "_chunk_" + chunkIndex + ".txt";
                chunkFilenames.add(chunkFilename);
                final String fileName1 = des_fileName;

                executor.execute(() -> {
                    try {
                        Path chunkPath = Paths.get("./uploads/" + chunkFilename);
                        Files.writeString(chunkPath, chunks.get(chunkIndex));

                        final String containerIpAddress;

                        switch (chunkIndex) {
                            case 1:
                                containerIpAddress = "172.18.0.5";
                                break;
                            case 2:
                                containerIpAddress = "172.18.0.6";
                                break;
                            case 3:
                                containerIpAddress = "172.18.0.3";
                                break;
                            default:
                                containerIpAddress = "172.18.0.4";
                                break;
                        }

                        String chunkFilePath = "./uploads/" + chunkFilename;
                        String containerFilePath = "/userFiles";

                        ProcessBuilder pb = new ProcessBuilder("scp", "-P", "22", chunkFilePath, "root@" + containerIpAddress + ":" + containerFilePath);
                        pb.inheritIO();
                        Process p = pb.start();
                        p.waitFor();

                        try ( PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO chunks (file_name, chunk_filename, user_Id, container_index) VALUES (?, ?, ?, ?)")) {
                            stmt2.setString(1, fileName1);
                            stmt2.setString(2, chunkFilenames.get(chunkIndex));
                            stmt2.setInt(3, userId);
                            stmt2.setInt(4, chunkIndex);
                            stmt2.executeUpdate();
                        }
                    } catch (IOException | InterruptedException | SQLException e) {
                        System.out.println("Exception in thread \"" + Thread.currentThread().getName() + "\" " + chunkFilename + ": " + e.getMessage());
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * @brief method to move a file from a source path to a destination path.
     * @param fileName
     * @param destinationPath
     * @param username
     * @throws SQLException
     * @throws IOException
     */
    public void moveFile(String fileName, String destinationPath, String username) throws SQLException, IOException {
        PreparedStatement stmt = null;
        try {

            stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.getInt("isLoggedIn") == 0) {
                throw new IllegalArgumentException("User is not loggedin. Cannot move the file.");
            }

            String des_fileName = fileName + "-copy";

            if (!fileName.contains(".txt")) {
                fileName = fileName + ".txt";
            }

            if (!des_fileName.contains(".txt")) {
                des_fileName = des_fileName + ".txt";
            }

            int userId = rs.getInt("id");

            stmt = connection.prepareStatement("SELECT * FROM files WHERE file_name = ? AND user_id = ?");
            stmt.setString(1, fileName);
            stmt.setInt(2, userId);

            ResultSet rs2 = stmt.executeQuery();

            if (!rs2.next()) {
                throw new IllegalArgumentException("User does not have permission to move the file/Incorrect file name.");
            }

            stmt = connection.prepareStatement("SELECT file_path, file_size FROM files WHERE file_name = ?");
            stmt.setString(1, fileName);
            ResultSet rs3 = stmt.executeQuery();

            if (!rs3.next()) {
                throw new IllegalArgumentException("The file does not exist.");
            }

            String filePath1 = "/home/ntu-user/userFiles/Downloads/" + fileName;
            String destinationPath2 = destinationPath + des_fileName;

            try {
                File sourceFile = new File(filePath1);
                File destinationFile = new File(destinationPath2);
                Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }

            stmt = connection.prepareStatement("UPDATE files SET file_path = ? WHERE file_name = ?");
            stmt.setString(1, destinationPath);
            stmt.setString(2, fileName);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}