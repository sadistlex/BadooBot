package Settings;


import Helpers.PropertyManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public  class WebDriverSettings {

    public static WebDriver driver;
    public static WebDriverWait wait;

    public static WebDriver getDriver() {
        return driver;
    }

    public static WebDriverWait getWait() {
        return wait;
    }


    public static void setup(){
        WebDriverManager.chromedriver().driverVersion("90.0.4430.24").setup();
        //WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setHeadless(true);

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        //options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, getWaitTime());
        driver.manage().timeouts().implicitlyWait(getWaitTime(), TimeUnit.SECONDS);
    }

    @BeforeAll
    public static void consoleOutput(){
        try {
            PrintStream out = new PrintStream(new FileOutputStream("target/logs/out.log",true));
            PrintStream dual = new DualStream(System.out, out);
            System.setOut(dual);
            PrintStream err = new PrintStream(new FileOutputStream("target/logs/out.log",true));
            dual = new DualStream(System.err, err);
            System.setErr(dual);
        }
        catch (FileNotFoundException e){
            System.out.println("Caught FileNotFoundException");
            System.out.println(e.getMessage());
        }
    }

    public static String getMainUrl (){
        return mainUrlRelease;
    }

    public static void takeScrnShot(WebDriver driver, String filepath) {
        try {
            TakesScreenshot screenShot = (TakesScreenshot) driver;
            File srcFile = screenShot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filepath);
            FileUtils.copyFile(srcFile, destFile);
        }
        catch (IOException ignored){
        }
    }

    private static int waitTime = 20;

    public static  int getWaitTime() {
        return waitTime;
    }

    public static String mainUrlRelease = "https://badoo.com";

    public static String signinLink = "/ru/signin/";
    public static String encountersLink = "/encounters";
    public static String matchedLink = "/matched";
    public static String messagesLink = "/messenger/open";


    public static String login = PropertyManager.getProperty("login");
    public static String pass = PropertyManager.getProperty("pass");;

    public static String getCookie() {
        return PropertyManager.getProperty("cookie");
    }

    public static int minWaitTime = Integer.parseInt(PropertyManager.getProperty("minWait"));
    public static int maxWaitTime = Integer.parseInt(PropertyManager.getProperty("maxWait"));
    public static int likeAmount = Integer.parseInt(PropertyManager.getProperty("likeAmount"));
    public static int repeats = Integer.parseInt(PropertyManager.getProperty("repeats"));

    public static String msg1 = PropertyManager.getProperty("msg1");
    public static String msg2 = PropertyManager.getProperty("msg2");
    public static String msg3 = PropertyManager.getProperty("msg3");

}
