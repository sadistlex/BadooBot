package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
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

    public MessengerPage() {
        this.driver = WebDriverSettings.getDriver();
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(WebDriverSettings.getDriver(),this);
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
    //Последнее сообщение слева в контактах
    public String lastMessageContactLoc = "span[class*='contact-card__message']";

    //Инпут для отправки сообщений
    @FindBy (css = "div[class='messenger-tools__input']")
    public WebElement inputMessage;

    //Кнопка отправить сообщение
    @FindBy (css = "button[class='messenger-tools__btn js-send-message']")
    public WebElement sendMessageBtn;

    //Имя собеседницы
    @FindBy (css = "span[class='connection-header__name']")
    public WebElement currentContactName;

    //Кнопка звездочка "Избранное" у контакта
    public String favouriteContactBtnLoc = "div[class*='js-im-favorites-wrap']";

    //Блок сообщений
    @FindBy (css = "div[id='messages_body']")
    public WebElement messagesBlock;

    //Сообщение
    public String messageLoc = "div[class*='js-message-block']";
    //Исходящее сообщение
    public String outgoingMessageLoc = "div[class*='message--out']";
    @FindBy (css = "div[class*='message--out']")
    public WebElement outgoingMessage;
    //Входящие сообщения
    public String incomingMessageLoc = "div[class*='message--in']";

    public void messagingSequence(){
        System.out.println("Starting messaging sequence");
        pageInner.open(messagesLink);
        pageInner.waitForPageLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(contactsUsersLoc)));
        contactIteration();
    }

    private void contactIteration(){
        try {
            loadAllContacts(); //Изначально подгружены не все контакты, пробегаемся по ним, чтобы загрузились все
            List<WebElement> contactsList = getContactsList();
            for (WebElement e : contactsList) {
                performSkips(); //Закрываем попапы
                //Забираем имя и ссылку на аватарку сбоку, для последующей сверки с текущей выбранной.
                String name = e.findElement(By.cssSelector(contactNameLoc)).getText();
                String avatarLink = extractIDFromImageLink(e.findElement(By.tagName(contactImageLoc)).getAttribute("src"));
                System.out.println("Opening conversation with " + name + " ID " + avatarLink);
                if (!checkIfFavourite(e) || !checkLastContactMsgIsOurs(e)){ //Пропускаем контакт, если он в избранных или последнее сообщение от нас
                    pageInner.click(e);
                    waitForContactLoad(e);
                    if (findPhoneNumberInMsgs(getMessagesList(incomingMessageLoc))){
                        pageInner.click(e.findElement(By.cssSelector(favouriteContactBtnLoc)));
                    }
                    if (isAwaitingResponse()) {
                        messageSendLogic();
                    } else {
                        System.out.println("Skipping contact, our message is not the last one");
                    }
                }
                else {
                    System.out.println("Skipping contact because it's in favourites or our message is latest");
                }
            }
        }
        catch (StaleElementReferenceException e){
            System.out.println("Stale element exception caught, reloading contacts");
            contactIteration();
        }
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

    private List<WebElement> getContactsList(){
        System.out.println("Getting contacts list");
        List<WebElement> contactsList = driver.findElements(By.cssSelector(contactsUsersLoc));
        System.out.println("Found " + contactsList.size());
        return contactsList;
    }

    private void waitForContactLoad(WebElement contact){
        String name = contact.findElement(By.cssSelector(contactNameLoc)).getText();
        String imgID = extractIDFromImageLink(contact.findElement(By.tagName(contactImageLoc)).getAttribute("src"));
        try {
            System.out.println("Waiting for contact load");
            wait.until(ExpectedConditions.textToBePresentInElement(currentContactName, name));
            System.out.println("Current contact name (" + name + ") matches, waiting for img ID");
            wait.until(ExpectedConditions.attributeContains(currentContactImage, "src", imgID));
            System.out.println("Current img ID (" + imgID + ") matches");
        }
        catch (TimeoutException e){
            if (!currentContactImage.getAttribute("src").contains("placeholder")) {
                System.out.println("Timeout Exception intercepted, trying to click on contact again " + e.getMessage());
                pageInner.click(contact);
                wait.until(ExpectedConditions.textToBePresentInElement(currentContactName, name));
                System.out.println("Current contact name (" + name + ") matches, waiting for img ID");
                wait.until(ExpectedConditions.attributeContains(currentContactImage, "src", imgID));
                System.out.println("Current img ID (" + imgID + ") matches");
            }
        }
    }

    private void performSkips(){
        pageInner.continueIfMultipleSessions();
        pageInner.skipAnnouncements();
    }

    private boolean checkIfFavourite(WebElement contactElement){
        System.out.println("Checking if contact is in Favorites or deleted");
        List<WebElement> favouriteElement = contactElement.findElements(By.cssSelector(favouriteContactBtnLoc));
        //Проверяю на наличие элемента, потому что у удаленных пользователей нет кнопки Избранное
        if (favouriteElement.isEmpty()){
            return true;
        }
        else {
            return favouriteElement.get(0).getAttribute("class").contains("is-active");
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

    private List<String> getMessagesList(String locator){
        System.out.println("Getting messages by locator " + locator);
        List<WebElement> messagesElementsList = driver.findElements(By.cssSelector(locator));
        System.out.println("Amount of message elements " + messagesElementsList.size());
        List<String> messageTexts = new ArrayList<>();
        for (WebElement e : messagesElementsList){
            String messageText = e.findElement(By.tagName("span")).getText();
            System.out.println(messageText);
            messageTexts.add(messageText);
        }
        System.out.println("Amount of message texts " + messageTexts.size());
        return messageTexts;
    }

    private String msgWithoutSmileys(String msg){
        return msg
                .replace(":)", "")
                .replace("  ", " ")
                .trim();
    }

    private boolean checkIfHasForeignMsgs(List<String> messageList){
        //Проверяю на наличие посторонних сообщений, чтобы не писать в ручные переписки.
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
        List<String> messageTexts = getMessagesList(outgoingMessageLoc);
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
        //Ссылки на аватарки в списке контактов и в открытой беседе разные, но у них есть один общий атрибут, который мы достаем, чтобы понять что картинки одинаковые.
        Pattern pattern = Pattern.compile("(h=.*?)&");
        Matcher matcher = pattern.matcher(link);
        String result = "";
        if (matcher.find()){
            result = matcher.group(1);
        }
        System.out.println("Extracted ID " + result);
        return result;
    }

    private boolean findPhoneNumberInMsgs(List<String> inMessages){
        System.out.println("Searching for phone numbers in incoming messages");
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        for (String msg : inMessages){
            try {
                PhoneNumberMatch p = phoneUtil.findNumbers(msg, "RU").iterator().next();
                System.out.println("Found phone number " + p.rawString());
                return true;
            }
            catch (Exception ignored){
            }
        }
        System.out.println("No phone numbers found");
        return false;
    }

    private void loadAllContacts(){
        //Изначально подгружены не все контакты, нужно пройтись по ним, чтобы подгрузить все.
        System.out.println("Loading all contacts");
        int afterCount = 1;
        int beforeCount = 0;
        List<WebElement> contactsList;
        while (afterCount>beforeCount){
            //Кликаем на самый нижний доступный контакт, пока их количество не перестанет увеличиваться.
            contactsList = getContactsList();
            beforeCount = contactsList.size();
            WebElement lastContact = contactsList.get(contactsList.size()-1);
            pageInner.click(lastContact);
            waitForContactLoad(lastContact);
            contactsList = getContactsList();
            afterCount = contactsList.size();
        }
        System.out.println("Finished loading contacts, total amount: " + afterCount);
    }

    private boolean checkLastContactMsgIsOurs(WebElement contact){
        List<String> presetMessagesList = List.of(msg1, msg2, msg3);
        String lastMsg = contact.findElement(By.cssSelector(lastMessageContactLoc)).getText();
        return presetMessagesList.contains(lastMsg);
    }

}
