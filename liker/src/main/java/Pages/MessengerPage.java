package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class MessengerPage extends DriverGetter {
    public MessengerPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }

    //Локатор поьзователей в сообщениях
    public String contactsUsersLoc = ".contacts__users .contacts__item";

    public List<WebElement> getContactsList(){
        System.out.println("Getting contacts list");
        List<WebElement> list = driver.findElements(By.cssSelector(contactsUsersLoc));
        System.out.println("Found " + list.size());
        return list;
    }

}
