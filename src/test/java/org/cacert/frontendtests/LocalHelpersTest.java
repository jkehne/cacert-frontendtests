package org.cacert.frontendtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class LocalHelpersTest {
    @Test public void checkFirstNamePrefix() {
        assertEquals("First", TestHelper.generateFirstName().substring(0, 5));
        assertEquals("First", TestHelper.generateFirstName().substring(0, 5));
        assertEquals("First", TestHelper.generateFirstName().substring(0, 5));
    }

    @Test public void checkLastNamePrefix() {
        assertEquals("Last", TestHelper.generateLastName().substring(0, 4));
        assertEquals("Last", TestHelper.generateLastName().substring(0, 4));
        assertEquals("Last", TestHelper.generateLastName().substring(0, 4));
    }

    @Test public void checkPasswordPrefix() {
        assertEquals("PW-", TestHelper.generatePassword().substring(0, 3));
        assertEquals("PW-", TestHelper.generatePassword().substring(0, 3));
        assertEquals("PW-", TestHelper.generatePassword().substring(0, 3));
    }

    @Test public void checkGeneratedEmailAddreses() {
        assertEquals("hans.dampf@cacert1.it-sls.de", TestHelper.generateEmailAddres("hans", "dampf"));
        assertEquals("hans.dampf@cacert1.it-sls.de", TestHelper.generateEmailAddres("Hans", "dampf"));
        assertEquals("hans.dampf@cacert1.it-sls.de", TestHelper.generateEmailAddres("Hans", "Dampf"));
    }
    
    @Test public void testExtractingUriFromMailText() {
        StringBuilder fixture = new StringBuilder("Read Mail\n");
        fixture.append("Thanks for signing up with CAcert.org, below is the link you need to open ");
        fixture.append("to verify your account. Once your account is verified you will be able to ");
        fixture.append("start issuing certificates till your hearts' content!\n\n");
        fixture.append("http://cacert1.it-sls.de/verify.php?type=email&emailid=240888&hash=57659595e2ade49d072146b0210398cc\n\n");
        fixture.append("Best regards\n");
        fixture.append("CAcert.org Support!\n");
        
        assertEquals(
            "http://cacert1.it-sls.de/verify.php?type=email&emailid=240888&hash=57659595e2ade49d072146b0210398cc", 
            TestHelper.extractActivationUri(fixture.toString())
        );
        assertNull(TestHelper.extractActivationUri("foo bar baz"));
    }
	
}
