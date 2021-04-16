package AllHomesPageObjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

public class AllHomesPage {
	static WebDriver driver;

	public AllHomesPage(WebDriver driver) {
		AllHomesPage.driver = driver;
	}

	/************************************************************************************************************************
	 * WebElements for All Homes Page
	 * 
	 ************************************************************************************************************************/
	// WebElements for All Homes page
	public static String listing_url = "//div[contains(@class,'allhomes-listing-card')]/a";
	public static String show_all = "(//button[text()='Show All'])[1]";

	/************************************************************************************************************************
	 * fetchMainURL - Fetches all of the Main Agent URL details from Backlog sheet
	 * in Input Excel
	 * 
	 ************************************************************************************************************************/
	public static HashMap<String, String> fetchMainURL() {
		HashMap<String, String> agentDetails = new HashMap<String, String>();
		try {
			// Defining the Input Excel File
			String filePath = System.getProperty("user.dir")
					+ "\\src\\main\\resources\\InputTestdata\\Listing details.xlsx";
			FileInputStream file = new FileInputStream(new File(filePath));
			ZipSecureFile.setMinInflateRatio(-1.0d);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFRow row = sheet.getRow(0);
			XSSFCell cell = row.getCell(0);
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				row = sheet.getRow(rowIndex);
				cell = row.getCell(5);
				String loginDetail = cell.getStringCellValue().trim();
				cell = row.getCell(6);
				String mainURL = cell.getStringCellValue().trim();
				if (agentDetails.containsKey(loginDetail)) {
					continue;
				} else {
					agentDetails.put(loginDetail, mainURL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Assert.fail();
		}
		return agentDetails;
	}

	/************************************************************************************************************************
	 * newListCheck - Fetches Agent URL details from passed Input All homes site and
	 * update the status in excel as NotFound, New
	 * 
	 ************************************************************************************************************************/
	public static WebDriver newListCheck(HashMap<String, String> agenturls) {
		try {
			String homePath = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", homePath + "\\Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			Set<String> keys = agenturls.keySet();

			for (String st : keys) {
				ArrayList<String> agentHomeUrl = new ArrayList<String>();
				driver.get(agenturls.get(st));
				String loginDetail = st;
				FluentWait wait = new FluentWait<WebDriver>(driver).withTimeout(25, TimeUnit.SECONDS)
						.pollingEvery(3, TimeUnit.SECONDS).ignoring(Exception.class);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(show_all)));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(show_all)));
				driver.findElement(By.xpath(show_all)).click();
				Thread.sleep(3000);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(listing_url)));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(listing_url)));
				List<WebElement> element = driver.findElements(By.xpath(listing_url));
				// Defining the Input Excel File
				String filePath = System.getProperty("user.dir")
						+ "\\src\\main\\resources\\InputTestdata\\Listing details.xlsx";
				FileInputStream file = new FileInputStream(new File(filePath));
				ZipSecureFile.setMinInflateRatio(-1.0d);
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				CellStyle style = workbook.createCellStyle();
				style.setFillBackgroundColor(IndexedColors.RED1.getIndex());
				style.setFillPattern(FillPatternType.FINE_DOTS);
				XSSFSheet sheet = workbook.getSheetAt(0);
				XSSFRow row = sheet.getRow(0);

				for (int j = 0; j < element.size(); j++) {
					JavascriptExecutor je = (JavascriptExecutor) driver;
					je.executeScript("arguments[0].scrollIntoView(true);", element.get(j));
					element = driver.findElements(By.xpath(listing_url));
					String url = element.get(j).getAttribute("href").trim();
					agentHomeUrl.add(url);
				}

				System.out.println(loginDetail + ":" + agentHomeUrl.size());

				// Iterating through excel values and comparing the Details
				for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					row = sheet.getRow(rowIndex);
					XSSFCell cell = row.getCell(5);
					if (cell.getStringCellValue().trim().equalsIgnoreCase(loginDetail)) {

						cell = row.getCell(1);
						String value = cell.getStringCellValue().trim();
						if (agentHomeUrl.contains(value)) {
							System.out.println("Value in Excel is Present in All Homes page");
						} else if (!(agentHomeUrl.contains(value))) {
							cell = row.getCell(3);
							cell.setCellValue("Not Found");
							System.out.println("Value in Excel is not Present in All Homes page");
						}
					}

					else {
						continue;
					}
				}

				int count = 0;
				
				// Iterating the values in All Homes and comparing it with Input Excel
				for (int i = 0; i < agentHomeUrl.size(); i++) {
					for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
						row = sheet.getRow(rowIndex);
						XSSFCell cell = row.getCell(5);
						if (cell.getStringCellValue().trim().equalsIgnoreCase(loginDetail)) {
							cell = row.getCell(1);
							if (cell.getStringCellValue().trim().equalsIgnoreCase(agentHomeUrl.get(i))) {
								count = count + 1;
								System.out.println("Already available" + agentHomeUrl.get(i));
							}
						}
					}
					if (count == 0) {
						row = sheet.createRow(sheet.getLastRowNum() + 1);
						XSSFCell cell = row.createCell(0);
						cell.setCellValue(sheet.getLastRowNum() + 1);
						cell = row.createCell(1);
						cell.setCellValue(agentHomeUrl.get(i));
						cell = row.createCell(3);
						cell.setCellValue("New");
						cell.setCellStyle(style);
						cell = row.createCell(5);
						cell.setCellValue(loginDetail);
						cell = row.createCell(6);
						cell.setCellValue(agenturls.get(st));
						System.out.println("Newly created" + agentHomeUrl.get(i));
						
					}
					count = 0;
				}
								
				// Updating the Excel Sheet with the changes
				FileOutputStream out = new FileOutputStream(new File(filePath));
				workbook.write(out);
				out.close();
			}
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return driver;
	}

}
