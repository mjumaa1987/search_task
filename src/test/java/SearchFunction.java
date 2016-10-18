import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by ismail on 10/18/16.
 */
public class SearchFunction {

    //Define WebDriver object
    WebDriver driver = null;
    //Define Base URL for http://link.springer.com/
    String baseURL = "http://link.springer.com/";
    //Define Search Bar Xpath
    By searchBar = By.id("query");
    //Define Search button xpath
    By searchButton = By.id("search");

    //This method executed before each test case starts WebDriver and assign to Browser and configure
    @BeforeMethod
    public void startDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.navigate().to(baseURL);
    }

    //This method executed after each test case stop WebDriver
    @AfterMethod
    public void stopDriver() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    @Test
    public void search_Happy_Scenario() {
        //Define search term
        String searchTerm = "Test Term";
        //Go to search bar and type in search term
        driver.findElement(searchBar).sendKeys(searchTerm);
        //Click search button
        driver.findElement(searchButton).click();
        //Get Text term from result search page
        String displayedTerm = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[1]/h1/strong[2]")).getText();
        //Remove single quotes from first and last of the term
        displayedTerm = displayedTerm.substring(1, displayedTerm.length() - 1);
        //Check if displayed term as what provided
        Assert.assertTrue(searchTerm.equals(displayedTerm), "The dispalyed term isn't equal to your searched term");
    }

    @Test
    public void search_Long_Term() {
        //Define search term
        String searchTerm = "Test Term";
        //Go to search bar and type in search term
        driver.findElement(searchBar).sendKeys(searchTerm);
        //Click search button
        driver.findElement(searchButton).click();
        //Get Text term from result search page
        String displayedTerm = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[1]/h1/strong[2]")).getText();
        //Remove single quotes from first and last of the term
        displayedTerm = displayedTerm.substring(1, displayedTerm.length() - 1);
        System.out.print(displayedTerm);
        Assert.assertTrue(searchTerm.equals(displayedTerm), "The dispalyed term isn't equal to your searched term");
    }

    @Test
    public void filter_Search_Results() {
        //Search for term
        search_Happy_Scenario();
        //Open filtration options
        driver.findElement(By.xpath("//*[@id=\"date-facet\"]/button")).click();
        //Select option two "Show Documents Published in"
        Select select = new Select(driver.findElement(By.id("date-facet-mode")));
        select.selectByVisibleText("in");
        //Type in specific year
        String year = "2014";
        //Clear year text field
        WebElement element = driver.findElement(By.name("facet-start-year")); // Define element and locate it
        element.clear(); // Clear the text in the field
        element.sendKeys(year);//Send specific year to the field
        //Click search button for filtration
        driver.findElement(By.id("date-facet-submit")).click();
        //Check if within same provided year
        String within = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[1]/p/a")).getText().replaceAll("[\\D]", "");
        Assert.assertTrue(within.equals(year), "The system didn't retrieve same year you searched for, please check : " + within);
    }

    @Test
    public void sort_Search_Results(){
        //Search for term
        search_Happy_Scenario();
        //Select newest First
        Select select = new Select(driver.findElement(By.id("sort-results")));
        select.selectByVisibleText("Newest First");
        //Check if results related to newest year
        for(int count = 2; count <= 20; count++){
            String previousYear = driver.findElement(By.xpath("//*[@id=\"results-list\"]/li[" + (count - 1) + "]/p[4]/span[2]/span")).getText().replaceAll("[\\D]", "");
            String currentYear = driver.findElement(By.xpath("//*[@id=\"results-list\"]/li[" + count + "]/p[4]/span[2]/span")).getText().replaceAll("[\\D]", "");
            Assert.assertTrue(Integer.parseInt(previousYear) >= Integer.parseInt(currentYear), "Check there is a card has old year than previous one, please check card number: " + count);

            //Scroll page down
            WebElement nodeCard = driver.findElement(By.xpath("//*[@id=\"results-list\"]/li[" + (count) + "]/p[4]/span[2]/span"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", nodeCard);
        }

    }

    @Test
    public void search_Results_Pagination(){
        search_Happy_Scenario();
        //get current page number
        String currentPageNumber = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[2]/form/span[2]/span[1]/input")).getAttribute("value");
        //Click next page
        driver.findElement(By.className("next")).click();
        //Check if page 2 appear
        String pageNumber = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[2]/form/span/span[1]/input")).getAttribute("value");
        Assert.assertTrue(Integer.parseInt(pageNumber) == (Integer.parseInt(currentPageNumber) + 1), "something went wrong, you're not moved to second page, check please the value is: " + pageNumber);
        //Move another page
        driver.findElement(By.className("next")).click();
        pageNumber = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[2]/form/span/span[1]/input")).getAttribute("value");
        Assert.assertTrue(Integer.parseInt(pageNumber) == (Integer.parseInt(currentPageNumber) + 2), "something went wrong, you're not moved to second page, check please the value is: " + pageNumber);

        //Back to previous page
        driver.findElement(By.className("prev")).click();
        pageNumber = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[2]/form/span/span[1]/input")).getAttribute("value");
        Assert.assertTrue(Integer.parseInt(pageNumber) == (Integer.parseInt(currentPageNumber) + 1), "something went wrong, you're not moved to previous page, check please the value is: " + pageNumber);
    }

    @Test
    public void search_For_NonExist_Value(){
        //Define search term
        String searchTerm = ";;;;;";
        //Go to search bar and type in search term
        driver.findElement(searchBar).sendKeys(searchTerm);
        //Click search button
        driver.findElement(searchButton).click();
        //Check the number of results
        String resultsNumber = driver.findElement(By.xpath("//*[@id=\"kb-nav--main\"]/div[1]/h1/strong[1]")).getText();
        Assert.assertTrue(resultsNumber.equals("0"), "The term displays results, please check the number of results: " + resultsNumber);
        //Check system displays error message
        String displayedMsg = driver.findElement(By.xpath("//*[@id=\"no-results-message\"]/h2")).getText();
        //Check if displayed term as what provided
        Assert.assertTrue(displayedMsg.contains("Sorry"), "Error message for results not found isn't displayed, please check the message was: " + displayedMsg);
    }
}
