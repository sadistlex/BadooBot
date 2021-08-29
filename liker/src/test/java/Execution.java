import Pages.*;
import Settings.TestControl;
import Settings.WebDriverSettings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

@ExtendWith(TestControl.class)
class Execution extends WebDriverSettings {
    static Page page;
    static LoginPage loginPage;
    static EncountersPage encountersPage;
    static MessengerPage messengerPage;


    @Nested
    @Tag("Main")
    public class MainSequence {

        @BeforeEach
        public void init() {
            WebDriverSettings.setup();
            loginPage = new LoginPage();
            encountersPage = new EncountersPage();
            messengerPage = new MessengerPage();
            page = new Page();
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
