package com.amalitech.qa.poll;

import com.amalitech.qa.base.BaseTest;
import com.amalitech.qa.testdata.PollTestData;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class PollApiTest extends BaseTest {

    private static final String POLLS_URL = BASE_URL + "/polls";
    private static String pollId;

    // ── Login before all poll tests ───────────────────────────────────────
    @BeforeClass
    public void loginBeforeTests() {
        loginAsUser();
        loginAsAdmin();
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-001 — CREATE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            dataProvider = "validPollCreationData",
            dataProviderClass = PollTestData.class,
            description = "POLL-001: Valid poll creation returns 201")
    public void testValidPollCreation(String title, String description,
                                      String type, int expectedStatus,
                                      String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "title": "%s",
                    "description": "%s",
                    "type": "%s",
                    "options": [
                        {"optionText": "Option A"},
                        {"optionText": "Option B"}
                    ]
                }
                """, title, description, type);

        Response response = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(expectedStatus)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/poll/poll-response.json"))
                .body("title", equalTo(title))
                .body("type", equalTo(type))
                .body("status", equalTo("OPEN"))
                .extract().response();

        // Save pollId for use in later tests
        if (pollId == null) {
            pollId = response.jsonPath().getString("pollId");
        }
    }

    @Test(priority = 2,
            dataProvider = "invalidPollCreationData",
            dataProviderClass = PollTestData.class,
            description = "POLL-001: Invalid poll creation returns 400")
    public void testInvalidPollCreation(String title, String type,
                                        int optionCount, int expectedStatus,
                                        String scenario) {
        System.out.println("Running scenario: " + scenario);

        StringBuilder options = new StringBuilder("[");
        for (int i = 0; i < optionCount; i++) {
            options.append(String.format(
                    "{\"optionText\": \"Option %d\"}", i + 1));
            if (i < optionCount - 1) options.append(",");
        }
        options.append("]");

        String body = String.format("""
                {
                    "title": "%s",
                    "type": "%s",
                    "options": %s
                }
                """, title, type, options);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(expectedStatus);
    }

    @Test(priority = 3,
            description = "POLL-001: Unauthenticated poll creation returns 401")
    public void testUnauthenticatedPollCreation() {
        given()
                .contentType(ContentType.JSON)
                .body(PollTestData.VALID_SINGLE_SELECT_POLL)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-002 — POLL TYPE SELECTION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            dataProvider = "pollTypeData",
            dataProviderClass = PollTestData.class,
            description = "POLL-002: Poll type validation")
    public void testPollTypeSelection(String type, int expectedStatus,
                                      String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "title": "Poll Type Test",
                    "type": "%s",
                    "options": [
                        {"optionText": "Option A"},
                        {"optionText": "Option B"}
                    ]
                }
                """, type);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(expectedStatus);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-003 — POLL OPTIONS MINIMUM
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 5,
            description = "POLL-003: Poll with exactly 2 options is created successfully")
    public void testPollWithExactlyTwoOptions() {
        String body = """
                {
                    "title": "Minimum Options Poll",
                    "type": "SINGLE_SELECT",
                    "options": [
                        {"optionText": "Option A"},
                        {"optionText": "Option B"}
                    ]
                }
                """;

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(201);
    }

    @Test(priority = 6,
            description = "POLL-003: Poll with blank option text returns 400")
    public void testPollWithBlankOptionText() {
        String body = """
                {
                    "title": "Blank Option Poll",
                    "type": "SINGLE_SELECT",
                    "options": [
                        {"optionText": ""},
                        {"optionText": "Option B"}
                    ]
                }
                """;

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(400)
                .body("message", containsString("Option text cannot be empty"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-004 — EDIT POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7,
            dataProvider = "validPollUpdateData",
            dataProviderClass = PollTestData.class,
            description = "POLL-004: Creator can update poll before voting",
            dependsOnMethods = "testValidPollCreation")
    public void testCreatorCanUpdatePoll(String newTitle,
                                         int expectedStatus,
                                         String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "title": "%s"
                }
                """, newTitle);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(POLLS_URL + "/" + pollId)
                .then()
                .statusCode(expectedStatus)
                .body("title", equalTo(newTitle));
    }

    @Test(priority = 8,
            description = "POLL-004: Non-creator cannot update poll",
            dependsOnMethods = "testValidPollCreation")
    public void testNonCreatorCannotUpdatePoll() {
        String body = """
                {
                    "title": "Unauthorized Update"
                }
                """;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(POLLS_URL + "/" + pollId)
                .then()
                .statusCode(403);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-005 — DELETE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 9,
            description = "POLL-005: Non-creator cannot delete poll",
            dependsOnMethods = "testValidPollCreation")
    public void testNonCreatorCannotDeletePoll() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete(POLLS_URL + "/" + pollId)
                .then()
                .statusCode(403);
    }

    @Test(priority = 10,
            description = "POLL-005: Creator can delete their own poll",
            dependsOnMethods = "testValidPollCreation")
    public void testCreatorCanDeletePoll() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete(POLLS_URL + "/" + pollId)
                .then()
                .statusCode(204);
    }

    @Test(priority = 11,
            description = "POLL-005: Deleted poll returns 404",
            dependsOnMethods = "testCreatorCanDeletePoll")
    public void testDeletedPollReturns404() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLLS_URL + "/" + pollId)
                .then()
                .statusCode(404);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-006 — VIEW ACTIVE POLLS LIST
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 12,
            description = "POLL-006: Authenticated user can get active polls list")
    public void testGetActivePollsList() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLLS_URL)
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test(priority = 13,
            description = "POLL-006: Unauthenticated user cannot get polls list")
    public void testUnauthenticatedCannotGetPollsList() {
        given()
                .when()
                .get(POLLS_URL)
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-007 — VIEW POLL DETAILS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 14,
            description = "POLL-007: Get poll details with valid ID",
            dependsOnMethods = "testValidPollCreation")
    public void testGetPollDetailsValidId() {
        // Create a fresh poll for this test
        Response createResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(PollTestData.VALID_SINGLE_SELECT_POLL)
                .when()
                .post(POLLS_URL)
                .then()
                .statusCode(201)
                .extract().response();

        String freshPollId = createResponse.jsonPath().getString("pollId");

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLLS_URL + "/" + freshPollId)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/poll/poll-response.json"))
                .body("pollId", notNullValue())
                .body("title", notNullValue())
                .body("type", notNullValue())
                .body("status", equalTo("OPEN"))
                .body("options", hasSize(greaterThanOrEqualTo(2)));
    }

    @Test(priority = 15,
            description = "POLL-007: Get poll details with invalid ID returns 404")
    public void testGetPollDetailsInvalidId() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLLS_URL + "/99999")
                .then()
                .statusCode(404);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-008 — ADMIN POLL MANAGEMENT
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 16,
            description = "POLL-008: Admin can view all polls")
    public void testAdminCanViewAllPolls() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(BASE_URL + "/admin/polls")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test(priority = 17,
            description = "POLL-008: Non-admin cannot access admin polls endpoint")
    public void testNonAdminCannotAccessAdminPolls() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/admin/polls")
                .then()
                .statusCode(403);
    }

}