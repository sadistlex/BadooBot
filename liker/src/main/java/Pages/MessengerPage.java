package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static Settings.WebDriverSettings.msg1;

public class MessengerPage extends DriverGetter {
    public MessengerPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }

    //Локатор пользователей в сообщениях
    public String contactsUsersLoc = ".contacts__users .contacts__item";
    //Локатор имени пользователя в сообщениях
    public String contactNameLoc = "div[class='contact-card__name ']";

    //Инпут для отправки сообщений
    @FindBy (css = "div[class='messenger-tools__input']")
    public WebElement inputMessage;

    //Кнопка отправить сообщение
    @FindBy (css = "button[class='messenger-tools__btn js-send-message']")
    public WebElement sendMessageBtn;

    //Имя собеседницы
    @FindBy (css = "span[class='connection-header__name']")
    public WebElement currentContactName;

    public List<WebElement> getContactsList(){
        System.out.println("Getting contacts list");
        List<WebElement> list = driver.findElements(By.cssSelector(contactsUsersLoc));
        System.out.println("Found " + list.size());
        return list;
    }

    public void messagingSequence(){
        System.out.println("Starting messaging sequence");
        pageInner.waitForPageLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(contactsUsersLoc)));
        List<WebElement> list = getContactsList();
        for (WebElement e : list){
            pageInner.continueMultiple();
            String name = e.findElement(By.cssSelector(contactNameLoc)).getText();
            System.out.println("Messaging " + name);
            pageInner.click(e);
            wait.until(ExpectedConditions.textToBePresentInElement(currentContactName, name));
            inputMessage.sendKeys(msg1);
            wait.until(ExpectedConditions.textToBePresentInElement(inputMessage, msg1));
            pageInner.click(sendMessageBtn);
            System.out.println("Message sent");
        }
    }

}
