package test.Mikado;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import AllHomesPageObjects.AllHomesPage;

public class UpdateNewListing {

	@Test
	@SuppressWarnings("static-access")
	public static void updateNewListing() throws IOException, InterruptedException {
		WebDriver driver = null;
		AllHomesPage allHomespage = new AllHomesPage(driver);
		driver=allHomespage.updateNewListingInfo();
		driver.quit();
		allHomespage.updateCity();
	}
}
