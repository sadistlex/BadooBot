package Settings;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestControl implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
        String testMethod = context.getTestMethod().orElseThrow().getName();
        WebDriverSettings.printBrowserLog(testMethod);
        System.err.println("Test " + testMethod + " finished, order ID = " + WebDriverSettings.getTestOrderID());
        System.err.println("Test success, order id = " + (WebDriverSettings.getTestOrderID()));
        WebDriverSettings.increaseTestOrderID();
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.driver.quit();
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.err.println("Test " + context.getTestMethod().orElseThrow().getName() + " disabled because of " + reason);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testMethod = context.getTestMethod().orElseThrow().getName();
        WebDriverSettings.printBrowserLog(testMethod);
        System.err.println("Test " + testMethod + " finished, order ID = " + WebDriverSettings.getTestOrderID());
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.takeScrnShot(WebDriverSettings.getDriver(),"target/screenshots/" + testMethod+WebDriverSettings.getTestOrderID() + ".png");
            System.err.println("Test " + testMethod + " aborted, screenshot saved, order id = " + WebDriverSettings.getTestOrderID());
        }
        cause.printStackTrace();
        WebDriverSettings.increaseTestOrderID();
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.driver.quit();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testMethod = context.getTestMethod().orElseThrow().getName();
        WebDriverSettings.printBrowserLog(testMethod);
        System.err.println("Test " + testMethod + " finished, order ID = " + WebDriverSettings.getTestOrderID());
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.takeScrnShot(WebDriverSettings.getDriver(),"target/screenshots/" + testMethod+WebDriverSettings.getTestOrderID() + ".png");
            System.err.println("Test " + testMethod +" failed, screenshot saved, order id = "+ WebDriverSettings.getTestOrderID());
        }
        System.err.println(cause.getMessage());
        WebDriverSettings.increaseTestOrderID();
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.driver.quit();
        }
    }


}
