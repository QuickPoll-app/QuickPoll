package com.amalitech.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ════════════════════════════════════════════════════════════════════════
    // LOCATORS
    // ════════════════════════════════════════════════════════════════════════
    private final By emailField    = By.cssSelector("input[type='email'], input[name='email'], input[placeholder*='email' i]");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By signInButton  = By.cssSelector("button[type='submit']");
    private final By errorMessage  = By.cssSelector(".error, .alert, [class*='error'], [class*='alert'], [class*='invalid']");
    private final By eyeIcon       = By.cssSelector("[class*='eye'], [class*='toggle'], button[aria-label*='password' i]");
    private final By createOneLink = By.cssSelector("a[href*='register'], a[routerLink*='register']");
    private final By pageHeading   = By.cssSelector("h1, h2, h3, [class*='title'], [class*='heading']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ACTIONS
    // ════════════════════════════════════════════════════════════════════════

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl);
    }

    public void enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        field.clear();
        field.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        field.clear();
        field.sendKeys(password);
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public void clickEyeIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(eyeIcon)).click();
    }

    public void clickCreateOne() {
        wait.until(ExpectedConditions.elementToBeClickable(createOneLink)).click();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ASSERTIONS / GETTERS
    // ════════════════════════════════════════════════════════════════════════

    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(signInButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailFieldDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSignInButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(signInButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCreateOneLinkDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(createOneLink)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPasswordFieldType() {
        try {
            return driver.findElement(passwordField).getAttribute("type");
        } catch (Exception e) {
            return "";
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
