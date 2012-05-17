/*
 * LICENSE
 * "THE BEER-WARE LICENSE" (Revision 42):
 * "Sven Strittmatter" <ich(at)weltraumschaf(dot)de> wrote this file.
 * As long as you retain this notice you can do whatever you want with
 * this stuff. If we meet some day, and you think this stuff is worth it,
 * you can buy me a beer in return.
 */
package org.cacert.frontendtests;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Sven Strittmatter <ich@weltraumschaf.de>
 */
public class LoginTest {

    private WebDriver driver;

    @Before public void setUp() {
        try {
            driver = TestHelper.createDriverByType(TestHelper.DriverType.HTMLUNIT);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @After public void tearDown() {
        driver.close();
        driver = null;
    }

    //TODO: More test cases
    @Test public void registerActivateAndLoginNewUser() {
    	String firstName = TestHelper.generateFirstName();
    	String lastName = TestHelper.generateLastName();
    	
    	CACertUser testUser = new CACertUser(
    			firstName, 
    			lastName, 
    			TestHelper.generateEmailAddres(firstName, lastName), 
    			TestHelper.generatePassword(), 
    			1980, 
    			1, 
    			1);
    	try {
			testUser.register(driver);
	    	testUser.login(driver);
	    	testUser.logout(driver);
		} catch (TestFailureException e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage(), false);
		}
    }
}
