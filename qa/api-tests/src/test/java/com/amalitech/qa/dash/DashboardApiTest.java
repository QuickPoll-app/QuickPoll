package com.amalitech.qa.dash;

import com.amalitech.qa.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Dashboard")
public class DashboardApiTest extends BaseTest {

    // ════════════════════════════════════════════════════════════════════════
    // DASH-001 / DASH-002 / DASH-003 — ACTIVE POLLS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            description = "DASH-001: Authenticated user gets all active polls — returns 200")
    @Story("DASH-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Any authenticated user can see all currently active polls on the dashboard")
    public void testGetActivePolls() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/dashboard/active")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("code", equalTo(200));
    }

    @Test(priority = 2,
            description = "DASH-001: Unauthenticated user cannot access active polls — returns 401")
    @Story("DASH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Unauthenticated request to active polls is rejected with 401")
    public void testGetActivePollsUnauthenticated() {
        given()
                .when()
                .get(BASE_URL + "/dashboard/active")
                .then()
                .statusCode(401);
    }

    @Test(priority = 3,
            description = "DASH-001: Admin can also get all active polls — returns 200")
    @Story("DASH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Admin user can see all currently active polls on the dashboard")
    public void testGetActivePollsAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(BASE_URL + "/dashboard/active")
                .then()
                .statusCode(200)
                .body("data", notNullValue());
    }

    // ════════════════════════════════════════════════════════════════════════
    // DASH-003 — TRENDING POLLS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            description = "DASH-003: Authenticated user gets trending polls — returns 200")
    @Story("DASH-003")
    @Severity(SeverityLevel.NORMAL)
    @Description("Any authenticated user can see trending polls on the dashboard")
    public void testGetTrendingPolls() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/dashboard/trending")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("code", equalTo(200));
    }

    @Test(priority = 5,
            description = "DASH-003: Unauthenticated user cannot access trending polls — returns 401")
    @Story("DASH-003")
    @Severity(SeverityLevel.NORMAL)
    @Description("Unauthenticated request to trending polls is rejected with 401")
    public void testGetTrendingPollsUnauthenticated() {
        given()
                .when()
                .get(BASE_URL + "/dashboard/trending")
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // DASH-005 — MY DASHBOARD (POLL CREATOR)
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 6,
            description = "DASH-005: User gets their own dashboard polls — returns 200")
    @Story("DASH-005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Logged-in user can see a summary of their own polls on the dashboard")
    public void testGetMyDashboard() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(BASE_URL + "/dashboard/me")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("code", equalTo(200));
    }

    @Test(priority = 7,
            description = "DASH-005: Admin gets their own dashboard polls — returns 200")
    @Story("DASH-005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Admin can see a summary of their own polls on the dashboard")
    public void testGetMyDashboardAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(BASE_URL + "/dashboard/me")
                .then()
                .statusCode(200)
                .body("data", notNullValue());
    }

    @Test(priority = 8,
            description = "DASH-005: Unauthenticated user cannot access my dashboard — returns 401")
    @Story("DASH-005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Unauthenticated request to personal dashboard is rejected with 401")
    public void testGetMyDashboardUnauthenticated() {
        given()
                .when()
                .get(BASE_URL + "/dashboard/me")
                .then()
                .statusCode(401);
    }

    // ════════════════════════════════════════════════════════════════════════
    // DASH-004 — ADMIN STATS (NOT IMPLEMENTED)
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 9,
            description = "DASH-004: Admin stats endpoint not yet implemented — returns 404")
    @Story("DASH-004")
    @Severity(SeverityLevel.NORMAL)
    @Description("Admin aggregate stats endpoint is not yet available — expected 404")
    public void testAdminStatsNotImplemented() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(BASE_URL + "/dashboard/stats")
                .then()
                .statusCode(anyOf(is(404), is(405)));
    }

}
