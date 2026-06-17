package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void testLoginAdmin() {
        UserService userService = new UserService();
        
        assertDoesNotThrow(() -> {
            userService.login("admin", "admin");
        });
    }
}