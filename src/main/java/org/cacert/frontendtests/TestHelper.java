/*
 * LICENSE
 * "THE BEER-WARE LICENSE" (Revision 42):
 * "Sven Strittmatter" <ich(at)weltraumschaf(dot)de> wrote this file.
 * As long as you retain this notice you can do whatever you want with
 * this stuff. If we meet some day, and you think this stuff is worth it,
 * you can buy me a beer in return.
 */
package org.cacert.frontendtests;

//import junit.framework.TestCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Sven Strittmatter <ich@weltraumschaf.de>
 */
public class TestHelper {

    /**
     * Supported driver types.
     */
    public enum DriverType {
        FIREFOX,
        CHROME,
        INTERNET_EXPLORER,
        HTMLUNIT
    }
    
    private static RandomString stringGenerator = null;

    static {
    	stringGenerator = new RandomString(10);
    }
    /**
     * Determines desired WebDriver type and returns accordingly.
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static WebDriver createDriverByType(DriverType type) throws Exception {
        WebDriver driver;
        switch (type) {
            case FIREFOX:
                driver = new FirefoxDriver();
                return driver;
            case CHROME:
                driver = new ChromeDriver();
                return driver;
            case INTERNET_EXPLORER:
                driver = new InternetExplorerDriver();
                return driver;
            case HTMLUNIT:
                driver = new HtmlUnitDriver();
                return driver;
            default:
                throw new Exception("Invalid driver type passed " + type + "!");
        }
    }
    
    public static String generateFirstName() {
        return "First" + stringGenerator.next();
    }

    public static String generateLastName() {
        return "Last" + stringGenerator.next();
    }

    public static String generatePassword() {
        return "PW-" + stringGenerator.next();
    }

    public static String generateEmailAddres(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder(firstName.toLowerCase());
        sb.append(".")
          .append(lastName.toLowerCase())
          .append("@")
          .append(TestConfig.MAIL_DOMAIN);
        return sb.toString();
    }

    public static String extractActivationUri(String mailText) {
        String lines[] = mailText.split("[\\r\\n]+");

        for (String line : lines) {
            if (line.startsWith("http://")) {
                return line;
            }
        }
        
        return null;
    }

}
