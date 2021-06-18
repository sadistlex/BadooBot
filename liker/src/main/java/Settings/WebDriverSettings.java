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
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
        //System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver.exe");
        WebDriverManager.chromedriver().driverVersion("90.0.4430.24").setup();
        //WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setHeadless(true);

        if (getMainUrl().contains("nginx")){
            options.setHeadless(true);
        }

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

    private static String fileChecker(String path) throws FileNotFoundException {
        System.out.println("Checking local file " + path);
        FileReader env = new FileReader(path);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(env);
            while (reader.ready()){
                sb.append(reader.readLine());
            }
            reader.close();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        String host = sb.toString();
        System.out.println(host);
        return host;
    }


    public static String getMainUrl (){
        //return mainURLLocal;
        //return mainUrlCurrent;
        //return mainUrlNginx;
        return mainUrlRelease;
    }

    public static void takeScrnShot(WebDriver driver, String filepath) {
        try {
            TakesScreenshot screenShot = (TakesScreenshot) driver;
            File srcFile = screenShot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filepath);
            FileUtils.copyFile(srcFile, destFile);
        }
        catch (IOException e){

        }
    }


    public static void printBrowserLog(String methodName){
        if (driver != null) {
            System.out.println("printing browser log");
            List<LogEntry> entries = driver.manage().logs().get(LogType.BROWSER).getAll();
            System.out.println("Entry list size " + entries.size());
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("target/logs/" + methodName + getTestOrderID() + ".txt"));
                for (LogEntry le : entries) {
                    String message = le.getMessage();
                    System.out.println(message);
                    bw.write(message);
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printPerformanceLog(){
        System.out.println("printing performance log");
        List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        System.out.println("Entry list size " + entries.size());
        List<String> entriesMsg = new ArrayList<>();
        for (LogEntry le : entries) {
            entriesMsg.add(le.getMessage());
        }
        entriesMsg.removeIf(x -> !x.contains(":8080"));
        System.out.println("Message list size " + entriesMsg.size());
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("target/logs/perf" + getTestOrderID() + ".txt"));
            for (String le : entriesMsg) {
                bw.write(le);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int waitTime = 20;

    public static  int getWaitTime() {
        return waitTime;
    }

    static int testOrderID = 1;

    public static int getTestOrderID() {
        return testOrderID;
    }

    public static void increaseTestOrderID(){
        testOrderID++;
    }


    public static String mainUrlRelease = "https://badoo.com/ru/signin/";
/*
    public static String login = "justletmeinalready@yandex.ru";
    public static String pass = "1badoo2";

    public static String cookie = "s1:258:K566WIqEO3f9lGD2ECNNLBGYN6dLlh7ec2YQoIz7";
*/
    public static String login = PropertyManager.getProperty("login");
    public static String pass = PropertyManager.getProperty("pass");;
    public static String cookie = PropertyManager.getProperty("cookie");;
    public static int minWaitTime = Integer.parseInt(PropertyManager.getProperty("minWait"));
    public static int maxWaitTime = Integer.parseInt(PropertyManager.getProperty("maxWait"));
    public static int likeAmount = Integer.parseInt(PropertyManager.getProperty("likeAmount"));


}
