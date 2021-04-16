package test.Mikado;


import java.io.IOException;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import WordpressPageObjects.AddNewPropertyPage;
import WordpressPageObjects.WordpressLoginPage;

public class AddNewProperty {

	@Test
	@SuppressWarnings("static-access")
	public static void addNewProperty() throws IOException, InterruptedException {
		WebDriver driver = null;
		Properties prop = CommonUtilities.ReadInputData.readPropertiesFile();
		String Browser_Username = prop.getProperty("browser_username").trim();
		String Browser_Password = prop.getProperty("browser_password").trim();
		String WordPress_user = prop.getProperty("wordpress_username").trim();
		String wordpress_pswd = prop.getProperty("wordpress_password").trim();
		String url = prop.getProperty("wordpress_url");
		WordpressLoginPage wordpressLoginPage = new WordpressLoginPage(driver);
		driver = wordpressLoginPage.wordPressLogin(url, Browser_Username, Browser_Password, WordPress_user,
				wordpress_pswd);
		AddNewPropertyPage addNewPropertyPage = new AddNewPropertyPage(driver);
		addNewPropertyPage.addNewProperty();
	}
}
