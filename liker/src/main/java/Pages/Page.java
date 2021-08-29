package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Page extends DriverGetter {

    public Page() {
        this.driver = WebDriverSettings.getDriver();
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(WebDriverSettings.getDriver(),this);
    }

    //Кнопка продолжить при двойном логине
    @FindBy (css = "div[class='btn js-continue']")
    public WebElement continueBtn;

    //Пропустить окно "отправлять уведомление"
    @FindBy (css = "div[class='btn btn--monochrome js-chrome-pushes-deny']")
    public WebElement skipBtn;

    //Закрыть окно туториала
    @FindBy (css = "div[class*='dropdown__close js-im-skip-filters-tooltip']")
    public WebElement skipTutorialBtn;

    public void waitMs(int ms){
        try{
            Thread.sleep(ms);
        }
        catch (InterruptedException ignored){
        }
    }

    public void pointAtElement(WebElement element){
        System.out.println("Pointing at " + element.toString());
        Actions action = new Actions(driver);
        action.moveToElement(element);
        action.perform();
    }

    public void assertElementVisible(WebElement element){
        waitForElementVisible(element);
        Assertions.assertTrue(element.isDisplayed(),"Виден ли элемент");
    }

    public void assertElementInvisible(WebElement element){
        System.out.println("Asserting element is invisible");
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        String loc = getLocatorFromWebElement(element);
        boolean exists = driver.findElements(By.cssSelector(loc)).size() != 0;
        driver.manage().timeouts().implicitlyWait(WebDriverSettings.getWaitTime(), TimeUnit.SECONDS);
        if (exists) {
            wait.until(ExpectedConditions.invisibilityOf(element));
            Assertions.assertFalse(element.isDisplayed(),"Невиден ли элемент");
        }
    }

    public String getLocatorFromWebElement(WebElement element) {
        //Поддерживает только css селектор
        String elementStr = element.toString();
        int index = elementStr.lastIndexOf("elector: ");
        String result = elementStr.substring(index+9);
        result = StringUtils.chop(result);
        System.out.println("got locator from element - " + result);
        return result;
    }

    public void assertPageURL(String url) {
        System.out.println("Asserting page URL");
        try {
            wait.until(ExpectedConditions.urlContains(url));
        }
        catch(TimeoutException e){
            System.out.println("Caught TimeoutException, waiting for Jquery to be done");
            waitForJQueryToBeInactive();
            waitForPageLoad();
        }
        URL pageUrl = null;
        try {
            pageUrl = new URL(driver.getCurrentUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (pageUrl != null) {
            Assertions.assertEquals(url, pageUrl.getFile());
        }
    }

    public void open(String urlAppend){
        open(urlAppend, new Dimension(1920,1080));
    }

    public void open(Dimension dimension){
        System.out.println("Opening main page");
        driver.manage().window().setSize(dimension);
        driver.get(WebDriverSettings.getMainUrl());
    }

    public void open (String urlAppend, Dimension dimension){
        System.out.println("Opening " + WebDriverSettings.getMainUrl() + urlAppend);
        driver.manage().window().setSize(dimension);
        driver.get(WebDriverSettings.getMainUrl()+urlAppend);
    }

    public void open(){
        open(new Dimension(1920,1080));
    }

    public void navigate(String urlAppend){
        System.out.println("Navigating to " + urlAppend);
        driver.navigate().to(WebDriverSettings.getMainUrl() + urlAppend);
    }

    public void scrollToTop(){
        System.out.println("Scrolling to top of the page");
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, 0);");
    }

    public void closeTutorialPopup(){
        boolean exists = pageInner.checkIfElementExists(skipTutorialBtn);
        if (exists){
            if (skipTutorialBtn.isDisplayed()) {
                System.out.println("Found tutorial popup, closing");
                pageInner.click(skipTutorialBtn);
            }
        }
    }

    public void click(WebElement element){
        try{
            System.out.println("Clicking on element " + element);
            element.click();
        }
        catch (ElementClickInterceptedException e){
            System.out.println("ElementClickInterceptedException caught");
            closeTutorialPopup();
            System.out.println("Trying to click again");
            element.click();
        }
        catch (NoSuchElementException  e){
            System.out.println("NoSuchElement caught");
            waitMs(1000);
            element.click();
        }
        catch (ElementNotInteractableException e){
            System.out.println("Caught NotInteractable Exception, waiting for element to be clickable");
            continueIfMultipleSessions();
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        }

    }

    public void waitForJQueryToBeInactive() {
        int max = 30000;
        try {
            //Checking if jquery undefined
            System.out.println("Check if Jquery defined");
            Boolean isJqueryUsed = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return (typeof(jQuery) != 'undefined')");
            if (isJqueryUsed) {
                System.out.println("Waiting for jquery to stop being active");
                while (true) {
                    // JavaScript test to verify jQuery is active or not
                    Boolean ajaxIsComplete = (Boolean) (((JavascriptExecutor) driver)
                            .executeScript("return jQuery.active == 0"));
                    if (ajaxIsComplete)
                        break;
                        max -= 300;
                        waitMs(300);
                    if(max < 0){
                        System.out.println("Jquery waiting timeout");
                        break;
                    }
                }
                System.out.println("Jquery finished");
            }
        }
        catch (JavascriptException ignored){
            waitMs(300);
            System.out.println("Caught error, trying again");
            waitForJQueryToBeInactive();
        }
    }

    public boolean checkIfElementExists(WebElement element){
        System.out.println("Checking if element exists on the page");
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
        String locator = getLocatorFromWebElement(element);
        List<WebElement> list = driver.findElements(By.cssSelector(locator));
        System.out.println("Amount of those elements on the page - " + list.size());
        driver.manage().timeouts().implicitlyWait(WebDriverSettings.getWaitTime(), TimeUnit.MILLISECONDS);
        return list.size() != 0;
    }

    public void waitForPageLoad(){
        System.out.println("Waiting for page to finish loading");
        int max = 300000;
        while (true){
            boolean loaded = (((JavascriptExecutor) driver)
                    .executeScript("return document.readyState")).equals("complete");
            if (loaded){
                System.out.println("Page finished loading, continuing");
                break;
            }
            else {
                max -= 500;
                waitMs(500);
            }
            if(max < 0){
                System.out.println("Waiting for page load timeout, ending wait");
                break;
            }
        }
    }

    public void continueIfMultipleSessions(){
        boolean exists = pageInner.checkIfElementExists(continueBtn);
        if (exists){
            if (continueBtn.isDisplayed()) {
                System.out.println("Found multiple login popup, continuing");
                pageInner.click(continueBtn);
            }
        }
    }

    public void skipAnnouncements(){
        boolean exists = pageInner.checkIfElementExists(skipBtn);
        if (exists){
            if (skipBtn.isDisplayed()) {
                System.out.println("Found popup, closing");
                pageInner.click(skipBtn);
                pageInner.waitMs(1000);
            }
        }
    }

    public void waitForElementVisible(WebElement element){
        System.out.println("Waiting for element " + element);
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        }
        catch (TimeoutException e){
            System.out.println("TimeoutException caught, waiting for jquery and page load");
            waitForJQueryToBeInactive();
            waitForPageLoad();
        }
    }

    public void scrollToElement(WebElement element){
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
        String locator = getLocatorFromWebElement(element);
        List<WebElement> list= driver.findElements(By.cssSelector(locator));
        if (list.size() != 0) {
            Actions actions = new Actions(driver);
            System.out.println("Moving to element");
            actions.moveToElement(element);
            actions.perform();
        }
        driver.manage().timeouts().implicitlyWait(WebDriverSettings.getWaitTime(), TimeUnit.SECONDS);
    }

}
