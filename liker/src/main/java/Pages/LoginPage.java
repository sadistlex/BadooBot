package Pages;

import Helpers.DriverGetter;
import Helpers.PropertyManager;
import Settings.WebDriverSettings;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static Settings.WebDriverSettings.signinLink;

public class LoginPage extends DriverGetter {
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }

    //Логин
    @FindBy (css = "input[name='email']")
    public WebElement inputEmail;
    //пароль
    @FindBy (css = "input[name='password']")
    public WebElement inputPassword;

    //Кнопка Войти
    @FindBy (css = "button[type='submit']")
    public WebElement loginBtn;

    public void login(){
        addCookie();
        pageInner.waitForPageLoad();
        System.out.println("Refreshing page");
        WebDriverSettings.getDriver().navigate().refresh();
        pageInner.waitForPageLoad();
        if (driver.getCurrentUrl().contains("signin")){
            System.out.println("Timeout caught, logging in the usual way");
            clearCookie();
            pageInner.waitForPageLoad();
            WebDriverSettings.getDriver().navigate().refresh();
            pageInner.waitForPageLoad();
            inputPassword.sendKeys(WebDriverSettings.pass);
            inputEmail.sendKeys(WebDriverSettings.login);
            System.out.println("Waiting for login to appear");
            wait.until(ExpectedConditions.textToBePresentInElementValue(inputEmail, WebDriverSettings.login));
            inputPassword.sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("signin")));
        }
        if (!driver.getCurrentUrl().contains("encounters")){
            pageInner.open("/encounters");
            wait.until(ExpectedConditions.urlContains("encounters"));
            pageInner.waitForPageLoad();
        }
        PropertyManager.setProperty("cookie", getSessionCookie());
    }

    public void addCookie(){
        System.out.println("Adding cookie");
        Cookie cookie = new Cookie("session", WebDriverSettings.getCookie());
        driver.manage().addCookie(cookie);
    }

    public void clearCookie(){
        System.out.println("Clearing session cookie");
        driver.manage().deleteCookieNamed("session");
    }

    public String getSessionCookie(){
        return driver.manage().getCookieNamed("session").getValue();
    }

    public void loginSequence(){
        pageInner.open(signinLink);
        login();
        pageInner.waitForPageLoad();
    }

}
