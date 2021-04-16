package CommonUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ReadInputData {
	WebDriver driver;

	public static Properties readPropertiesFile() {
		Properties prop = new Properties();
		try {
			String homePath = System.getProperty("user.dir");
			File file = new File(homePath + "\\src\\main\\resources\\InputTestdata\\logincredentials.properties");
			FileInputStream fileInput = new FileInputStream(file);
			prop.load(fileInput);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}

	public static void readExcelFile() {
		try {
			String homePath = System.getProperty("user.dir");
			File file = new File(
					homePath + "\\src\\main\\resources\\InputTestdata\\Automation-Listing-TobeExecuted.xlsx");
			FileInputStream fileInput = new FileInputStream(file);
			ZipSecureFile.setMinInflateRatio(-1.0d);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
