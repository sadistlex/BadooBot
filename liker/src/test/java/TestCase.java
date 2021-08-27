import Pages.*;
import Settings.TestControl;
import Settings.WebDriverSettings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

@ExtendWith(TestControl.class)
class TestCase extends WebDriverSettings {
    static Page page;
    static LoginPage loginPage;
    static EncountersPage encountersPage;
    static MessengerPage messengerPage;


    @Nested
    @Tag("Main")
    public class MainCases {

        @BeforeEach
        public void init() {
            WebDriverSettings.setup();
            loginPage = new LoginPage(driver);
            encountersPage = new EncountersPage(driver);
            messengerPage = new MessengerPage(driver);
            page = PageFactory.initElements(driver, Page.class);
        }

        @Test
        public void fullSequence(){
            loginPage.loginSequence();
            for (int i=1;i<=repeats;i++){
                System.out.println("Starting sequence #" + i);
                encountersPage.pressLikeSequence(likeAmount);
                messengerPage.messagingSequence();
            }
            System.out.println("All sequences are done");
        }
    }
}
