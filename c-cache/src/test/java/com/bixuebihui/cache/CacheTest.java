package com.bixuebihui.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.junit.jupiter.api.Test;

public class CacheTest {
    //	Be Careful

    //If a NeedsRefreshException is raised you have to invoke admin.putInCache or even admin.cancelUpdate to avoid deadlock situation.
    GeneralCacheAdministrator admin = new GeneralCacheAdministrator();
    private boolean updated = false;


    @Test
    public void testFireFlash() {
        test1();
        test1();

        test1();
        System.out.println("before flash");
        admin.flushEntry("myKey");
        System.out.println("after flash");
        test1();


        test1();

    }

    //Typical use with fail over
    public void test1() {
        String myKey = "myKey";
        String myValue;
        int myRefreshPeriod = 1000;
        try {
            // Get from the cache
            myValue = (String) admin.getFromCache(myKey, myRefreshPeriod);
            System.out.println("get from cache!");
        } catch (NeedsRefreshException nre) {
            try {
                // Get the value (probably from the database)
                myValue = "This is the content retrieved.";
                // Store in the cache
                admin.putInCache(myKey, myValue);
                System.out.println("put in cache");
            } catch (Exception ex) {
                // We have the current content if we want fail-over.
                myValue = (String) nre.getCacheContent();
                // It is essential that cancelUpdate is called if the
                // cached content is not rebuilt
                admin.cancelUpdate(myKey);
            }
        }
    }

    @Test

    //Typical use without fail over
    public void test2() {
        String myKey = "myKey";
        String myValue;
        int myRefreshPeriod = 1000;
        try {
            // Get from the cache
            myValue = (String) admin.getFromCache(myKey, myRefreshPeriod);
        } catch (NeedsRefreshException nre) {
            try {
                // Get the value (probably from the database)
                myValue = "This is the content retrieved.";
                // Store in the cache
                admin.putInCache(myKey, myValue);
                updated = true;
            } finally {
                if (!updated) {
                    // It is essential that cancelUpdate is called if the
                    // cached content could not be rebuilt
                    admin.cancelUpdate(myKey);
                }
            }
        }
    }
}
