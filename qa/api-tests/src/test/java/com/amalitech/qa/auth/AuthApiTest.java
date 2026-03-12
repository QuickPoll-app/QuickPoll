package com.amalitech.qa.auth;

import com.amalitech.qa.base.BaseTest;
import com.amalitech.qa.testdata.AuthTestData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class AuthApiTest extends BaseTest {

    // Real user UUID from staging DB
    private static final String TEST_USER_UUID = "550e8400-e29b-41d4-a716-446655440001";

    // ════════════════════════════════════════════════════════════════════════
    // CLEANUP — Delete test-registered users before all tests run
    // ════════════════════════════════════════════════════════════════════════

    @BeforeClass
    public void cleanUpBeforeTests() {
        String[] testEmails = {
                "johndoe@amalitech.com",
                "janesmith@amalitech.com",
                "testuser@amalitech.com"
        };

        String cleanupAdminToken = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "admin@amalitech.com",
                            "password": "password123"
                        }
                        """)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(200)
                .extract().response()
                .jsonPath().getString("data.token");

        for (String email : testEmails) {
            try {
                Response userResponse = given()
                        .header("Authorization", "Bearer " + cleanupAdminToken)
                        .when()
                        .get(BASE_URL + "/users/email/" + email)
                        .then()
                        .extract().response();

                if (userResponse.statusCode() == 200) {
                    String userId = userResponse.jsonPath().getString("id");
                    given()
                            .header("Authorization", "Bearer " + cleanupAdminToken)
                            .when()
                            .delete(BASE_URL + "/users/" + userId);
                    System.out.println("Pre-cleaned user: " + email);
                }
            } catch (Exception e) {
                System.out.println("Pre-clean skipped for: " + email);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CLEANUP — Delete test-registered users after all tests run
    // ════════════════════════════════════════════════════════════════════════

    @AfterClass
    public void cleanUpTestUsers() {
        String[] testEmails = {
                "johndoe@amalitech.com",
                "janesmith@amalitech.com",
                "testuser@amalitech.com"
        };

        // Login as admin to get fresh token for cleanup
        String cleanupAdminToken = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "admin@amalitech.com",
                            "password": "password123"
                        }
                        """)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(200)
                .extract().response()
                .jsonPath().getString("data.token");

        for (String email : testEmails) {
            try {
                // Get user UUID by email
                Response userResponse = given()
                        .header("Authorization", "Bearer " + cleanupAdminToken)
                        .when()
                        .get(BASE_URL + "/users/email/" + email)
                        .then()
                        .extract().response();

                if (userResponse.statusCode() == 200) {
                    String userId = userResponse.jsonPath().getString("id");
                    // Delete user
                    given()
                            .header("Authorization", "Bearer " + cleanupAdminToken)
                            .when()
                            .delete(BASE_URL + "/users/" + userId)
                            .then()
                            .extract().response();
                    System.out.println("Cleaned up test user: " + email);
                }
            } catch (Exception e) {
                System.out.println("Could not clean up user: " + email + " — " + e.getMessage());
            }
        }

        // Reset test user role back to USER after role update tests
        try {
            given()
                    .header("Authorization", "Bearer " + cleanupAdminToken)
                    .when()
                    .patch(BASE_URL + "/users/" + TEST_USER_UUID + "/role-update?role=USER")
                    .then()
                    .extract().response();
            System.out.println("Reset test user role back to USER");
        } catch (Exception e) {
            System.out.println("Could not reset user role: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-001 — USER REGISTRATION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            dataProvider = "validRegistrationData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-001: Valid registration returns 201")
    public void testValidRegistration(String name, String email,
                                      String password, int expectedStatus,
                                      String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """, name, email, password);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(expectedStatus)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/auth/register-response.json"))
                .body("data.email", equalTo(email))
                .body("data.role", equalTo("USER"));
    }

    @Test(priority = 2,
            dataProvider = "invalidRegistrationData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-001: Invalid registration returns 400")
    public void testInvalidRegistration(String name, String email,
                                        String password, int expectedStatus,
                                        String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """, name, email, password);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(expectedStatus);
    }

    @Test(priority = 3,
            dataProvider = "duplicateEmailData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-001: Duplicate email returns 409")
    public void testDuplicateEmailRegistration(String name, String email,
                                               String password,
                                               int expectedStatus,
                                               String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """, name, email, password);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(expectedStatus)
                .body("message", containsString("Email already in use"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-002 — USER LOGIN
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            dataProvider = "validLoginData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-002: Valid login returns 200 with JWT and role")
    public void testValidLogin(String email, String password,
                               int expectedStatus, String expectedRole,
                               String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, email, password);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(expectedStatus)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/auth/login-response.json"))
                .body("data.token", notNullValue())
                .body("data.role", equalTo(expectedRole))
                .extract().response();

        if (expectedRole.equals("USER")) {
            userToken = response.jsonPath().getString("data.token");
        }
        if (expectedRole.equals("ADMIN")) {
            adminToken = response.jsonPath().getString("data.token");
        }
    }

    @Test(priority = 5,
            dataProvider = "invalidLoginData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-002: Invalid login returns correct error code")
    public void testInvalidLogin(String email, String password,
                                 int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, email, password);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(expectedStatus);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-003 — JWT TOKEN VALIDATION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 6,
            description = "AUTH-003: Valid JWT allows access to protected endpoint",
            dependsOnMethods = "testValidLogin")
    public void testValidJwtAccessesProtectedEndpoint() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(200);
    }

    @Test(priority = 7,
            description = "AUTH-003: No JWT returns 401")
    public void testNoJwtReturns401() {
        given()
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(401);
    }

    @Test(priority = 8,
            dataProvider = "invalidTokenData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-003: Invalid tokens return 401")
    public void testInvalidTokenReturns401(String token,
                                           int expectedStatus,
                                           String scenario) {
        System.out.println("Running scenario: " + scenario);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(expectedStatus);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-006 — LOGIN ERROR MESSAGES
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 9,
            description = "AUTH-006: Error message does not reveal which field failed")
    public void testLoginErrorMessageIsGeneric() {
        String body = """
                {
                    "email": "user@amalitech.com",
                    "password": "wrongpassword"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(401)
                .extract().response();

        String message = response.jsonPath().getString("message");

        assert !message.toLowerCase().contains("password incorrect")
                : "SECURITY VIOLATION: Error reveals password is wrong";
        assert !message.toLowerCase().contains("email not found")
                : "SECURITY VIOLATION: Error reveals email does not exist";
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-007 — ROLE MANAGEMENT
    // PATCH /api/users/{userId}/role-update?role=ADMIN
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 10,
            description = "AUTH-007: Non-admin cannot update user role — returns 403",
            dependsOnMethods = "testValidLogin")
    public void testNonAdminCannotUpdateRole() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .patch(BASE_URL + "/users/" + TEST_USER_UUID + "/role-update?role=ADMIN")
                .then()
                .statusCode(403);
    }

    @Test(priority = 11,
            description = "AUTH-007: Admin can update user role successfully",
            dependsOnMethods = "testValidLogin")
    public void testAdminCanUpdateUserRole() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .patch(BASE_URL + "/users/" + TEST_USER_UUID + "/role-update?role=ADMIN")
                .then()
                .statusCode(200);
    }

    @Test(priority = 12,
            description = "AUTH-007: Updating role for non-existent user returns 404",
            dependsOnMethods = "testValidLogin")
    public void testUpdateRoleNonExistentUser() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .patch(BASE_URL + "/users/00000000-0000-0000-0000-000000000000/role-update?role=ADMIN")
                .then()
                .statusCode(404);
    }

    @Test(priority = 13,
            dataProvider = "invalidRoleData",
            dataProviderClass = AuthTestData.class,
            description = "AUTH-007: Invalid role values return 400",
            dependsOnMethods = "testValidLogin")
    public void testInvalidRoleValueReturns400(String role,
                                               int expectedStatus,
                                               String scenario) {
        System.out.println("Running scenario: " + scenario);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .patch(BASE_URL + "/users/" + TEST_USER_UUID + "/role-update?role=" + role)
                .then()
                .statusCode(expectedStatus);
    }

}
