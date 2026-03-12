package com.amalitech.qa.testdata;

import org.testng.annotations.DataProvider;

public class AuthTestData {

    // ════════════════════════════════════════════════════════════════════════
    // VALID REQUEST BODIES
    // ════════════════════════════════════════════════════════════════════════

    public static final String VALID_REGISTRATION_BODY = """
            {
                "name": "Test User",
                "email": "testuser@amalitech.com",
                "password": "password123"
            }
            """;

    public static final String VALID_LOGIN_BODY = """
            {
                "email": "user@amalitech.com",
                "password": "password123"
            }
            """;

    public static final String VALID_ADMIN_LOGIN_BODY = """
            {
                "email": "admin@amalitech.com",
                "password": "password123"
            }
            """;

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-001 — REGISTRATION DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid registration scenarios
     * Columns: name, email, password, expectedStatus, scenario
     */
    @DataProvider(name = "validRegistrationData")
    public static Object[][] validRegistrationData() {
        return new Object[][] {
                {
                        "John Doe",
                        "johndoe@amalitech.com",
                        "password123",
                        201,
                        "Valid registration with all fields"
                },
                {
                        "Jane Smith",
                        "janesmith@amalitech.com",
                        "securePass456",
                        201,
                        "Valid registration with different credentials"
                }
        };
    }

    /**
     * Invalid registration scenarios
     * Columns: name, email, password, expectedStatus, scenario
     */
    @DataProvider(name = "invalidRegistrationData")
    public static Object[][] invalidRegistrationData() {
        return new Object[][] {
                {
                        "",
                        "testuser@amalitech.com",
                        "password123",
                        400,
                        "Missing name"
                },
                {
                        "Test User",
                        "",
                        "password123",
                        400,
                        "Missing email"
                },
                {
                        "Test User",
                        "notanemail",
                        "password123",
                        400,
                        "Invalid email format"
                },
                {
                        "Test User",
                        "testuser@amalitech.com",
                        "123",
                        400,
                        "Password too short (under 8 characters)"
                },
                {
                        "Test User",
                        "testuser@amalitech.com",
                        "",
                        400,
                        "Missing password"
                },
                {
                        "",
                        "",
                        "",
                        400,
                        "All fields empty"
                }
        };
    }

    /**
     * Duplicate email registration scenarios
     * Columns: name, email, password, expectedStatus, scenario
     */
    @DataProvider(name = "duplicateEmailData")
    public static Object[][] duplicateEmailData() {
        return new Object[][] {
                {
                        "Fake User",
                        "user@amalitech.com",
                        "password123",
                        409,
                        "Duplicate user email"
                },
                {
                        "Fake Admin",
                        "admin@amalitech.com",
                        "password123",
                        409,
                        "Duplicate admin email"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-002 — LOGIN DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid login scenarios
     * Columns: email, password, expectedStatus, expectedRole, scenario
     */
    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return new Object[][] {
                {
                        "user@amalitech.com",
                        "password123",
                        200,
                        "USER",
                        "Valid user login"
                },
                {
                        "admin@amalitech.com",
                        "password123",
                        200,
                        "ADMIN",
                        "Valid admin login"
                }
        };
    }

    /**
     * Invalid login scenarios
     * Columns: email, password, expectedStatus, scenario
     */
    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        return new Object[][] {
                {
                        "user@amalitech.com",
                        "wrongpassword",
                        401,
                        "Wrong password"
                },
                {
                        "nobody@amalitech.com",
                        "password123",
                        401,
                        "Unregistered email"
                },
                {
                        "",
                        "password123",
                        400,
                        "Empty email"
                },
                {
                        "user@amalitech.com",
                        "",
                        400,
                        "Empty password"
                },
                {
                        "",
                        "",
                        400,
                        "Both fields empty"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-003 — JWT TOKEN DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Invalid token scenarios
     * Columns: token, expectedStatus, scenario
     */
    @DataProvider(name = "invalidTokenData")
    public static Object[][] invalidTokenData() {
        return new Object[][] {
                {
                        "thisIsAFakeAndTamperedToken12345",
                        401,
                        "Tampered token"
                },
                {
                        "",
                        401,
                        "Empty token"
                },
                {
                        "Bearer ",
                        401,
                        "Bearer with no token"
                },
                {
                        "eyJhbGciOiJIUzI1NiJ9.expiredpayload.invalidsignature",
                        401,
                        "Expired token format"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-007 — ROLE MANAGEMENT DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Invalid role update scenarios
     * Columns: role, expectedStatus, scenario
     */
    @DataProvider(name = "invalidRoleData")
    public static Object[][] invalidRoleData() {
        return new Object[][] {
                {
                        "SUPERUSER",
                        400,
                        "Invalid role value"
                },
                {
                        "",
                        400,
                        "Empty role value"
                },
                {
                        "admin",
                        400,
                        "Lowercase role value"
                }
        };
    }

}
