package com.amalitech.qa.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTest {

    // ── URLs ──────────────────────────────────────────────────────────────
    protected static String BASE_URL;
    protected static String AUTH_URL;

    // ── Default Credentials ───────────────────────────────────────────────
    protected static String ADMIN_EMAIL;
    protected static String USER_EMAIL;
    protected static String VALID_PASSWORD;

    // ── Shared Tokens ─────────────────────────────────────────────────────
    protected static String userToken;
    protected static String adminToken;

    // ── Read Parameters from testng.xml ───────────────────────────────────
    @BeforeClass
    @Parameters({"baseUrl", "adminEmail", "userEmail", "validPassword"})
    public void setup(
            @Optional("http://localhost:8080/api") String baseUrl,
            @Optional("admin@amalitech.com")       String adminEmail,
            @Optional("user@amalitech.com")        String userEmail,
            @Optional("password123")               String validPassword
    ) {
        BASE_URL       = baseUrl;
        AUTH_URL       = BASE_URL + "/auth";
        ADMIN_EMAIL    = adminEmail;
        USER_EMAIL     = userEmail;
        VALID_PASSWORD = validPassword;

        RestAssured.baseURI = BASE_URL;

        System.out.println("========================================");
        System.out.println("Test Environment Setup");
        System.out.println("Base URL    : " + BASE_URL);
        System.out.println("Admin Email : " + ADMIN_EMAIL);
        System.out.println("User Email  : " + USER_EMAIL);
        System.out.println("========================================");
    }

    // ── Reusable Login Helper ─────────────────────────────────────────────
    protected String loginAndGetToken(String email, String password) {
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
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getString("token");
    }

    // ── Login as User ─────────────────────────────────────────────────────
    protected void loginAsUser() {
        userToken = loginAndGetToken(USER_EMAIL, VALID_PASSWORD);
    }

    // ── Login as Admin ────────────────────────────────────────────────────
    protected void loginAsAdmin() {
        adminToken = loginAndGetToken(ADMIN_EMAIL, VALID_PASSWORD);
    }

    // ── Reusable Given Helper ─────────────────────────────────────────────
    protected static io.restassured.specification.RequestSpecification given() {
        return io.restassured.RestAssured.given()
                .contentType(ContentType.JSON);
    }

}