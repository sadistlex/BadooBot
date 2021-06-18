package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.Random;

import static Settings.WebDriverSettings.maxWaitTime;
import static Settings.WebDriverSettings.minWaitTime;

public class EncountersPage extends DriverGetter {
    public EncountersPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }



    @FindBy (css = "div[class='btn btn--monochrome js-chrome-pushes-deny']")
    public WebElement skipBtn;




    public void waitRandomTime(){
        Random rand = new Random();
        int randomNum = rand.nextInt((maxWaitTime - minWaitTime) + 1) + minWaitTime;
        System.out.println("Waiting for " + randomNum + " ms");
        pageInner.waitMs(randomNum);
    }

    public void pressLike(int amount){
        for (int i=0;i<amount;i++){
            waitRandomTime();
            skipAnnouncements();
            System.out.println("Pressing 1 for the " + (i+1) + "'th time, " + (amount-i) + " remaining");
            new Actions(driver).sendKeys("1").perform();
        }
    }

    private void skipAnnouncements(){
        boolean exists = pageInner.checkIfElementExists(skipBtn);
        if (exists){
            if (skipBtn.isDisplayed()) {
                System.out.println("Found popup, closing");
                pageInner.click(skipBtn);
                pageInner.waitMs(1000);
            }
        }
    }

}
