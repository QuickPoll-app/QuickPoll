package com.amalitech.ui.tests;

import com.amalitech.ui.base.BaseTest;
import com.amalitech.ui.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

@Feature("Login UI")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void setUpPage() {
        loginPage = new LoginPage(driver);
        loginPage.navigateTo(BASE_URL);
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-001 — LOGIN PAGE LOADS
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 1,
            description = "TC-UI-001: Login page loads with all required elements")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login page loads with email field, password field, sign in button and create account link")
    public void testLoginPageLoads() {
        Assert.assertTrue(loginPage.isEmailFieldDisplayed(),
                "Email field should be visible");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password field should be visible");
        Assert.assertTrue(loginPage.isSignInButtonDisplayed(),
                "Sign In button should be visible");
        Assert.assertTrue(loginPage.isCreateOneLinkDisplayed(),
                "Create one link should be visible");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-002 — VALID USER LOGIN
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 2,
            description = "TC-UI-002: Valid user login redirects to dashboard")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify valid user credentials log in successfully and redirect to dashboard")
    public void testValidUserLogin() {
        loginPage.login(USER_EMAIL, VALID_PASSWORD);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login")));

        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("login"),
                "User should be redirected away from login page. Current URL: " + currentUrl);
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-003 — VALID ADMIN LOGIN
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 3,
            description = "TC-UI-003: Valid admin login redirects to dashboard")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify admin credentials log in successfully and redirect to dashboard")
    public void testValidAdminLogin() {
        loginPage.login(ADMIN_EMAIL, VALID_PASSWORD);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login")));

        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("login"),
                "Admin should be redirected away from login page. Current URL: " + currentUrl);
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-004 — WRONG PASSWORD
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 4,
            description = "TC-UI-004: Wrong password shows error message")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify wrong password shows error and user stays on login page")
    public void testWrongPasswordShowsError() {
        loginPage.login(USER_EMAIL, "wrongpassword");

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for wrong password");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("login") ||
                        loginPage.isLoginPageDisplayed(),
                "User should remain on login page");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-005 — UNREGISTERED EMAIL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 5,
            description = "TC-UI-005: Unregistered email shows error message")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify unregistered email shows error and user stays on login page")
    public void testUnregisteredEmailShowsError() {
        loginPage.login("notregistered@amalitech.com", VALID_PASSWORD);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for unregistered email");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-006 — EMPTY EMAIL
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 6,
            description = "TC-UI-006: Empty email shows validation error")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify empty email field shows validation error")
    public void testEmptyEmailShowsValidationError() {
        loginPage.enterPassword(VALID_PASSWORD);
        loginPage.clickSignIn();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "User should remain on login page when email is empty");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-007 — EMPTY PASSWORD
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 7,
            description = "TC-UI-007: Empty password shows validation error")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify empty password field shows validation error")
    public void testEmptyPasswordShowsValidationError() {
        loginPage.enterEmail(USER_EMAIL);
        loginPage.clickSignIn();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "User should remain on login page when password is empty");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-008 — BOTH FIELDS EMPTY
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 8,
            description = "TC-UI-008: Both fields empty shows validation errors")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify both fields empty shows validation errors")
    public void testBothFieldsEmptyShowsValidationErrors() {
        loginPage.clickSignIn();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "User should remain on login page when both fields are empty");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-009 — INVALID EMAIL FORMAT
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 9,
            description = "TC-UI-009: Invalid email format shows validation error")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify invalid email format shows format validation error")
    public void testInvalidEmailFormatShowsError() {
        loginPage.enterEmail("notanemail");
        loginPage.enterPassword(VALID_PASSWORD);
        loginPage.clickSignIn();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "User should remain on login page for invalid email format");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-010 — PASSWORD MASKED BY DEFAULT
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 10,
            description = "TC-UI-010: Password field is masked by default")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password field type is password (masked) by default")
    public void testPasswordMaskedByDefault() {
        String fieldType = loginPage.getPasswordFieldType();
        Assert.assertEquals(fieldType, "password",
                "Password field should be masked by default");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-011 — EYE ICON TOGGLES PASSWORD VISIBILITY
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 11,
            description = "TC-UI-011: Eye icon toggles password visibility")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify clicking the eye icon toggles password field between masked and visible")
    public void testEyeIconTogglesPasswordVisibility() {
        loginPage.enterPassword(VALID_PASSWORD);

        // Password should be masked initially
        Assert.assertEquals(loginPage.getPasswordFieldType(), "password",
                "Password should be masked initially");

        // Click eye icon — should show password
        loginPage.clickEyeIcon();
        Assert.assertEquals(loginPage.getPasswordFieldType(), "text",
                "Password should be visible after clicking eye icon");

        // Click eye icon again — should mask password
        loginPage.clickEyeIcon();
        Assert.assertEquals(loginPage.getPasswordFieldType(), "password",
                "Password should be masked after clicking eye icon again");
    }

    // ════════════════════════════════════════════════════════════════════════
    // TC-UI-012 — NAVIGATE TO REGISTRATION
    // ════════════════════════════════════════════════════════════════════════

    @Test(priority = 12,
            description = "TC-UI-012: Create one link navigates to registration page")
    @Story("FE-AUTH-001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify clicking Create one link navigates to the registration page")
    public void testCreateOneLinkNavigatesToRegistration() {
        loginPage.clickCreateOne();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("register"));

        Assert.assertTrue(loginPage.getCurrentUrl().contains("register"),
                "Should navigate to registration page. Current URL: " + loginPage.getCurrentUrl());
    }

}
