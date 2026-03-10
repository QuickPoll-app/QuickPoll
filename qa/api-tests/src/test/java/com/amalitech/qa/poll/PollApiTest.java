package com.amalitech.qa.poll;

import com.amalitech.qa.base.BaseTest;
import com.amalitech.qa.testdata.PollTestData;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.*;

import static org.hamcrest.Matchers.*;

@Epic("QuickPoll API")
@Feature("Poll Management")
public class PollApiTest extends BaseTest {

    private static String POLL_URL;
    private static String createdPollId;

    // ── Setup ──────────────────────────────────────────────────────────────

    @BeforeClass(dependsOnMethods = "setup")
    public void pollSetup() {
        POLL_URL = BASE_URL + "/polls";
        loginAsAdmin();
        loginAsUser();
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-001 — CREATE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            dataProvider = "validPollCreationData",
            dataProviderClass = PollTestData.class,
            description = "Admin can create polls with valid data")
    @Story("POLL-001: Create Poll")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an admin can successfully create a poll")
    public void testCreatePoll(String title, String description, String type,
                               int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = buildPollBody(title, description, type,
                new String[]{"Option A", "Option B", "Option C"}, null);

        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(expectedStatus)
                .body("data.title", equalTo(title))
                .body("data.type", equalTo(type))
                .body("data.status", notNullValue())
                .extract().response();

        // Save the last created poll ID for downstream tests
        String id = response.jsonPath().getString("data.id");
        if (id != null) createdPollId = id;
    }

    @Test(priority = 2,
            dataProvider = "invalidPollCreationData",
            dataProviderClass = PollTestData.class,
            description = "Poll creation fails with invalid data")
    @Story("POLL-001: Create Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that poll creation returns 400 for invalid request bodies")
    public void testInvalidPollCreation(String title, String type, int optionCount,
                                        int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        String[] options = buildOptions(optionCount);
        String body = buildPollBody(title, "Description", type, options, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(expectedStatus);
    }

    @Test(priority = 3,
            description = "Non-admin user cannot create a poll")
    @Story("POLL-001: Create Poll")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a regular user gets 403 when trying to create a poll")
    public void testCreatePollAsUserForbidden() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .body(PollTestData.VALID_SINGLE_SELECT_POLL)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(403);
    }

    @Test(priority = 4,
            description = "Unauthenticated user cannot create a poll")
    @Story("POLL-001: Create Poll")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a poll without a token returns 401")
    public void testCreatePollWithoutTokenUnauthorized() {
        given()
                .body(PollTestData.VALID_SINGLE_SELECT_POLL)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-002 — GET ALL POLLS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 5,
            description = "Authenticated user can retrieve all polls")
    @Story("POLL-002: View Polls")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that GET /polls returns a paginated list of polls")
    public void testGetAllPolls() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLL_URL)
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.content", notNullValue())
                .body("data.content.size()", greaterThanOrEqualTo(0));
    }

    @Test(priority = 6,
            description = "Unauthenticated user cannot retrieve polls")
    @Story("POLL-002: View Polls")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that GET /polls without token returns 401")
    public void testGetAllPollsWithoutToken() {
        given()
                .when()
                .get(POLL_URL)
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-003 — GET SINGLE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7,
            dependsOnMethods = "testCreatePoll",
            description = "Authenticated user can get a single poll by ID")
    @Story("POLL-003: View Single Poll")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that GET /polls/{id} returns the correct poll")
    public void testGetPollById() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLL_URL + "/" + createdPollId)
                .then()
                .statusCode(200)
                .body("data.id", equalTo(createdPollId))
                .body("data.title", notNullValue())
                .body("data.options", notNullValue());
    }

    @Test(priority = 8,
            description = "Getting a non-existent poll returns 404")
    @Story("POLL-003: View Single Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that GET /polls/{id} with an invalid ID returns 404")
    public void testGetPollByInvalidId() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(POLL_URL + "/non-existent-id-99999")
                .then()
                .statusCode(404);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-004 — EDIT POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 9,
            dependsOnMethods = "testCreatePoll",
            dataProvider = "validPollUpdateData",
            dataProviderClass = PollTestData.class,
            description = "Admin can update a poll before voting starts")
    @Story("POLL-004: Edit Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that admin can update poll title before voting starts")
    public void testUpdatePoll(String newTitle, int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body = String.format("""
                {
                    "title": "%s"
                }
                """, newTitle);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .put(POLL_URL + "/" + createdPollId)
                .then()
                .statusCode(expectedStatus)
                .body("data.title", equalTo(newTitle));
    }

    @Test(priority = 10,
            dependsOnMethods = "testCreatePoll",
            description = "Regular user cannot update a poll")
    @Story("POLL-004: Edit Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a regular user gets 403 when trying to update a poll")
    public void testUpdatePollAsUserForbidden() {
        String body = """
                {
                    "title": "Unauthorized Update Attempt"
                }
                """;

        given()
                .header("Authorization", "Bearer " + userToken)
                .body(body)
                .when()
                .put(POLL_URL + "/" + createdPollId)
                .then()
                .statusCode(403);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-005 — CLOSE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 11,
            dependsOnMethods = "testCreatePoll",
            description = "Admin can close an active poll")
    @Story("POLL-005: Close Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that admin can close an active poll")
    public void testClosePoll() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .patch(POLL_URL + "/" + createdPollId + "/close")
                .then()
                .statusCode(200)
                .body("data.status", equalTo("CLOSED"));
    }

    @Test(priority = 12,
            dependsOnMethods = "testClosePoll",
            description = "Regular user cannot close a poll")
    @Story("POLL-005: Close Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a regular user gets 403 when trying to close a poll")
    public void testClosePollAsUserForbidden() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .patch(POLL_URL + "/" + createdPollId + "/close")
                .then()
                .statusCode(403);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-006 — EXPIRY DATE
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 13,
            dataProvider = "expiryDateData",
            dataProviderClass = PollTestData.class,
            description = "Poll expiry date validation")
    @Story("POLL-006: Poll Expiry")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that expiry date is validated correctly on poll creation")
    public void testPollExpiryDate(String expiresAt, int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        String body;
        if (expiresAt.isEmpty()) {
            body = PollTestData.VALID_SINGLE_SELECT_POLL;
        } else {
            body = String.format("""
                    {
                        "title": "Expiry Test Poll",
                        "description": "Testing expiry",
                        "type": "SINGLE_SELECT",
                        "options": [
                            {"optionText": "Option A"},
                            {"optionText": "Option B"}
                        ],
                        "expiresAt": "%s"
                    }
                    """, expiresAt);
        }

        given()
                .header("Authorization", "Bearer " + adminToken)
                .body(body)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(expectedStatus);
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-007 — DELETE POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 14,
            dependsOnMethods = "testCreatePoll",
            description = "Regular user cannot delete a poll")
    @Story("POLL-007: Delete Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a regular user gets 403 when trying to delete a poll")
    public void testDeletePollAsUserForbidden() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete(POLL_URL + "/" + createdPollId)
                .then()
                .statusCode(403);
    }

    @Test(priority = 15,
            dependsOnMethods = {"testCreatePoll", "testClosePoll"},
            description = "Admin can delete a poll")
    @Story("POLL-007: Delete Poll")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that admin can delete a poll")
    public void testDeletePoll() {
        // Create a fresh poll to delete
        Response response = given()
                .header("Authorization", "Bearer " + adminToken)
                .body(PollTestData.VALID_SINGLE_SELECT_POLL)
                .when()
                .post(POLL_URL)
                .then()
                .statusCode(201)
                .extract().response();

        String pollToDelete = response.jsonPath().getString("data.id");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete(POLL_URL + "/" + pollToDelete)
                .then()
                .statusCode(204);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private String buildPollBody(String title, String description, String type,
                                 String[] options, String expiresAt) {
        StringBuilder optionJson = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            optionJson.append(String.format("{\"optionText\": \"%s\"}", options[i]));
            if (i < options.length - 1) optionJson.append(",");
        }

        String body = String.format("""
                {
                    "title": "%s",
                    "description": "%s",
                    "type": "%s",
                    "options": [%s]
                    %s
                }
                """,
                title, description, type, optionJson,
                expiresAt != null ? ", \"expiresAt\": \"" + expiresAt + "\"" : "");

        return body;
    }

    private String[] buildOptions(int count) {
        if (count == 0) return new String[]{};
        String[] options = new String[count];
        for (int i = 0; i < count; i++) {
            options[i] = "Option " + (char)('A' + i);
        }
        return options;
    }
}
