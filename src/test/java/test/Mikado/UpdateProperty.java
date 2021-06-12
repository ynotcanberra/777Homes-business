package test.Mikado;


import java.io.IOException;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import WordpressPageObjects.UpdatePropertyPage;
import WordpressPageObjects.WordpressLoginPage;

public class UpdateProperty {

	@Test
	@SuppressWarnings("static-access")
	public static void updateProperty() throws IOException, InterruptedException {
		WebDriver driver = null;
		Properties prop = CommonUtilities.ReadInputData.readPropertiesFile();
		String Browser_Username = prop.getProperty("browser_username").trim();
		String Browser_Password = prop.getProperty("browser_password").trim();
		String WordPress_user = prop.getProperty("wordpress_username").trim();
		String wordpress_pswd = prop.getProperty("wordpress_password").trim();
		String url = prop.getProperty("wordpress_staging_url");
		WordpressLoginPage wordpressLoginPage = new WordpressLoginPage(driver);
		driver = wordpressLoginPage.wordPressLogin(url, Browser_Username, Browser_Password, WordPress_user,
				wordpress_pswd);
		UpdatePropertyPage updateProperty = new UpdatePropertyPage(driver);
		updateProperty.updateProperty();
	}
}
