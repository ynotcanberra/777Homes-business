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
	public static String prop_Title = "//h1[contains(@class,'css-hed0vw e9vzjw54')]/div";
	public static String prop_Type = "//div[@data-testid='feature-icons']/span[1]";
	public static String prop_City = "(//span[@itemprop='name'])[3]";
	public static String prop_Area = "(//span[@itemprop='name'])[4]";
	public static String number_Bedrooms = "//div[@data-testid='feature-icons']/span[2]";
	public static String number_Bathrooms = "//div[@data-testid='feature-icons']/span[3]";
	public static String number_Parking = "//div[@data-testid='feature-icons']/span[4]";
	public static String energy_Rating = "//div[@data-testid='feature-icons']/span[5]";
	public static String full_Address = "//div[@data-testid='summary']//h1";
	public static String description = "//div[@class='ReactCollapse--content']/section/div";
	public static String price = "//div[@data-testid='price']";

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
				cell = row.getCell(6);
				String loginDetail = cell.getStringCellValue().trim();
				cell = row.getCell(7);
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
					XSSFCell cell = row.getCell(6);
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
						XSSFCell cell = row.getCell(6);
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
						cell.setCellValue(sheet.getLastRowNum());
						cell = row.createCell(1);
						cell.setCellValue(agentHomeUrl.get(i));
						cell = row.createCell(3);
						cell.setCellValue("New");
						cell.setCellStyle(style);
						cell = row.createCell(6);
						cell.setCellValue(loginDetail);
						cell = row.createCell(7);
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

	/************************************************************************************************************************
	 * checkActiveListingInfo - Checks whether Active Listing has updated Info in
	 * Listing sheet and updates the details if not
	 * 
	 ************************************************************************************************************************/
	public static WebDriver checkActiveListingInfo() {
		try {
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
			XSSFCell cell = row.getCell(3);

			// Fetch the details of Listing Sheet
			XSSFSheet sheet1 = workbook.getSheetAt(1);
			XSSFRow row1 = sheet1.getRow(0);
			XSSFCell cell1 = row1.getCell(3);
			String homePath = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", homePath + "\\Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			FluentWait wait = new FluentWait<WebDriver>(driver).withTimeout(25, TimeUnit.SECONDS)
					.pollingEvery(3, TimeUnit.SECONDS).ignoring(Exception.class);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				cell = row.getCell(3);
				String status = cell.getStringCellValue();
				if (status.equalsIgnoreCase("Active")) {
					cell = row.getCell(0);
					double property_ID = cell.getNumericCellValue();
					String propertyID = String.valueOf(property_ID);
					cell = row.getCell(1);
					String url = cell.getStringCellValue().trim();
					driver.get(url);
					if (driver.findElements(By.xpath("//div[@data-testid='badges']/span")).size() > 0) {
						String propertyStatus = driver.findElement(By.xpath("//div[@data-testid='badges']/span"))
								.getText().trim();
						String propertyType = driver
								.findElement(By.xpath("//div[@data-testid='feature-icons']/span[1]")).getText().trim();
						for (int j = 1; j <= sheet1.getLastRowNum(); j++) {
							row1 = sheet1.getRow(j);
							cell1 = row1.getCell(0);
							double expproperty_ID = cell1.getNumericCellValue();
							String expectedPropertyID = String.valueOf(expproperty_ID);
							if (propertyID.equalsIgnoreCase(expectedPropertyID)) {
								cell1 = row1.getCell(3);
								String expectedPropertyType = cell1.getStringCellValue().trim();
								cell1 = row1.getCell(4);
								String expectedPropertyStatus = cell1.getStringCellValue().trim();
								if (!propertyType.equalsIgnoreCase(expectedPropertyType)) {
									cell1 = row1.getCell(3);
									cell1.setCellValue(propertyType);
									cell1.setCellStyle(style);
									cell1 = row1.createCell(1);
									cell1.setCellValue("TO BE UPDATED");
									cell1.setCellStyle(style);
								}
								if (!propertyStatus.equalsIgnoreCase(expectedPropertyStatus)) {
									cell1 = row1.getCell(4);
									cell1.setCellValue(propertyStatus);
									cell1.setCellStyle(style);
									cell1 = row1.createCell(1);
									cell1.setCellValue("TO BE UPDATED");
									cell1.setCellStyle(style);
								}
							}
						}
					}

					else {
						String propertyStatus = "Sale";
						String propertyType = driver
								.findElement(By.xpath("//div[@data-testid='feature-icons']/span[1]")).getText().trim();
						for (int j = 1; j <= sheet1.getLastRowNum(); j++) {
							row1 = sheet1.getRow(j);
							cell1 = row1.getCell(0);
							double expPropertyID = cell1.getNumericCellValue();
							String expectedPropertyID = String.valueOf(expPropertyID);
							if (propertyID.equalsIgnoreCase(expectedPropertyID)) {
								cell1 = row1.getCell(3);
								String expectedPropertyType = cell1.getStringCellValue().trim();
								cell1 = row1.getCell(4);
								String expectedPropertyStatus = cell1.getStringCellValue().trim();
								if (!propertyType.equalsIgnoreCase(expectedPropertyType)) {
									cell1 = row1.getCell(3);
									cell1.setCellValue(propertyType);
									cell1.setCellStyle(style);
									cell1 = row1.createCell(1);
									cell1.setCellValue("TO BE UPDATED");
									cell1.setCellStyle(style);
								}

								if (!propertyStatus.equalsIgnoreCase(expectedPropertyStatus)) {
									cell1 = row1.getCell(4);
									cell1.setCellValue(propertyStatus);
									cell1.setCellStyle(style);
									cell1 = row1.createCell(1);
									cell1.setCellValue("TO BE UPDATED");
									cell1.setCellStyle(style);
								}
							}
						}
					}
				}

				else {
					continue;
				}

				// Updating the Excel Sheet with the changes
				FileOutputStream out = new FileOutputStream(new File(filePath));
				workbook.write(out);
				out.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return driver;

	}

	/************************************************************************************************************************
	 * updateNewListingInfo - Updates New Listing Info in Backlog Sheet by fetching
	 * the details from All Homes WebPage
	 * 
	 ************************************************************************************************************************/
	public static WebDriver updateNewListingInfo() {
		try {
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
			XSSFCell cell = row.getCell(3);

			// Fetch the details of Listing Sheet
			XSSFSheet sheet1 = workbook.getSheetAt(1);
			XSSFRow row1 = sheet1.getRow(0);
			XSSFCell cell1 = row1.getCell(3);

			// Fetch the details of Listing Sheet
			String homePath = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", homePath + "\\Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			FluentWait wait = new FluentWait<WebDriver>(driver).withTimeout(25, TimeUnit.SECONDS)
					.pollingEvery(3, TimeUnit.SECONDS).ignoring(Exception.class);

			// Fetch the details of Listing Sheet
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				cell = row.getCell(3);
				String status = cell.getStringCellValue().trim();
				cell = row.getCell(1);
				String URL = cell.getStringCellValue().trim();
				cell = row.getCell(0);
				double iD = cell.getNumericCellValue();
				int x = (int) iD;
				String ID = String.valueOf(x);
				cell = row.getCell(5);
				String contactInfo ="";
				if(cell != null)
				{
				contactInfo = cell.getStringCellValue().trim();
				}
				else
				{
				contactInfo ="";
				}
				cell = row.getCell(6);
				String loginDetail = cell.getStringCellValue().trim();

				int count = 0;

				if (status.equalsIgnoreCase("New")) {
					count = count + 1;
					String block_Building_Size_Label = "m² approx";
					driver.get(URL);
					// Fetching the Property Title
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop_Title)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(prop_Title)));
					String propTitle = driver.findElement(By.xpath(prop_Title)).getText().trim();

					// Fetching the Property Type
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop_Type)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(prop_Type)));
					String propType = driver.findElement(By.xpath(prop_Type)).getText().trim();
					
					if(propType.contains("House"))
					{
						propType = "House";
					}
					else if(propType.contains("Apartment"))
					{
						propType = "Apartment";
					}
					else if(propType.contains("Townhouse"))
					{
						propType = "Town house";
					}
					else if(propType.contains("Land")||propType.contains("Other"))
					{
						propType = "Others";
					}
					
					// Fetching the Property Status
					String propStatus = "";
					if (driver.findElements(By.xpath("//div[@data-testid='badges']/span")).size() > 0) {
						wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath("//div[@data-testid='badges']/span")));
						wait.until(
								ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-testid='badges']/span")));
						propStatus = driver.findElement(By.xpath("//div[@data-testid='badges']/span")).getText().trim();
					} else {
						propStatus = "Sale";
					}

					// Fetching the Property City
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop_City)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(prop_City)));
					String propCity = driver.findElement(By.xpath(prop_City)).getText().trim();

					// Fetching the Property Area
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop_Area)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(prop_Area)));
					String propArea = driver.findElement(By.xpath(prop_Area)).getText().trim();

					// Fetching the Number of Bedrooms
					String noofBedrooms = "";
					if (driver.findElements(By.xpath(number_Bedrooms)).size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(number_Bedrooms)));
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(number_Bedrooms)));
						noofBedrooms = driver.findElement(By.xpath(number_Bedrooms)).getText().trim();
						if (noofBedrooms.contains("bedrooms")) {
							char[] a = noofBedrooms.toCharArray();
							noofBedrooms = a[0] + "";
						} else {
							noofBedrooms = "";
						}
					} else {
						noofBedrooms = "";
					}

					// Fetching the Number of Bathrooms
					String noofBathrooms = "";
					if (driver.findElements(By.xpath(number_Bathrooms)).size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(number_Bathrooms)));
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(number_Bathrooms)));
						noofBathrooms = driver.findElement(By.xpath(number_Bathrooms)).getText().trim();
						if (noofBathrooms.contains("bathrooms")) {
							char[] a = noofBathrooms.toCharArray();
							noofBathrooms = a[0] + "";
						} else {
							noofBathrooms = "";
						}
					} else {
						noofBathrooms = "";
					}

					// Fetching the Number of Parking
					String noofParking = "";
					if (driver.findElements(By.xpath(number_Parking)).size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(number_Parking)));
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(number_Parking)));
						noofParking = driver.findElement(By.xpath(number_Parking)).getText().trim();
						if (noofParking.contains("garage spaces")) {
							char[] a = noofParking.toCharArray();
							noofParking = a[0] + "";
						} else {
							noofParking = "";
						}
					} else {
						noofParking = "";
					}

					// Fetching the Energy rating
					String energyRating = "";
					if (driver.findElements(By.xpath(energy_Rating)).size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(energy_Rating)));
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(energy_Rating)));
						energyRating = driver.findElement(By.xpath(energy_Rating)).getText().trim();
						if (energyRating.contains("EER")) {
							char[] a = energyRating.toCharArray();
							energyRating = a[0] + "";
						} else {
							energyRating = "";
						}
					} else {
						energyRating = "";
					}

					// Fetching the Full Address
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(full_Address)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(full_Address)));
					String fullAddress = driver.findElement(By.xpath(full_Address)).getText().trim();
					if (fullAddress.contains("no street name provided")) {
						String[] ab = fullAddress.split(",");
						fullAddress = ab[1];
					}

					// Fetching the Description
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(description)));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(description)));
					String descrption = driver.findElement(By.xpath(description)).getText().trim();

					// fetching the Size Details
					String size = "";
					if (driver.findElements(By.xpath("//span[contains(text(),'size')]/following::span[1]"))
							.size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(
								By.xpath("//span[contains(text(),'size')]/following::span[1]")));
						wait.until(ExpectedConditions
								.elementToBeClickable(By.xpath("//span[contains(text(),'size')]/following::span[1]")));
						List<WebElement> element = driver
								.findElements(By.xpath("//span[contains(text(),'size')]/following::span[1]"));
						size = "Block/House: ";
						for (int j = 0; j < element.size(); j++) {

							if (element.size() == 1) {
								String text = element.get(j).getText().trim();
								String[] value = text.split(" ");
								size = size + value[0] + "/ -";
							} else {
								String text1 = element.get(0).getText().trim();
								String text2 = element.get(1).getText().trim();
								String[] value1 = text1.split(" ");
								size = size + value1[0] + "/ ";
								String[] value2 = text2.split(" ");
								size = size + value2[0];
								break;
							}
						}
					} else {
						size = "";
					}

					// fetching the Price Details
					String Price = "";
					String PriceLabel = "";
					if (driver.findElements(By.xpath(price)).size() > 0) {
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(price)));
						wait.until(ExpectedConditions.elementToBeClickable(By.xpath(price)));
						String priceTag = driver.findElement(By.xpath(price)).getText();
						if (priceTag.contains("$")) {
							if (priceTag.contains("-")) {
								String[] a = priceTag.split("-");
								Price = a[0];
								char[] pr = Price.toCharArray();
								ArrayList<Character>ab=new ArrayList<Character>();
								for (int k = 0; k < pr.length; k++) {
									if (pr[k] == '0' | pr[k] == '1' | pr[k] == '2' | pr[k] == '3' | pr[k] == '4'
											| pr[k] == '5' | pr[k] == '6' | pr[k] == '7' | pr[k] == '8'
											| pr[k] == '9') {
										ab.add(pr[k]);
									}

								}								
								StringBuilder sb = new StringBuilder();
								for (Character s : ab)
								{
								    sb.append(s);
								    sb.append("\t");
								}
								Price = sb.toString();							
								PriceLabel = priceTag;
							}

							else {
								PriceLabel = priceTag;
								Price = priceTag;
								char[] pr = Price.toCharArray();
								ArrayList<Character>ab=new ArrayList<Character>();								
								for (int k = 0; k < pr.length; k++) {
									if (pr[k] == '0' | pr[k] == '1' | pr[k] == '2' | pr[k] == '3' | pr[k] == '4'
											| pr[k] == '5' | pr[k] == '6' | pr[k] == '7' | pr[k] == '8'
											| pr[k] == '9') {
										ab.add(pr[k]);
									}

								}
								StringBuilder sb = new StringBuilder();
								for (Character s : ab)
								{
								    sb.append(s);
								    sb.append("\t");
								}
								Price = sb.toString();	
							}
						} else {
							PriceLabel = priceTag;
						}
					}

					// Updating the details in Backlog Sheet
					int lastrow = sheet1.getLastRowNum();
					int rowIndex = lastrow + count;
					row1 = sheet1.createRow(rowIndex);
					cell1 = row1.createCell(0);
					cell1.setCellValue(ID);
					cell1 = row1.createCell(1);
					cell1.setCellValue("New");
					cell1 = row1.createCell(2);
					cell1.setCellValue(propTitle);
					cell1 = row1.createCell(3);
					cell1.setCellValue(propType);
					cell1 = row1.createCell(4);
					cell1.setCellValue(propStatus);
					cell1 = row1.createCell(5);
					cell1.setCellValue(propCity);
					cell1 = row1.createCell(6);
					cell1.setCellValue(propArea);
					cell1 = row1.createCell(7);
					cell1.setCellValue(size);
					cell1 = row1.createCell(8);
					cell1.setCellValue(block_Building_Size_Label);
					cell1 = row1.createCell(10);
					cell1.setCellValue(Price);
					cell1 = row1.createCell(11);
					cell1.setCellValue(PriceLabel);
					cell1 = row1.createCell(12);
					cell1.setCellValue(noofBedrooms);
					cell1 = row1.createCell(13);
					cell1.setCellValue(noofBathrooms);
					cell1 = row1.createCell(15);
					cell1.setCellValue(energyRating);
					cell1 = row1.createCell(16);
					cell1.setCellValue(noofParking);
					cell1 = row1.createCell(17);
					cell1.setCellValue(fullAddress);
					cell1 = row1.createCell(18);
					cell1.setCellValue(descrption);
					cell1 = row1.createCell(19);
					cell1.setCellValue(contactInfo);
					cell1 = row1.createCell(20);
					cell1.setCellValue(loginDetail);

					// Updating the Excel Sheet with the changes
					FileOutputStream out = new FileOutputStream(new File(filePath));
					workbook.write(out);
					out.close();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return driver;
	}
}