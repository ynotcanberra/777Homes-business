package WordpressPageObjects;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class UpdatePropertyPage {
	static WebDriver driver;

	public UpdatePropertyPage(WebDriver driver) {
		UpdatePropertyPage.driver = driver;
	}

	/************************************************************************************************************************
	 * WebElements for Login Page
	 * 
	 ************************************************************************************************************************/
	// WebElements for Login page
	public static String mikado_property = "//div[text()='Mikado Properties']";
	public static String add_new_property = "(//li[text()='Mikado Properties']/following::a[text()='Add New'])[1]";
	public static String addNewPropertyHeading = "//h1[contains(text(),'Add New Property')]";
	public static String title = "//input[@id='title']";
	public static String content = "//body[@data-id='content']/p";
	public static String content1 = "//body[@data-id='content']/h3";
	public static String property_id = "//input[contains(@name,'property_id')]";
	public static String property_price = "(//input[contains(@name,'property_price')])[1]";
	public static String price_label = "(//input[contains(@name,'property_price')])[2]";
	public static String property_size = "(//input[contains(@name,'property_size')])[1]";
	public static String size_label = "(//input[contains(@name,'property_size')])[2]";
	public static String property_bedroom = "//input[contains(@name,'property_bedroom')]";
	public static String property_bathroom = "//input[contains(@name,'property_bathroom')]";
	public static String property_year = "//input[contains(@name,'year_built')]";
	public static String property_heating = "//input[contains(@name,'property_heating')]";
	public static String property_parking = "//input[contains(@name,'property_accommodation')]";
	public static String property_address = "//input[contains(@placeholder,'Enter a location')]";
	public static String country = "//select[contains(@name,'property_address_country')]";
	public static String contact = "//select[contains(@name,'property_contact_info')]";
	public static String owner = "//select[contains(@name,'property_contact_owner')]";
	public static String save_draft = "//input[@value='Save Draft']";
	public static String update = "//input[@type='submit'][@value='Update']";
	public static String search_Input = "//input[@type='search']";
	public static String search_Submit = "//input[@value='Search Posts']";

	@SuppressWarnings("unchecked")
	public static void updateProperty() {
		try {
			String homePath = System.getProperty("user.dir");
			String filePath = homePath + "\\src\\main\\resources\\InputTestdata\\Listing details.xlsx";
			FileInputStream file = new FileInputStream(new File(filePath));
			ZipSecureFile.setMinInflateRatio(-1.0d);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(1);
			XSSFRow row = sheet.getRow(0);
			XSSFSheet sheet1 = workbook.getSheetAt(0);
			XSSFRow row1 = sheet1.getRow(0);
			XSSFCell cell1 = row1.getCell(1);

			FluentWait wait = new FluentWait<WebDriver>(driver).withTimeout(25, TimeUnit.SECONDS)
					.pollingEvery(3, TimeUnit.SECONDS).ignoring(Exception.class);
			JavascriptExecutor je = (JavascriptExecutor) driver;
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				try {
					row = sheet.getRow(rowIndex);
					XSSFCell cell = row.getCell(1);
					String status = cell.getStringCellValue().trim();
					cell = row.getCell(4);
					String property_Status = cell.getStringCellValue().trim();

					if (status.equalsIgnoreCase("TO BE UPDATED")) {
						// Entering Property Title by fetching from Input Excel
						String propertyTitle = "";
						if (row != null) {
							cell = row.getCell(2);
							// Fetch the Title Details
							if (cell != null) {
								// Found column and there is value in the cell.
								propertyTitle = cell.getStringCellValue().trim();
							}
						}

						String propertyID = "";
						// Fetching Property ID from LIsting sheet
						if (row != null) {
							cell = row.getCell(0);
							// Fetch the Title Details
							try {
								if (cell != null) {
									// Found column and there is value in the cell.
									propertyID = cell.getStringCellValue().trim();
								}
							} catch (Exception e) {
								double ID = cell.getNumericCellValue();
								int x = (int) ID;
								propertyID = String.valueOf(x);
							}
						}

						String propTag = "";
						String PID = "";
						row1 = sheet1.getRow(1);
						cell1 = row1.getCell(2);
						Boolean value = false;

						for (int j = 1; j <= sheet1.getLastRowNum(); j++) {
							row1 = sheet1.getRow(j);
							cell1 = row1.getCell(0);
							try {
								if (cell1 != null) {
									// Found column and there is value in the cell.
									PID = cell1.getStringCellValue().trim();
								}
							} catch (Exception e) {
								double ID = cell1.getNumericCellValue();
								int x = (int) ID;
								PID = String.valueOf(x);
							}
							if (PID.equalsIgnoreCase(propertyID)) {
								cell1 = row1.getCell(2);
								// Fetch the Title Details
								// Fetching the Details of corresponding Property from Backlog Sheet
								if (!cell1.getStringCellValue().equalsIgnoreCase("")) {
									cell1 = row1.getCell(2);
									String propertyLink = cell1.getStringCellValue().trim();
									String[] a = propertyLink.split("https://www.777homes.com.au/");
									System.out.println(a[0]);
									System.out.println(a[1]);
									char[] b = a[1].toCharArray();
									StringBuffer Tag = new StringBuffer();
									for (Character s : b) {
										if (Character.isDigit(s)) {
											Tag.append(s);
										}
									}
									propTag = Tag.toString();
									value = true;
									break;
								}
							} else {
								continue;
							}
						}

						if (value) {
							// Wait Until Page loads
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(mikado_property)));
							driver.findElement(By.xpath(mikado_property)).click();
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(search_Input)));
							driver.findElement(By.xpath(search_Input)).clear();
							driver.findElement(By.xpath(search_Input)).sendKeys(propertyTitle);
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(search_Submit)));
							driver.findElement(By.xpath(search_Submit)).click();

							// Waiting for Page to complete Load
							wait.until(new ExpectedCondition<Boolean>() {
								public Boolean apply(WebDriver wdriver) {
									return ((JavascriptExecutor) driver).executeScript("return document.readyState")
											.equals("complete");
								}
							});
							Thread.sleep(3000);

							List<WebElement> element = driver.findElements(By.xpath("//a[@class='row-title']"));
							Actions actions = new Actions(driver);
							JavascriptExecutor executor = (JavascriptExecutor) driver;

							// Mouse over on the property Name
							for (int j = 0; j < element.size(); j++) {
								element = driver.findElements(By.xpath("//a[@class='row-title']"));
								String tagLink = element.get(j).getAttribute("href").trim();
								if (tagLink.matches("(.*)" + propTag + "(.*)")) {
									// element.get(i).click();
									executor.executeScript("arguments[0].click();", element.get(j));
									break;
								}
							}

							// Selecting Features by fetching the values from Input Excel
							String features = "";
							int count = 0;
							if (row != null) {
								if (count == 0) {
									cell = row.getCell(21);
									// Fetch the Features Details
									if (cell != null) {
										// Found column and there is value in the cell.
										String feauturevalue = cell.getStringCellValue().trim();
										if (feauturevalue.equalsIgnoreCase("Yes")) {
											features = "House & Land";
											count++;
										}
									}
									if (count == 0) {
										cell = row.getCell(22);
										// Fetch the Features Details
										if (cell != null) {
											// Found column and there is value in the cell.
											String feauturevalue = cell.getStringCellValue().trim();
											if (feauturevalue.equalsIgnoreCase("Yes")) {
												features = "Gym/Pool/Spa";
												count++;
											}
										}
										if (count == 0) {
											cell = row.getCell(23);
											// Fetch the Features Details
											if (cell != null) {
												// Found column and there is value in the cell.
												String feauturevalue = cell.getStringCellValue().trim();
												if (feauturevalue.equalsIgnoreCase("Yes")) {
													features = "Outdoor Space";
													count++;
												}
											}
											if (count == 0) {
												cell = row.getCell(24);
												// Fetch the Features Details
												if (cell != null) {
													// Found column and there is value in the cell.
													String feauturevalue = cell.getStringCellValue().trim();
													if (feauturevalue.equalsIgnoreCase("Yes")) {
														features = "Secure parking";
														count++;
													}
												}
												if (count == 0) {
													cell = row.getCell(25);
													// Fetch the Features Details
													if (cell != null) {
														// Found column and there is value in the cell.
														String feauturevalue = cell.getStringCellValue().trim();
														if (feauturevalue.equalsIgnoreCase("Yes")) {
															features = "Brand new";
															count++;
														}
													}
												}
												if (count == 0) {
													cell = row.getCell(26);
													// Fetch the Features Details
													if (cell != null) {
														// Found column and there is value in the cell.
														String feauturevalue = cell.getStringCellValue().trim();
														if (feauturevalue.equalsIgnoreCase("Yes")) {
															features = "Central A/C";
															count++;
														}
													}
												}
												if (count == 0) {
													cell = row.getCell(27);
													// Fetch the Features Details
													if (cell != null) {
														// Found column and there is value in the cell.
														String feauturevalue = cell.getStringCellValue().trim();
														if (feauturevalue.equalsIgnoreCase("Yes")) {
															features = "Elevator";
															count++;
														}
													}
												}
												if (count == 0) {
													cell = row.getCell(28);
													// Fetch the Features Details
													if (cell != null) {
														// Found column and there is value in the cell.
														String feauturevalue = cell.getStringCellValue().trim();
														if (feauturevalue.equalsIgnoreCase("Yes")) {
															features = "NBN ready";
															count++;
														}
													}
												}
												if (count == 0) {
													cell = row.getCell(29);
													// Fetch the Features Details
													if (cell != null) {
														// Found column and there is value in the cell.
														String feauturevalue = cell.getStringCellValue().trim();
														if (feauturevalue.equalsIgnoreCase("Yes")) {
															features = "Off the plan";
															count++;
														}
													}
												}
											}
											if (count == 0) {
												cell = row.getCell(30);
												// Fetch the Features Details
												if (cell != null) {
													// Found column and there is value in the cell.
													String feauturevalue = cell.getStringCellValue().trim();
													if (feauturevalue.equalsIgnoreCase("Yes")) {
														features = "Pet friendly";
														count++;
													}
												}
											}
										}
									}
								}
							}
							if (!features.equalsIgnoreCase("")) {
								wait.until(ExpectedConditions.visibilityOfElementLocated(
										By.xpath("//label[contains(text(),'" + features + "')]/input")));
								driver.findElement(By.xpath("//label[contains(text(),'" + features + "')]/input"))
										.click();
							}

							// Selecting Property Status by fetching the values from Input Excel
							String propertyStatus = "";
							if (row != null) {
								cell = row.getCell(4);
								// Fetch the Property Status Details
								if (cell != null) {
									// Found column and there is value in the cell.
									propertyStatus = cell.getStringCellValue().trim();
								}
							}
							if (propertyStatus != null) {
								if (propertyStatus.equalsIgnoreCase("OFFER")) {
									propertyStatus = "Offer";
								}
								je.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(
										By.xpath("//label[contains(text(),'" + propertyStatus + "')]/input")));
								wait.until(ExpectedConditions.visibilityOfElementLocated(
										By.xpath("//label[contains(text(),'" + propertyStatus + "')]/input")));
								List<WebElement> element1 = driver.findElements(By.xpath(
										"//li[contains(@id,'property-status')]/following::input[@checked='checked']"));
								Thread.sleep(5000);
								try {
									executor.executeScript("arguments[0].click();", element1.get(0));
								} catch (Exception e) {
									actions.click(element1.get(0));
									Action action = actions.build();
									action.perform();
								}
								Thread.sleep(5000);

								try {
									executor.executeScript("arguments[0].click();", driver.findElement(
											By.xpath("//label[contains(text(),'" + propertyStatus + "')]/input")));
								} catch (Exception e) {
									actions.click(driver.findElement(
											By.xpath("//label[contains(text(),'" + propertyStatus + "')]/input")));
									Action action = actions.build();
									action.perform();
								}

							}

							// Entering Description by fetching from Input Excel
							String description = "";
							if (row != null) {
								cell = row.getCell(18);
								// Fetch the Title Details
								if (cell != null) {
									// Found column and there is value in the cell.
									description = cell.getStringCellValue().trim();
								}
							}
							if (description != null) {
								driver.switchTo().frame("content_ifr");
								if (driver.findElements(By.xpath(content)).size() > 0) {
									je.executeScript("arguments[0].scrollIntoView(true);",driver.findElement(By.xpath(content)));
									wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(content)));
									driver.findElement(By.xpath(content)).clear();
									driver.findElement(By.xpath(content)).sendKeys(description);
								} else if (driver.findElements(By.xpath(content1)).size() > 0) {
									je.executeScript("arguments[0].scrollIntoView(true);",driver.findElement(By.xpath(content1)));
									wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(content1)));
									driver.findElement(By.xpath(content1)).clear();
									driver.findElement(By.xpath(content1)).sendKeys(description);
								}
								driver.switchTo().defaultContent();
							}

							// Entering the meta description
							String[] metDes = description.split(",");
							String metaDescription = propertyTitle + " - " + metDes[0];
							je.executeScript("arguments[0].scrollIntoView(true);",
									driver.findElement(By.xpath("//div[text()='Meta description']/following::div[1]")));
							wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//div[text()='Meta description']/following::div[1]")));
							executor = (JavascriptExecutor) driver;
							executor.executeScript("arguments[0].click();",
									driver.findElement(By.xpath("//div[text()='Meta description']/following::div[1]")));
							Actions action = new Actions(driver);
							action.sendKeys(
									driver.findElement(By.xpath("//div[text()='Meta description']/following::div[1]")),
									metaDescription).build().perform();

							// Clicking on Advanced and selecting the Options
							wait.until(ExpectedConditions
									.visibilityOfElementLocated(By.xpath("//span[text()='Advanced']")));
							driver.findElement(By.xpath("//span[text()='Advanced']")).click();
							wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//select[contains(@id,'yoast-meta-robots-noindex-metabox')]")));
							Select select = new Select(driver.findElement(
									By.xpath("//select[contains(@id,'yoast-meta-robots-noindex-metabox')]")));
							select.selectByVisibleText("Yes (current default for Mikado Properties)");
							wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.xpath("(//input[contains(@name,'yoast-meta-robots-nofollow-metabox')])[1]")));
							driver.findElement(
									By.xpath("(//input[contains(@name,'yoast-meta-robots-nofollow-metabox')])[1]"))
									.click();

							// Entering Price by fetching from Input Excel
							String Price = "";
							if (row != null) {
								cell = row.getCell(10);
								// Fetch the Price Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Price = cell.getStringCellValue().trim();
										Price = Price.replace("\t", "");
										// Price = String.valueOf(price);
									}
								} catch (Exception e) {
									int price = (int) cell.getNumericCellValue();
									Price = String.valueOf(price);
								}
							}
							if (Price != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_price)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_price)));
							driver.findElement(By.xpath(property_price)).clear();
							driver.findElement(By.xpath(property_price)).sendKeys(Price);

							// Entering Price Label by fetching from Input Excel
							String PriceLabel = "";
							if (row != null) {
								cell = row.getCell(11);
								// Fetch the Price label Details
								if (cell != null) {
									// Found column and there is value in the cell.
									PriceLabel = cell.getStringCellValue().trim();

								}
							}
							if (PriceLabel != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(price_label)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(price_label)));
							driver.findElement(By.xpath(price_label)).clear();
							driver.findElement(By.xpath(price_label)).sendKeys(PriceLabel);

							// Selecting the Price label Position
							je.executeScript("arguments[0].scrollIntoView(true);",
									driver.findElement(By.xpath("//select[contains(@name,'price_label_position')]")));
							wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//select[contains(@name,'price_label_position')]")));
							select = new Select(
									driver.findElement(By.xpath("//select[contains(@name,'price_label_position')]")));
							select.selectByVisibleText("After Price");

							// Entering Size by fetching from Input Excel
							String Size = "";
							if (row != null) {
								cell = row.getCell(7);
								// Fetch the Price label Details
								if (cell != null) {
									// Found column and there is value in the cell.
									Size = cell.getStringCellValue().trim();
								}
							}
							if (Size != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_size)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_size)));
							driver.findElement(By.xpath(property_size)).clear();
							driver.findElement(By.xpath(property_size)).sendKeys(Size);

							// Entering Size Label by fetching from Input Excel
							String SizeLabel = "";
							if (row != null) {
								cell = row.getCell(8);
								// Fetch the Price label Details
								if (cell != null) {
									// Found column and there is value in the cell.
									SizeLabel = cell.getStringCellValue().trim();
								}
							}
							if (SizeLabel != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(size_label)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(size_label)));
							driver.findElement(By.xpath(size_label)).clear();
							driver.findElement(By.xpath(size_label)).sendKeys(SizeLabel);

							// Selecting the Size label Position
							je.executeScript("arguments[0].scrollIntoView(true);",
									driver.findElement(By.xpath("//select[contains(@name,'size_label_position')]")));
							wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//select[contains(@name,'size_label_position')]")));
							select = new Select(
									driver.findElement(By.xpath("//select[contains(@name,'size_label_position')]")));
							select.selectByVisibleText("After Value");

							// Entering Bedrooms by fetching from Input Excel
							String Bedrooms = "";
							if (row != null) {
								cell = row.getCell(12);
								// Fetch the Bedrooms Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Bedrooms = cell.getStringCellValue();

									}
								} catch (Exception e) {
									int bedrooms = (int) cell.getNumericCellValue();
									Bedrooms = String.valueOf(bedrooms);
								}
							}
							if (Bedrooms != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_bedroom)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_bedroom)));
							driver.findElement(By.xpath(property_bedroom)).clear();
							driver.findElement(By.xpath(property_bedroom)).sendKeys(Bedrooms);

							// Entering Bathrooms by fetching from Input Excel
							String Bathrooms = "";
							if (row != null) {
								cell = row.getCell(13);
								// Fetch the Bathrooms Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Bathrooms = cell.getStringCellValue();
									}
								} catch (Exception e) {
									int bathrooms = (int) cell.getNumericCellValue();
									Bathrooms = String.valueOf(bathrooms);
								}
							}
							if (Bathrooms != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_bathroom)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_bathroom)));
							driver.findElement(By.xpath(property_bathroom)).clear();
							driver.findElement(By.xpath(property_bathroom)).sendKeys(Bathrooms);

							// Entering Year of Built by fetching from Input Excel
							String Year = "";
							if (row != null) {
								cell = row.getCell(14);
								// Fetch the Year Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Year = cell.getStringCellValue();
									}
								} catch (Exception e) {
									int year = (int) cell.getNumericCellValue();
									Year = String.valueOf(year);
								}
							}
							if (Year != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_year)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_year)));
							driver.findElement(By.xpath(property_year)).clear();
							driver.findElement(By.xpath(property_year)).sendKeys(Year);

							// Entering Heat details by fetching it from Input Excel
							String Heat = "";
							if (row != null) {
								cell = row.getCell(15);
								// Fetch the Heat Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Heat = cell.getStringCellValue();
									}
								} catch (Exception e) {
									int heat = (int) cell.getNumericCellValue();
									Heat = String.valueOf(heat);
								}
							}
							if (Heat != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_heating)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_heating)));
							driver.findElement(By.xpath(property_heating)).clear();
							driver.findElement(By.xpath(property_heating)).sendKeys(Heat);

							// Entering Parking details by fetching it from Input Excel
							String Parking = "";
							if (row != null) {
								cell = row.getCell(16);
								// Fetch the Parking Details
								try {
									if (cell != null) {
										// Found column and there is value in the cell.
										Parking = cell.getStringCellValue();
									}
								} catch (Exception e) {
									// Found column and there is value in the cell.
									int parking = (int) cell.getNumericCellValue();
									Parking = String.valueOf(parking);
								}
							}
							if (Parking != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_parking)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_parking)));
							driver.findElement(By.xpath(property_parking)).clear();
							driver.findElement(By.xpath(property_parking)).sendKeys(Parking);

							// Entering and Selecting Address details by fetching it from Input Excel
							String Address = "";
							if (row != null) {
								cell = row.getCell(17);
								// Fetch the Address Details
								if (cell != null) {
									// Found column and there is value in the cell.
									Address = cell.getStringCellValue().trim();
								}
							}
							if (Address != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(property_address)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(property_address)));
							je.executeScript("arguments[0].value='" + Address + "';",
									driver.findElement(By.xpath(property_address)));
							Robot robot = new Robot();
							robot.keyPress(KeyEvent.VK_DOWN);
							robot.keyRelease(KeyEvent.VK_DOWN);
							robot.keyPress(KeyEvent.VK_ENTER);
							robot.keyRelease(KeyEvent.VK_ENTER);
							Thread.sleep(5000);

							// Selecting Country details as Australia
							je.executeScript("arguments[0].scrollIntoView(true);",
									driver.findElement(By.xpath(country)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(country)));
							Select prop_country = new Select(driver.findElement(By.xpath(country)));
							prop_country.selectByVisibleText("Australia");

							// Selecting the Contact details by fetching it from Input Excel
							String Contact = "";
							if (row != null) {
								cell = row.getCell(19);
								// Fetch the Address Details
								if (cell != null) {
									// Found column and there is value in the cell.
									Contact = cell.getStringCellValue().trim();
								}
							}
							if (Contact != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(contact)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(contact)));
							Select prop_contact = new Select(driver.findElement(By.xpath(contact)));
							prop_contact.selectByVisibleText("Owner Info");

							// Selecting the Owner details by fetching it from Input Excel
							String Owner = "";
							if (row != null) {
								cell = row.getCell(20);
								// Fetch the Address Details
								if (cell != null) {
									// Found column and there is value in the cell.
									Owner = cell.getStringCellValue().trim();
								}
							}
							if (Owner != null)
								je.executeScript("arguments[0].scrollIntoView(true);",
										driver.findElement(By.xpath(owner)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(owner)));
							Select prop_owner = new Select(driver.findElement(By.xpath(owner)));
							prop_owner.selectByVisibleText("newdoorproperties");

							// Click on Save Draft Button
							je.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(title)));
							wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(title)));
							Thread.sleep(5000);
							je.executeScript("arguments[0].click();", driver.findElement(By.xpath(update)));
							// Waiting for Page to complete Load
							wait.until(new ExpectedCondition<Boolean>() {
								public Boolean apply(WebDriver wdriver) {
									return ((JavascriptExecutor) driver).executeScript("return document.readyState")
											.equals("complete");
								}
							});
							Thread.sleep(3000);

							// Updating the Automation Status
							cell = row.createCell(1);
							cell.setCellValue("Active");

							for (int k = 1; k <= sheet1.getLastRowNum(); k++) {
								row1 = sheet1.getRow(k);
								cell1 = row1.getCell(0);
								String prop_ID = "";
								try {
									double ID = cell1.getNumericCellValue();
									int iD = (int) ID;
									prop_ID = String.valueOf(iD);
								} catch (Exception e) {
									prop_ID = cell1.getStringCellValue();
								}

								if (prop_ID.equalsIgnoreCase(propertyID)) {
									cell1 = row1.getCell(3);
									cell1.setCellValue("Active");
								} else {
									continue;
								}
							}
						}
						// Updating the Excel Sheet with the changes
						FileOutputStream out = new FileOutputStream(new File(filePath));
						workbook.write(out);
						out.close();

					} else {
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
					driver.navigate().refresh();
					continue;
				}

			}
			driver.quit();
		}

		catch (Exception e) {
			driver.quit();
			e.printStackTrace();
			Assert.fail();

		}
	}

}