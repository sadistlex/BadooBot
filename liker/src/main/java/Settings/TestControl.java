package Settings;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TestControl implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
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
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.driver.quit();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        System.out.println(currentTime);
        WebDriverSettings.takeScrnShot(WebDriverSettings.getDriver(),"target/screenshots/" + currentTime + ".png");
        if (WebDriverSettings.driver != null) {
            WebDriverSettings.driver.quit();
        }
    }


}
