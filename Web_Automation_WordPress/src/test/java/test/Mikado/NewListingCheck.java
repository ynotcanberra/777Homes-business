package test.Mikado;

import java.io.IOException;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import AllHomesPageObjects.AllHomesPage;

public class NewListingCheck {

	@Test
	@SuppressWarnings("static-access")
	public static void checkNewAgentList() throws IOException, InterruptedException {
		WebDriver driver = null;
		AllHomesPage allHomespage = new AllHomesPage(driver);
		HashMap<String, String> mainURLs = allHomespage.fetchMainURL();
		allHomespage.newListCheck(mainURLs);
	}
}
