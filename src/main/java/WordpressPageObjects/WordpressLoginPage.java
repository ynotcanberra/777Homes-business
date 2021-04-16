package WordpressPageObjects;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;

public class WordpressLoginPage {
	static WebDriver driver;

	public WordpressLoginPage(WebDriver driver) {
		WordpressLoginPage.driver = driver;
	}
	
	
	/************************************************************************************************************************
	 * WebElements for Login Page
	 * 
	 ************************************************************************************************************************/
	// WebElements for Login page
	public static String userName = "//input[@id='user_login']";
	public static String password = "//input[@id='user_pass']";
	public static String logInButton = "//input[@id='wp-submit']";
	

	public static WebDriver wordPressLogin(String url,String browser_userName,String browser_password,String wordpress_userName,String wordpress_password)
	{ 
		try {
			String homePath = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", homePath + "\\Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			String[] login_url=url.split("://");
			url=login_url[0]+"://"+browser_userName+":"+browser_password+"@"+login_url[1];
			driver.manage().window().maximize();
			driver.get(url);
			FluentWait wait = new FluentWait<WebDriver>(driver).withTimeout(25,TimeUnit.SECONDS).pollingEvery(3,TimeUnit.SECONDS).ignoring(Exception.class);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(userName)));
            driver.findElement(By.xpath(userName)).sendKeys(wordpress_userName);
            driver.findElement(By.xpath(password)).sendKeys(wordpress_password);
            driver.findElement(By.xpath(logInButton)).click();
            Thread.sleep(5000);
			if(driver.findElements(By.xpath(userName)).size()>0)
			{
			driver.findElement(By.xpath(userName)).sendKeys(wordpress_userName);
            driver.findElement(By.xpath(password)).sendKeys(wordpress_password);
            driver.findElement(By.xpath(logInButton)).click();
			}					
	}
	 catch (Exception e) {
		    e.printStackTrace();
		    Assert.fail();
		    }
	return driver;
		
	}

}
