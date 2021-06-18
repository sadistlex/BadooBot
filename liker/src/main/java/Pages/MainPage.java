package Pages;

import Helpers.DriverGetter;
import Settings.WebDriverSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


public  class MainPage extends DriverGetter {

    public MainPage(WebDriver driver) {
        this.driver = driver;
        wait = WebDriverSettings.getWait();
        PageFactory.initElements(driver,this);
    }




}
