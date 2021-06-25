package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Settings.WebDriverSettings.*;

public class MessengerPage extends DriverGetter {
    public MessengerPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }

    public enum Stages {
        ONE, TWO, THREE, DONE, OTHER
    }

    //Локатор пользователей в сообщениях
    public String contactsUsersLoc = ".contacts__users .contacts__item";
    //Локатор имени пользователя в сообщениях
    public String contactNameLoc = "div[class='contact-card__name ']";

    //Локатор аватарки контакта слева
    public String contactImageLoc = "img";
    //Аватарка выбранного контакта
    @FindBy (css = "img[class='connection-header__img']")
    public WebElement currentContactImage;

    //Инпут для отправки сообщений
    @FindBy (css = "div[class='messenger-tools__input']")
    public WebElement inputMessage;

    //Кнопка отправить сообщение
    @FindBy (css = "button[class='messenger-tools__btn js-send-message']")
    public WebElement sendMessageBtn;

    //Имя собеседницы
    @FindBy (css = "span[class='connection-header__name']")
    public WebElement currentContactName;

    //Блок сообщений
    @FindBy (css = "div[id='messages_body']")
    public WebElement messagesBlock;

    //Сообщение
    public String messageLoc = "div[class*='js-message-block']";
    //Исходящее сообщение
    public String outgoingMessageLoc = "div[class*='message--out']";
    @FindBy (css = "div[class*='message--out']")
    public WebElement outgoingMessage;

    private List<WebElement> getContactsList(){
        System.out.println("Getting contacts list");
        List<WebElement> contactsList = driver.findElements(By.cssSelector(contactsUsersLoc));
        System.out.println("Found " + contactsList.size());
        return contactsList;
    }

    public void messagingSequence(){
        System.out.println("Starting messaging sequence");
        pageInner.open(messagesLink);
        pageInner.waitForPageLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(contactsUsersLoc)));
        contactIteration();
    }

    private void contactIteration(){
        try {
            List<WebElement> contactsList = getContactsList();
            for (WebElement e : contactsList) {
                performSkips();
                String name = e.findElement(By.cssSelector(contactNameLoc)).getText();
                String avatarLink = extractIDFromImageLink(e.findElement(By.tagName(contactImageLoc)).getAttribute("src"));
                System.out.println("Opening conversation with " + name + " ID " + avatarLink);
                pageInner.click(e);
                waitForContactLoad(e, name, avatarLink);
                if (isAwaitingResponse()) {
                    messageSendLogic();
                } else {
                    System.out.println("Skipping contact, our message is not the last one");
                }
            }
        }
        catch (StaleElementReferenceException e){
            System.out.println("Stale element exception caught, reloading contacts");
            contactIteration();
        }
    }

    private void waitForContactLoad(WebElement contact, String name, String imgID){
        try {
            System.out.println("Waiting for contact load");
            wait.until(ExpectedConditions.textToBePresentInElement(currentContactName, name));
            System.out.println("Current contact name (" + name + ") matches, waiting for img ID");
            wait.until(ExpectedConditions.attributeContains(currentContactImage, "src", imgID));
            System.out.println("Current img ID (" + imgID + ") matches");
        }
        catch (TimeoutException e){
            System.out.println("Timeout Exception intercepted, trying to click on contact again " + e.getMessage());
            pageInner.click(contact);
            wait.until(ExpectedConditions.textToBePresentInElement(currentContactName, name));
            System.out.println("Current contact name (" + name + ") matches, waiting for img ID");
            wait.until(ExpectedConditions.attributeContains(currentContactImage, "src", imgID));
            System.out.println("Current img ID (" + imgID + ") matches");
        }
    }

    private void performSkips(){
        pageInner.continueIfMultipleSessions();
        pageInner.skipAnnouncements();
    }

    private void messageSendLogic(){
        Stages currentStage = checkCurrentStage();
        if (currentStage.equals(Stages.OTHER)){
            System.out.println("Skipping contact, foreign messages found");
        }
        else if (currentStage.equals(Stages.DONE)){
            System.out.println("Skipping contact, all steps are already finished");
        }
        else {
            String message;
            int msgAmount;
            switch (currentStage){
                case ONE:
                    message = msg1;
                    msgAmount = 0;
                    break;
                case TWO:
                    message = msg2;
                    msgAmount = 1;
                    break;
                case THREE:
                    message = msg3;
                    msgAmount = 2;
                    break;
                default:
                    message = "";
                    msgAmount = 0;
            }
            System.out.println("Sending message: " + message);
            inputMessage.sendKeys(message);
            wait.until(ExpectedConditions.textToBePresentInElement(inputMessage, message));
            pageInner.click(sendMessageBtn);
            System.out.println("Message sent");
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(outgoingMessageLoc), msgAmount));
            System.out.println("Message visible");
        }

    }

    private boolean isAwaitingResponse(){
        System.out.println("Checking if contact is awaiting response");
        List<WebElement> messagelist = driver.findElements(By.cssSelector(messageLoc));
        if (messagelist.size()>0) {
            String lastMessageClass = messagelist.get(messagelist.size() - 1).getAttribute("class");
            System.out.println("Total amount of messages found " + messagelist.size() + " Last message class is " + lastMessageClass);
            if (lastMessageClass.contains("message--out")) {
                System.out.println("Latest message is ours");
                return false;
            }
            else {
                System.out.println("Latest message is theirs");
                return true;
            }
        }
        else {
            System.out.println("No messages found");
            return true;
        }
    }

    private List<String> getOutgoingMessagesList(){
        System.out.println("Getting outgoing messages");
        List<WebElement> outMessagesElementsList = driver.findElements(By.cssSelector(outgoingMessageLoc));
        System.out.println("Amount of outgoing message elements " + outMessagesElementsList.size());
        List<String> messageTexts = new ArrayList<>();
        for (WebElement e : outMessagesElementsList){
            String messageText = e.findElement(By.tagName("span")).getText();
            System.out.println(messageText);
            messageTexts.add(messageText);
        }
        System.out.println("Amount of outgoing message texts " + messageTexts.size());
        return messageTexts;
    }

    private String getLastMessageText(){
        System.out.println("Getting latest outgoing message");
        List<String> messagesList = getOutgoingMessagesList();
        return messagesList.get(messagesList.size()-1);
    }

    private String msgWithoutSmileys(String msg){
        return msg
                .replace(":)", "")
                .replace("  ", " ")
                .trim();
    }

    private boolean checkIfHasForeignMsgs(List<String> messageList){
        System.out.println("Checking for foreign messages");
        List<String> presetMessagesList = List.of(msgWithoutSmileys(msg1), msgWithoutSmileys(msg2), msgWithoutSmileys(msg3));
//        System.out.println("List of preset messages:");
//        presetMessagesList.forEach(System.out::println);
        boolean checkResult = false;
        if (messageList.size()>3){
            checkResult = true;
        }
        else if (messageList.size()>0){
            for (String msg : messageList) {
                if (!presetMessagesList.contains(msg)) {
                    System.out.println("Found foreign message: " + msg);
                    checkResult = true;
                    break;
                }
            }
        }
        System.out.println("Result of foreign messages check is " + checkResult);
        return checkResult;
    }

    private Stages checkCurrentStage(){
        List<String> messageTexts = getOutgoingMessagesList();
        if (messageTexts.size()==0){
            return Stages.ONE;
        }
        else if (checkIfHasForeignMsgs(messageTexts)){
            return Stages.OTHER;
        }
        else if (messageTexts.size()==1){
            return Stages.TWO;
        }
        else if (messageTexts.size()==2){
            return Stages.THREE;
        }
        else {
            return Stages.DONE;
        }
    }

    private String extractIDFromImageLink(String link){
        Pattern pattern = Pattern.compile("(h=.*?)&");
        Matcher matcher = pattern.matcher(link);
        String result = "";
        if (matcher.find()){
            result = matcher.group(1);
        }
        System.out.println("Extracted ID " + result);
        return result;
    }

}
