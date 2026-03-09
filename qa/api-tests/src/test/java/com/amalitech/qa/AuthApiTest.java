package com.amalitech.qa;

import com.amalitech.qa.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthApiTest extends BaseTest {

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-001 — USER REGISTRATION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1, description = "AUTH-001: Successful registration returns 201")
    public void testSuccessfulRegistration() {
        String body = """
                {
                    "name": "Test User",
                    "email": "testuser@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("testuser@amalitech.com"))
                .body("role", equalTo("USER"));
    }

    @Test(priority = 2, description = "AUTH-001: Duplicate email returns 409")
    public void testDuplicateEmailRegistration() {
        String body = """
                {
                    "name": "Test User",
                    "email": "user@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(409)
                .body("message", containsString("Email already in use"));
    }

    @Test(priority = 3, description = "AUTH-001: Missing name returns 400")
    public void testRegistrationMissingName() {
        String body = """
                {
                    "email": "newuser@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(400);
    }

    @Test(priority = 4, description = "AUTH-001: Missing email returns 400")
    public void testRegistrationMissingEmail() {
        String body = """
                {
                    "name": "Test User",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(400);
    }

    @Test(priority = 5, description = "AUTH-001: Invalid email format returns 400")
    public void testRegistrationInvalidEmailFormat() {
        String body = """
                {
                    "name": "Test User",
                    "email": "notanemail",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(400);
    }

    @Test(priority = 6, description = "AUTH-001: Password under 8 characters returns 400")
    public void testRegistrationShortPassword() {
        String body = """
                {
                    "name": "Test User",
                    "email": "shortpass@amalitech.com",
                    "password": "123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(400);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-002 — USER LOGIN
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7, description = "AUTH-002: Successful login returns 200 with JWT")
    public void testSuccessfulLogin() {
        String body = """
                {
                    "email": "user@amalitech.com",
                    "password": "password123"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        userToken = response.jsonPath().getString("token");
    }

    @Test(priority = 8, description = "AUTH-002: Wrong password returns 401")
    public void testLoginWrongPassword() {
        String body = """
                {
                    "email": "user@amalitech.com",
                    "password": "wrongpassword"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(401);
    }

    @Test(priority = 9, description = "AUTH-002: Unregistered email returns 401")
    public void testLoginUnregisteredEmail() {
        String body = """
                {
                    "email": "nobody@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(401);
    }

    @Test(priority = 10, description = "AUTH-002: Empty email returns 400")
    public void testLoginEmptyEmail() {
        String body = """
                {
                    "email": "",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(400);
    }

    @Test(priority = 11, description = "AUTH-002: Empty password returns 400")
    public void testLoginEmptyPassword() {
        String body = """
                {
                    "email": "user@amalitech.com",
                    "password": ""
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(400);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-003 — JWT TOKEN VALIDATION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 12, description = "AUTH-003: Valid JWT allows access to protected endpoint")
    public void testValidJwtAccessesProtectedEndpoint() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(200);
    }

    @Test(priority = 13, description = "AUTH-003: No JWT returns 401")
    public void testNoJwtReturns401() {
        given()
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(401);
    }

    @Test(priority = 14, description = "AUTH-003: Tampered JWT returns 401")
    public void testTamperedJwtReturns401() {
        given()
                .header("Authorization", "Bearer thisIsAFakeAndTamperedToken12345")
                .when()
                .get(BASE_URL + "/polls")
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-005 — SEED ACCOUNTS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 15, description = "AUTH-005: Admin seed account login returns ADMIN role")
    public void testAdminSeedAccountLogin() {
        String body = """
                {
                    "email": "admin@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("role", equalTo("ADMIN"));
    }

    @Test(priority = 16, description = "AUTH-005: User seed account login returns USER role")
    public void testUserSeedAccountLogin() {
        String body = """
                {
                    "email": "user@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("role", equalTo("USER"));
    }

    @Test(priority = 17, description = "AUTH-005: Registering with admin email returns 409")
    public void testRegisterWithAdminEmailReturns409() {
        String body = """
                {
                    "name": "Fake Admin",
                    "email": "admin@amalitech.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(AUTH_URL + "/register")
                .then()
                .statusCode(409);
    }

    // ════════════════════════════════════════════════════════════════════════
    // AUTH-006 — LOGIN ERROR MESSAGES
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 18, description = "AUTH-006: Error message does not reveal which field failed")
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
                : "Error message reveals password is wrong — security violation";
        assert !message.toLowerCase().contains("email not found")
                : "Error message reveals email does not exist — security violation";
    }

}
