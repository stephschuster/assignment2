package bgu.spl.a2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Medhopz on 12/10/2016.
 */
public class VersionMonitorTest {
    private VersionMonitor testy;
    /*
        @inv (versionNumber >= 0)
    */

    /*
        @pre none
        @post none
        @return the version number
    */
    @Test
    public void getVersion() throws Exception {
        testy = new VersionMonitor();
        int result = testy.getVersion();
        int expected = 0;
        assertTrue(result == expected);
    }

    /*
        @pre none
        @post (testy.getVersion() == @pre(testy.getVersion()) + 1
    */
    @Test
    public void inc_once() throws Exception {
        testy = new VersionMonitor();
        int expected = testy.getVersion() + 1;
        testy.inc();
        int result = testy.getVersion();
        assertTrue(expected == result);
    }

    //TODO : once we understand how
    @Test
    public void awaitTest() throws Exception {
        testy = new VersionMonitor();
        final int num = testy.getVersion();
        final boolean[] result = {false};
        Thread t1 = new Thread(() -> {
            try {
                testy.await(num);
                result[0] = true;
            } catch (InterruptedException e) {
                result[0] = false;
            }
        });
        t1.start();
        try {
            Thread.sleep(2000);
            assertFalse(result[0]);
            testy.inc();
            Thread.sleep(2000);
            assertTrue(result[0]);

        } catch (InterruptedException e) {
            assertTrue(false);
        }
    }

}