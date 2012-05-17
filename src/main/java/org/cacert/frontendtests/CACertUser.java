package org.cacert.frontendtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.TestFailure;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CACertUser {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private int year;
	private int month;
	private int day;
	
	public CACertUser() {
		firstName = null;
		lastName = null;
		email = null;
		password = null;
		year = 0;
		month = 0;
		day = 0;
	}
	
	public CACertUser(
				String firstName,
				String lastName,
				String email,
				String password,
				int year,
				int month,
				int day) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public void register(WebDriver driver) throws TestFailureException {
        /*
         * 1. register user at TEST_SYSTEM_URI
         */
        driver.get(TestConfig.TEST_SYSTEM_URI + "index.php?id=1");
        driver.findElement(By.name("fname"))
              .sendKeys(firstName);
        driver.findElement(By.name("lname"))
              .sendKeys(lastName);

        List<WebElement> days = driver.findElement(By.name("day"))
                                      .findElements(By.tagName("option"));
        days.get(day - 1).click();
        List<WebElement> months = driver.findElement(By.name("month"))
                                        .findElements(By.tagName("option"));
        months.get(month - 1).click();
        WebElement yearInput = driver.findElement(By.name("year"));
        if (! yearInput.getAttribute("value").equals("19XX"))
        	throw new TestFailureException("Year field not correctly prefiled!");
        yearInput.clear();
        yearInput.sendKeys(String.valueOf(year));

        driver.findElement(By.name("email"))
              .sendKeys(email);

        WebElement passwordInput;
        passwordInput = driver.findElement(By.name("pword1"));
        passwordInput.sendKeys(password);
        passwordInput = driver.findElement(By.name("pword2"));
        passwordInput.sendKeys(password);

        driver.findElement(By.name("Q1"))
              .sendKeys("qestion_1");
        driver.findElement(By.name("A1"))
              .sendKeys("answer_1");
        driver.findElement(By.name("Q2"))
              .sendKeys("qestion_2");
        driver.findElement(By.name("A2"))
              .sendKeys("answer_2");
        driver.findElement(By.name("Q3"))
              .sendKeys("qestion_3");
        driver.findElement(By.name("A3"))
              .sendKeys("answer_3");
        driver.findElement(By.name("Q4"))
              .sendKeys("qestion_4");
        driver.findElement(By.name("A4"))
              .sendKeys("answer_4");
        driver.findElement(By.name("Q5"))
              .sendKeys("qestion_5");
        driver.findElement(By.name("A5"))
              .sendKeys("answer_5");

        // Verify that notfy checkboxes are checked by default
        WebElement notifyCheckbox;
        notifyCheckbox = driver.findElement(By.name("general"));
        if (! notifyCheckbox.getAttribute("checked").equals("true"))
        	throw new TestFailureException("general notifications box not checked");
        notifyCheckbox = driver.findElement(By.name("country"));
        if (! notifyCheckbox.getAttribute("checked").equals("true"))
        	throw new TestFailureException("country notifications box not checked");
        notifyCheckbox = driver.findElement(By.name("regional"));
        if (! notifyCheckbox.getAttribute("checked").equals("true"))
        	throw new TestFailureException("regional notifications box not checked");
        notifyCheckbox = driver.findElement(By.name("radius"));
        if (! notifyCheckbox.getAttribute("checked").equals("true"))
        	throw new TestFailureException("radius notifications box not checked");
        
        WebElement agreeCheckbox = driver.findElement(By.name("cca_agree"));
        if (agreeCheckbox.getAttribute("checked") != null)
        	throw new TestFailureException("CCA agreement checkbox checked by default");
        agreeCheckbox.click();
        agreeCheckbox.submit();

        StringBuilder registerSuccessMessage = new StringBuilder();
        registerSuccessMessage.append("Your information has been submitted into our system. ");
        registerSuccessMessage.append("You will now be sent an email with a web link, ");
        registerSuccessMessage.append("you need to open that link in your web browser within ");
        registerSuccessMessage.append("24 hours or your information will be removed from our system!");
        if (! driver
        		.findElement(By.cssSelector(".story"))
        		.findElement(By.tagName("p"))
        		.getText()
        		.equals(registerSuccessMessage.toString()))
        	throw new TestFailureException("Registration failed!");

        /*
         * 2. activate user at MANAGEMENT_SYSTEM_URI
         */
        driver.get(TestConfig.MANAGEMENT_SYSTEM_URI);
        driver.findElement(By.name("login_name"))
              .sendKeys(email);
        driver.findElement(By.name("login_password"))
              .sendKeys(password);
        driver.findElement(By.name("submit"))
              .click();
        if (! driver
        		.findElement(By.tagName("h1"))
        		.getText()
        		.equals("Dashboard"))
        	throw new TestFailureException("Can't login to management system!");

        List<WebElement> headerNavLinks = driver.findElement(By.id("header-navigation"))
                                                .findElements(By.tagName("a"));
        
        for (WebElement link : headerNavLinks) {
            if (link.getText().equals("Mail")) {
                link.click();
                break;
            }
        }

        if (! driver
        		.findElement(By.tagName("h1"))
        		.getText()
        		.equals("View own Mail"))
        	throw new TestFailureException("Can't view own mails!");

        List<WebElement> tableRows = driver.findElement(By.tagName("table"))
                                           .findElements(By.tagName("tr"));
        if (tableRows.size() != 2)
        	throw new TestFailureException("There seem to be more than one email in the inbox, unexpectingly!");

        WebElement secondRow = tableRows.get(1);
        secondRow.findElement(By.tagName("a"))
                 .click();
        
        if (! driver
        		.findElement(By.tagName("h1"))
        		.getText()
        		.equals("Read Mail"))
        	throw new TestFailureException("Can't view activation mail!");
        
        String mailText = driver.findElement(By.id("content")).getText();
        String activationUri = TestHelper.extractActivationUri(mailText);
        driver.get(activationUri);

        if (! driver
        		.findElement(By.className("story"))
        		.findElement(By.tagName("h3"))
        		.getText()
        		.equals("Updated"))
        	throw new TestFailureException("Cannot activate test user!");

        // Verify emailadress.
        // @todo test the other two options: don't verify and notify support
        driver.findElement(By.name("Yes"))
              .click();
        StringBuilder activationSuccesMessage = new StringBuilder("Updated\n");
        activationSuccesMessage.append("Your account and/or email address has been verified. ");
        activationSuccesMessage.append("You can now start issuing certificates for this address.");
        
        if (! driver
        		.findElement(By.className("story"))
        		.getText()
        		.equals(activationSuccesMessage.toString()))
        	throw new TestFailureException("Failed to activate test user account!");

	}
	
	public void login(WebDriver driver) throws TestFailureException {
        /*
         * 3. login at TEST_SYSTEM_URI
         */
        driver.get(TestConfig.TEST_SYSTEM_URI + "index.php?id=4");
        driver.findElement(By.name("email"))
              .sendKeys(email);
        driver.findElement(By.name("pword"))
              .sendKeys(password);
        driver.findElement(By.name("process"))
              .click();
        
        List<WebElement> loggedInNav = driver.findElement(By.id("home"))
                                             .findElements(By.tagName("a"));
        
        if (! loggedInNav.get(0).getAttribute("href").equals(TestConfig.TEST_SYSTEM_URI + "index.php")) {
             throw new TestFailureException("There is no home nav link!");
        }
        
        if (! loggedInNav.get(1).getAttribute("href").equals(TestConfig.TEST_SYSTEM_URI + "account.php?id=logout")) {
            throw new TestFailureException("There is no logout nav link!");
        }
	}
     
	public void logout (WebDriver driver) throws TestFailureException {
        /*
         * 4. logout at TEST_SYSTEM_URI
         */
        driver.get(TestConfig.TEST_SYSTEM_URI + "account.php?id=logout");
        if (! driver
        		.findElement(By.className("story"))
        		.findElement(By.tagName("h3"))
        		.getText()
        		.equals("Are you new to CAcert?"))
        	throw new TestFailureException("Logout failed!");
	}
	
	public void delete(WebDriver driver) throws TestFailureException {
		logout(driver); //just in case
		
		//log in as admin
		CACertUser admin = new CACertUser();
		admin.setEmail(TestConfig.adminEmail);
		admin.setPassword(TestConfig.adminPassword);
		admin.login(driver);
		
		//find test account to delete
		driver.get(TestConfig.TEST_SYSTEM_URI + "account.php?id=42");
		
		driver.findElement(By.name("email")).sendKeys(email);
		driver.findElement(By.name("process")).click();
		
		if (driver
				.findElement(By.cssSelector(".story"))
				.getText()
				.equals("Es wurde kein passender Benutzer " + email + " gefunden"))
			throw new TestFailureException("Test user account not found");
		
		WebElement deleteLink = driver.findElement(By.partialLinkText("Konto löschen"));
		String deleteURI = deleteLink.getAttribute("href");
		driver.get(deleteURI);
		driver.findElement(By.name("process")).click();
	}
	
	//begin autogenerated get and set methods
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
