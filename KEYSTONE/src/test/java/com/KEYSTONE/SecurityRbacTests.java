package com.KEYSTONE;

import com.KEYSTONE.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityRbacTests {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("Should generate valid JWT token with user email and role claim")
    void testJwtTokenGeneration() {
        String token = jwtService.generateToken("manager@keystone.com", "MANAGER");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("manager@keystone.com", jwtService.extractEmail(token));
        assertEquals("MANAGER", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("Should generate tokens for all 4 RBAC roles and extract claims correctly")
    void testAllRolesTokenGeneration() {
        String[] roles = {"MANAGER", "DISPATCHER", "TECHNICIAN", "CUSTOMER"};
        for (String role : roles) {
            String email = role.toLowerCase() + "@keystone.com";
            String token = jwtService.generateToken(email, role);

            assertTrue(jwtService.isTokenValid(token));
            assertEquals(email, jwtService.extractEmail(token));
            assertEquals(role, jwtService.extractRole(token));
        }
    }

    @Test
    @DisplayName("Should reject malformed or tampered JWT token")
    void testInvalidJwtToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.structure"));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid(null));
    }
}
