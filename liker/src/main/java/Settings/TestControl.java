package Settings;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class TestControl implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
        if (WebDriverSettings.getDriver() != null) {
            WebDriverSettings.getDriver().quit();
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.err.println("Test " + context.getTestMethod().orElseThrow().getName() + " disabled because of " + reason);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        if (WebDriverSettings.getDriver() != null) {
            WebDriverSettings.getDriver().quit();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String currentTime = WebDriverSettings.getCurrentTime();
        System.out.println(currentTime);
        System.out.println("Failed");
        System.err.println(cause.getMessage());
        WebDriverSettings.takeScrnShot(WebDriverSettings.getDriver(),"target/screenshots/" + currentTime + ".png");
        if (WebDriverSettings.getDriver() != null) {
            WebDriverSettings.getDriver().quit();
        }
    }


}
