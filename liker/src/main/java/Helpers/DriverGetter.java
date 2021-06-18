package Helpers;

import Pages.Page;
import Settings.WebDriverSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverGetter {
    public static WebDriver driver = WebDriverSettings.getDriver();
    public static WebDriverWait wait = WebDriverSettings.getWait();
    public static Page pageInner = new Page(driver);
}
