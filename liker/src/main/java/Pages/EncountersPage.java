package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Random;

import static Settings.WebDriverSettings.*;

public class EncountersPage extends DriverGetter {
    public EncountersPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }

    //Сообщение для попапа при совпадении
    @FindBy (css = "input[class*='text-field__input']")
    public WebElement inputMatchMsg;
    //Кнопка отправить сообщение при совпадении
    @FindBy (css = "div[class*='js-send-message']")
    public WebElement matchMsgSendBtn;

    //Кнопка Сообщения в меню
    @FindBy (css = "a[href='/messenger/open']")
    public WebElement messengerOpenBtn;

    //Кнопка закрыть сообщения
    @FindBy (css = "div[class*='messenger-ovl__close im-close']")
    public WebElement closeMessagesBtn;

    public void waitRandomTime(){
        Random rand = new Random();
        int randomNum = rand.nextInt((maxWaitTime - minWaitTime) + 1) + minWaitTime;
        System.out.println("Waiting for " + randomNum + " ms");
        pageInner.waitMs(randomNum);
    }

    public void pressLikeSequence(int amount){
        closeMessages();
        for (int i=0;i<amount;i++){
            waitRandomTime();
            pageInner.skipAnnouncements();
            matchReactWithMsg();
            pageInner.continueIfMultipleSessions();
            System.out.println("Pressing 1 for the " + (i+1) + " time, " + (amount-i) + " remaining");
            new Actions(driver).sendKeys("1").perform();
        }
    }

    private void closeMessages(){
        boolean exists = pageInner.checkIfElementExists(closeMessagesBtn);
        if (exists){
            if (closeMessagesBtn.isDisplayed()) {
                System.out.println("Found messages popup, closing");
                pageInner.click(closeMessagesBtn);
            }
        }
    }


    private void matchReactWithMsg(){
        boolean exists = pageInner.checkIfElementExists(inputMatchMsg);
        if (exists){
            if (inputMatchMsg.isDisplayed()) {
                System.out.println("Found match popup, sending message");
                inputMatchMsg.sendKeys(msg1);
                wait.until(ExpectedConditions.textToBePresentInElementValue(inputMatchMsg, msg1));
                pageInner.click(matchMsgSendBtn);
            }
        }
    }



}
