package Pages;

import Helpers.AttractivenessFilter;
import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    //Текущая картинка кандидата в лайках
    @FindBy (css = ".photo-gallery__photo img")
    public WebElement candidateImg;
    //Текущая картинка кандидата - общий div
    @FindBy (css = "div[class*='photo-gallery__photo-bg']")
    public WebElement candidateImgDiv;
    //Счетчик текущей картинки кандидата
    @FindBy (css = "span[class*='js-gallery-photo-current']")
    public WebElement currentPhotoCounter;
    //Общее количество фоток у кандидата
    @FindBy (css = "span[class*='js-gallery-photo-total']")
    public WebElement totalPhotoCounter;

    public void waitRandomTime(){
        Random rand = new Random();
        int randomNum = rand.nextInt((maxWaitTime - minWaitTime) + 1) + minWaitTime;
        System.out.println("Waiting for " + randomNum + " ms");
        pageInner.waitMs(randomNum);
    }

    public void pressLikeSequence(int amount){
        closeMessages(); //Закрываем окно с сообщениями
        boolean attractivenessFilter = attract_filter.contains("true");
        System.out.println("Attractiveness Filter is " + attractivenessFilter);
        float attractivenessThreshold = getAttract_threshold();
        for (int i=0;i<amount;i++){
            waitRandomTime();
            System.out.println("Pressing like/dislike for the " + (i+1) + " time, " + (amount-i) + " remaining");
            pageInner.skipAnnouncements(); //Закрываем всплывающие попапы
            matchReactWithMsg(); //Обрабатываем всплывающее сообщение о взаимной симпатии
            pageInner.continueIfMultipleSessions(); //Обрабатываем попап, который появляется, если сессия открыта в нескольких браузерах.
            String link = getImageLink(); //Достаем ссылку на картинку пользователя
            if (attractivenessFilter){
                attractivenessFilterSequence(attractivenessThreshold, link); //Запускаем оценку привлекательности
            }
            else{
                pressLike(); //Ставим лайк без разбора
            }
            waitForImageToChange(link); //Ждем, пока сменится картинка пользователя.
        }
    }

    private void attractivenessFilterSequence(float threshold, String link){
        try {
            float currentRating = AttractivenessFilter.getAttractivenessRating(getImageFromContact(link));
            if (currentRating>threshold){
                System.out.println("Pressing like, because rating is higher than threshold of " + threshold);
                pressLike();
            }
            else if (currentRating==0.0){
                System.out.println("Current photo has no detectable attractiveness, checking for other photos");
                if (morePhotosAvailable()){
                    System.out.println("Found more photos, switching to another one");
                    switchToNextPhoto();
                }
                else {
                    System.out.println("Pressing dislike, no face found in all photos");
                    pressDislike();
                }
            }
            else {
                System.out.println("Pressing dislike, rating is lower than threshold of " + threshold);
                pressDislike();
            }
        }
        catch (IOException ignored){
        }
    }

    private void pressLike(){
        new Actions(driver).sendKeys("1").perform();
    }

    private void pressDislike(){
        new Actions(driver).sendKeys("2").perform();
    }

    private void switchToNextPhoto() {new Actions(driver).sendKeys(Keys.ARROW_RIGHT).perform();}

    private void waitForImageToChange(String link){
        //Элемент candidateImg соответствует главной картинке пользователя, дополнительные картинки имеют локатор candidateImgDiv
        System.out.println("Waiting until image changes");
        boolean exists = pageInner.checkIfElementExists(candidateImg);
        if (exists){
            wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(candidateImg, "src", link)));
        }
        else {
            wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(candidateImgDiv, "style", link)));
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

    private boolean morePhotosAvailable(){
        int current = Integer.parseInt(currentPhotoCounter.getText());
        int total = Integer.parseInt(totalPhotoCounter.getText());
        return total>current;
    }

    private byte[] getImageFromContact(String link) throws IOException {
        System.out.println("Getting image file from element");
        InputStream in = new URL(link).openStream();
        return in.readAllBytes();
    }

    private String getImageLink(){
        //Элемент candidateImg соответствует главной картинке пользователя, дополнительные картинки имеют локатор candidateImgDiv
        boolean exists = pageInner.checkIfElementExists(candidateImg);
        String link = "";
        if (exists){
            //Главная картинка хранит ссылку на изображение в атрибуте src
            link = candidateImg.getAttribute("src");
        }
        else {
            //Второстепенные картинки хранят ссылку на изображение в background-image
            link = candidateImgDiv.getCssValue("background-image")
                    .replace("url(\"", "")
                    .replace("\")", "");
        }
        System.out.println(link);
        return link;
    }

}
