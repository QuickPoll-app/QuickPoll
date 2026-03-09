package com.amalitech.qa.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class BaseTest {

    // ── URLs ──────────────────────────────────────────────────────────────
    protected static final String BASE_URL = "http://localhost:8080/api";
    protected static final String AUTH_URL = BASE_URL + "/auth";

    // ── Default Credentials ───────────────────────────────────────────────
    protected static final String ADMIN_EMAIL    = "admin@amalitech.com";
    protected static final String USER_EMAIL     = "user@amalitech.com";
    protected static final String VALID_PASSWORD = "password123";

    // ── Shared Tokens ─────────────────────────────────────────────────────
    protected static String userToken;
    protected static String adminToken;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
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

}