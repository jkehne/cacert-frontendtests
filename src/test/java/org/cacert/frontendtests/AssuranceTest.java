package org.cacert.frontendtests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AssuranceTest {

	private WebDriver driver;
	private CACertUser testUser, assurer;
	
	public AssuranceTest() {
		assurer = new CACertUser();
		assurer.setEmail(TestConfig.assurerEmail);
		assurer.setPassword(TestConfig.assurerPassword);
	}
	
	@Before public void setUp() {
        //setup driver
		try {
            driver = TestHelper.createDriverByType(TestHelper.DriverType.HTMLUNIT);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        
		//create test user
		String firstName = TestHelper.generateFirstName();
		String lastName = TestHelper.generateLastName();
		testUser = new CACertUser(
				firstName, 
				lastName, 
				TestHelper.generateEmailAddres(firstName, lastName), 
				TestHelper.generatePassword(), 
				1980, 
				1, 
				1);
		
		try {
			testUser.register(driver);
		} catch (TestFailureException e) {
			fail(e.getMessage());
		}
    }

    @After public void tearDown() {
        try {
			testUser.delete(driver);
		} catch (TestFailureException e) {
			fail(e.getMessage());
		}
    	
    	driver.close();
        driver = null;
    }

    private void performAssurance(
    		String location,
    		String date,
    		boolean personalMeeting,
    		boolean identityCorrect,
    		boolean readAP,
    		int points) throws TestFailureException {
    	
    	assurer.login(driver);
    	
    	//get search page
    	driver.get(TestConfig.TEST_SYSTEM_URI + "wot.php?id=5");
    	
    	//lookup test user
    	driver.findElement(By.name("email")).sendKeys(testUser.getEmail());
    	driver.findElement(By.name("process")).click();
    	try {
	    	WebElement story = driver
	    		.findElement(By.cssSelector(".story"))
	        	.findElement(By.tagName("p"));
	        throw new TestFailureException("Test user email address not found: " + story.getText());
    	} catch (NoSuchElementException e) {
    		//NOP (expected)
    	}

    	//set checkboxes according to parameters
    	WebElement checkbox = driver.findElement(By.name("certify"));
        if (checkbox.getAttribute("checked") != null)
        	throw new TestFailureException("Face to face meeting checkbox checked by default");
        checkbox.click();

    	checkbox = driver.findElement(By.name("assertion"));
        if (checkbox.getAttribute("checked") != null)
        	throw new TestFailureException("Information correct checkbox checked by default");
        checkbox.click();

    	checkbox = driver.findElement(By.name("rules"));
        if (checkbox.getAttribute("checked") != null)
        	throw new TestFailureException("AP read checkbox checked by default");
        checkbox.click();
        
        //fill out text boxes according to parameters
        if (location != null)
        	driver.findElement(By.name("location")).sendKeys(location);
        if (date != null) {
        	//date is pre-filled by default!
        	WebElement dateField = driver.findElement(By.name("date"));
        	dateField.clear();
        	dateField.sendKeys(date);
        }
        driver.findElement(By.name("points")).sendKeys(String.valueOf(points));
        
        //submit the form
        driver.findElement(By.name("process")).click();
        
        //now, check if the assurance was successful
    	try {
	    	WebElement story = driver
	    		.findElement(By.cssSelector(".story"))
	        	.findElement(By.tagName("p"));
	        throw new TestFailureException(story.getText());
    	} catch (NoSuchElementException e) {
    		//NOP (expected)
    	}
        
    	//TODO: Assurance appears broken on test server. Check what a successful assurance should look like.

    	assurer.logout(driver);
    }
    
    @Test public void assuranceTestEmptyForm() {
    	try {
			performAssurance(null, "", false, false, false, 0);
			assertTrue("Null assurance succeeded",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestNoMeeting() {
    	try {
			performAssurance("CACert test server", null, false, true, true, 10);
			assertTrue("Assurance succeeded without personal meeting box checked",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestNoIdentity() {
    	try {
			performAssurance("CACert test server", null, true, false, true, 10);
			assertTrue("Assurance succeeded without identity correct box checked",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestNoAP() {
    	try {
			performAssurance("CACert test server", null, true, true, false, 10);
			assertTrue("Assurance succeeded without AP read box checked",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestNoLocation() {
    	try {
			performAssurance(null, null, true, true, true, 10);
			assertTrue("Assurance succeeded without location set",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestNoDate() {
    	try {
			performAssurance("CACert test server", "", true, true, true, 10);
			assertTrue("Assurance succeeded without date set",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    //TODO: Is this test supposed to fail? Could be rounded to 0 points
    @Test public void assuranceTestNegativePoints() {
    	try {
			performAssurance("CACert test server", null, true, true, true, -1);
			assertTrue("Assurance succeeded with negative points value",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }

    @Test public void assuranceTestZeroPoints() {
    	try {
			performAssurance("CACert test server", null, true, true, true, 0);
		} catch (TestFailureException e) {
			assertTrue("Zero points assurance failed: " + e.getMessage(),false);
		}
    }

    @Test public void assuranceTestTenPoints() {
    	try {
			performAssurance("CACert test server", null, true, true, true, 10);
		} catch (TestFailureException e) {
			assertTrue("Ten points assurance failed: " + e.getMessage(),false);
		}
    }

    @Test public void assuranceTestMaxPoints() {
    	try {
			performAssurance("CACert test server", null, true, true, true, TestConfig.assurerMaxPoints);
		} catch (TestFailureException e) {
			assertTrue(
					"Assurance using " 
							+ TestConfig.assurerMaxPoints 
							+ " failed: "
							+ e.getMessage(),
					false);
		}
    }

    @Test public void assuranceTestTooManyPoints() {
    	try {
			performAssurance("CACert test server", null, true, true, true, 1000);
			assertTrue("Assurance succeeded with 1000 points",false);
		} catch (TestFailureException e) {
			//NOP, test is expected to fail
		}
    }
}
