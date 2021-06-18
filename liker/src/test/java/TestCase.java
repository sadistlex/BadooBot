import Pages.EncountersPage;
import Pages.LoginPage;
import Pages.MainPage;
import Pages.Page;
import Settings.TestControl;
import Settings.WebDriverSettings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

@ExtendWith(TestControl.class)
class TestCase extends WebDriverSettings {
    static MainPage mainPage;
    static Actions actions;
    static Page page;
    static LoginPage loginPage;
    static EncountersPage encountersPage;


    @Nested
    @Tag("Main")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@Disabled
    public class MainCases {

        @BeforeEach
        public void init() {
            WebDriverSettings.setup();
            mainPage = new MainPage(driver);
            loginPage = new LoginPage(driver);
            encountersPage = new EncountersPage(driver);
            page = PageFactory.initElements(driver, Page.class);
        }

        @Test
        @Order(1)
        public void likes() {
            page.open();
            loginPage.login();
            page.waitForPageLoad();
            encountersPage.pressLike(likeAmount);
        }
    }
}
