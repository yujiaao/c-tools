package com.bixuebihui.util.email;

import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import static com.bixuebihui.util.email.SMTPMXLookup.isAddressValid;

@Disabled("It is too slow")
public class SMTPMXLookupTest {

    @Test
    public void testIsAddressValid() {
        String[] testData = {
           /*
            "real@rgagnon.com",
            "you@acquisto.net",
            "fail.me@nowhere.spam", // Invalid domain name
            "arkham@bigmeanogre.net", // Invalid address
            "nosuchaddress@yahoo.com" // Failure of this method
            */
            "131212312312312@msn.com",
            "xwx@live.cn",
            "www@qsn.so",
            "xwx@g.cn",
        };

        for (String testDatum : testData) {
            System.out.println(testDatum + " is valid? " +
                    isAddressValid(testDatum));
        }
    }

}
