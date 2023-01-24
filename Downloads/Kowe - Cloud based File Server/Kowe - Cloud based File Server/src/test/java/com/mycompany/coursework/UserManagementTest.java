/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.coursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ntu-user
 */
public class UserManagementTest {
    
    public UserManagementTest() {
    }

    UserManagement userManagement = new UserManagement();
    
   @Test
    public void testUserManager() {
        assertDoesNotThrow(() -> {
            UserManagement userManager = new UserManagement();
        });
    }

    /**
     * Test of createUser method, of class UserManagement.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateUser() throws Exception {
        UserManagement userManager = new UserManagement();
        assertDoesNotThrow(() -> {
            String response = userManager.createUser("John", "Doe", "johndoe@example.com", "johndoe", "password");
            assertEquals("User: johndoe's account has been created successfully.", response);
        });
        
    }

    /**
     * Test of updateUser method, of class UserManagement.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateUser() throws Exception {
        UserManagement userManager = new UserManagement();
        assertDoesNotThrow(() -> {
            String response = userManager.updateUser(1, "Jane", "Doe", "janedoe@example.com", "janedoe", "password", "true", "false");
            assertEquals("User: janedoe's account has been updated successfully.", response);
        });
    }

    /**
     * Test of userLogin method, of class UserManagement.
     * @throws java.lang.Exception
     */
    @Test
    public void testUserLogin() throws Exception {
        UserManagement userManager = new UserManagement();
        
        //Test with valid login details
        assertDoesNotThrow(() -> {
            userManager.userLogin("johndoe", "password");
        });
    }

    /**
     * Test of userLogout method, of class UserManagement.
     * @throws java.lang.Exception
     */
    @Test
    public void testUserLogout() throws Exception {
        UserManagement userManager = new UserManagement();
        assertDoesNotThrow(() -> {
            userManager.userLogout("johndoe");
        });
    }
    
    /**
     * Test of deleteUser method, of class UserManagement.
     * @throws java.lang.Exception
     */
    @Test
    public void testDeleteUser() throws Exception {
        UserManagement userManager = new UserManagement();
        assertDoesNotThrow(() -> {
            String response = userManager.deleteUser("johndoe");
            assertEquals("User: johndoe's account has been deleted successfully.", response);
        });
    }
    
}
