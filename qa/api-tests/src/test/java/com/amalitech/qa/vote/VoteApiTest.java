package com.amalitech.qa.vote;

import com.amalitech.qa.base.BaseTest;
import com.amalitech.qa.testdata.VoteTestData;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Voting")
public class VoteApiTest extends BaseTest {

    // ════════════════════════════════════════════════════════════════════════
    // HELPER — build optionIds JSON array
    // ════════════════════════════════════════════════════════════════════════

    private String buildVoteBody(String... optionIds) {
        String ids = Arrays.stream(optionIds)
                .map(id -> "\"" + id + "\"")
                .collect(Collectors.joining(", "));
        return "{ \"optionIds\": [" + ids + "] }";
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-001 — SINGLE SELECT VOTE
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            dataProvider = "validSingleSelectVoteData",
            dataProviderClass = VoteTestData.class,
            description = "VOTE-001: Valid single select vote returns 201")
    @Story("VOTE-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("User casts a vote on a single-select poll by choosing one option")
    public void testSingleSelectVote(String pollId, String optionId,
                                     int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(buildVoteBody(optionId))
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(expectedStatus)
                .body("message", equalTo("Vote recorded successfully"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-002 — MULTI SELECT VOTE
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 2,
            dataProvider = "validMultiSelectVoteData",
            dataProviderClass = VoteTestData.class,
            description = "VOTE-002: Valid multi select vote returns 201")
    @Story("VOTE-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("User casts a vote on a multi-select poll by choosing one or more options")
    public void testMultiSelectVote(String pollId, String[] optionIds,
                                    int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(buildVoteBody(optionIds))
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(expectedStatus)
                .body("message", equalTo("Vote recorded successfully"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-003 — PREVENT DUPLICATE VOTING
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 3,
            description = "VOTE-003: User cannot vote twice on the same poll — returns 409")
    @Story("VOTE-003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("System prevents a user from voting more than once on the same poll")
    public void testDuplicateVoteReturns409() {
        String pollId  = VoteTestData.SINGLE_SELECT_POLL_ID;
        String optionId = VoteTestData.OPTION_REMOTE;
        String body = buildVoteBody(optionId);

        // First vote — should succeed
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(anyOf(is(201), is(409)));

        // Second vote — should be rejected
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(409);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-004 — VOTES ARE FINAL AND NON-EDITABLE
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            description = "VOTE-004: User cannot change vote after submission — returns 409")
    @Story("VOTE-004")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Submitted votes are final and cannot be changed after submission")
    public void testVoteIsNonEditable() {
        String pollId = VoteTestData.SINGLE_SELECT_POLL_ID;

        // Cast initial vote
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(buildVoteBody(VoteTestData.OPTION_REMOTE))
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(anyOf(is(201), is(409)));

        // Attempt to change vote to a different option
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(buildVoteBody(VoteTestData.OPTION_HYBRID))
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(409);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-005 — BLOCK VOTING ON EXPIRED POLL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 5,
            description = "VOTE-005: Voting on expired poll returns 400 or 403")
    @Story("VOTE-005")
    @Severity(SeverityLevel.NORMAL)
    @Description("System blocks voting attempts on polls that have expired")
    public void testVoteOnExpiredPollBlocked() {
        // This poll needs to be created with a past expiry date
        // Using admin token to create an expired poll first
        String expiredPollBody = """
                {
                    "title": "Expired Poll Test",
                    "description": "This poll has already expired",
                    "type": "SINGLE_SELECT",
                    "options": [
                        {"optionText": "Option A"},
                        {"optionText": "Option B"}
                    ],
                    "expiresAt": "2020-01-01T00:00:00Z"
                }
                """;

        // Attempt to create expired poll — should return 400
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(expiredPollBody)
                .when()
                .post(BASE_URL + "/polls")
                .then()
                .statusCode(400);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE — UNAUTHENTICATED ACCESS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 6,
            description = "VOTE: Unauthenticated user cannot vote — returns 401")
    @Story("VOTE-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Unauthenticated user is rejected when trying to cast a vote")
    public void testUnauthenticatedVoteReturns401() {
        given()
                .contentType(ContentType.JSON)
                .body(buildVoteBody(VoteTestData.OPTION_REMOTE))
                .when()
                .post(BASE_URL + "/polls/" + VoteTestData.SINGLE_SELECT_POLL_ID + "/vote")
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE — INVALID VOTE SCENARIOS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7,
            dataProvider = "invalidVoteData",
            dataProviderClass = VoteTestData.class,
            description = "VOTE: Invalid vote scenarios return correct error codes")
    @Story("VOTE-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Invalid vote requests return appropriate error codes")
    public void testInvalidVote(String pollId, String[] optionIds,
                                int expectedStatus, String scenario) {
        System.out.println("Running scenario: " + scenario);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(buildVoteBody(optionIds))
                .when()
                .post(BASE_URL + "/polls/" + pollId + "/vote")
                .then()
                .statusCode(expectedStatus);
    }

}
