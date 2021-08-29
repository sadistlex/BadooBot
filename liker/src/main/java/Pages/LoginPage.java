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
    public LoginPage() {
        this.driver = WebDriverSettings.getDriver();
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(WebDriverSettings.getDriver(),this);
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

    public void loginSequence(){
        pageInner.open(signinLink);
        login();
        pageInner.waitForPageLoad();
    }

    private void login(){
        addCookie();
        pageInner.waitForPageLoad();
        System.out.println("Refreshing page");
        WebDriverSettings.getDriver().navigate().refresh(); //Обновляем страницу, чтобы попробовать зайти через куку
        pageInner.waitForPageLoad();
        if (driver.getCurrentUrl().contains("signin")){ //Если мы все еще на странице логина, значит кука не подошла, заходим по логин/пароль
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
            PropertyManager.setCookieProperty(getSessionCookie()); //Сохраняем новую рабочую куку в проперти
        }
        if (!driver.getCurrentUrl().contains("encounters")){ //Иногда бывает, что после логина нас направляет на промо страницу вместо encounters
            pageInner.open("/encounters");
            wait.until(ExpectedConditions.urlContains("encounters"));
            pageInner.waitForPageLoad();
        }
    }

    private void addCookie(){
        System.out.println("Adding cookie");
        Cookie cookie = new Cookie("session", WebDriverSettings.getCookie());
        driver.manage().addCookie(cookie);
    }

    private void clearCookie(){
        System.out.println("Clearing session cookie");
        driver.manage().deleteCookieNamed("session");
    }

    private String getSessionCookie(){
        return driver.manage().getCookieNamed("session").getValue();
    }

}
