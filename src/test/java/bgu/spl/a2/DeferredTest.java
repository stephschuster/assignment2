package bgu.spl.a2;

import bgu.spl.a2.Deferred;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;
/**
 * Created by stephanieschustermann on 10/12/2016.
 */

public class DeferredTest {

    private Deferred<Integer> deferred;

    // I added this because we need a "new" deferred object every time -
    // ask me later if you want more explanation
    @Before
    public void setup() {
        deferred = new Deferred<Integer>();
    }

    /*
        @pre none
        @post none
        @throws IllegalStateException
    */
    @Test(expected = IllegalStateException.class)
    public void getResultFailed() {
        deferred.get();
    }

    /*
        @pre none
        @post none
        @return the result value
    */
    @Test
    public void getResultSuccess() {
        Integer expected = 2;
        deferred.resolve(2);
        Integer result = deferred.get();
        assertTrue(expected == result);
    }

    /*
        @pre none
        @post none
        @return isResolved() == true
    */
    @Test
    public void isResolvedTrue() {
        deferred.resolve(2);
        assertTrue(deferred.isResolved());
    }

    /*
        @pre none
        @post none
        @return isResolved() == false
    */
    @Test
    public void isResolvedFalse() {
        assertFalse(deferred.isResolved());
    }

    /*
        @param T value to resolve the deferred value
        @pre isRevolve() == true
        @post none
        @throws IllegalStateException
    */
    @Test(expected = IllegalStateException.class)
    public void resolvedAlreadyResolvedException() {
        deferred.resolve(1);
        deferred.resolve(2);
    }

    /*
        @param T value to resolve the deferred value
        @pre isRevolve() == false
        @post isRevolve() == true
        @post getResolve() == (@param)
    */
    @Test
    public void resolveCheckResolveValue() {
        deferred.resolve(1);
        assertTrue(deferred.isResolved());
        assertTrue(deferred.get() == 1);
    }

    /*
        @param T value to resolve the deferred value
        @pre callbackList[] = false
        @post callbackList[]  = true
        @post isResolved() = true
    */
    @Test
    public void resolveCheckNotify() {
        final boolean[] result = {false, false};
        deferred.whenResolved(() -> result[0] = !result[0]);
        deferred.whenResolved(() -> result[1] = !result[1]);


        deferred.resolve(4);
        try {
            Thread.sleep(2000);
            assertTrue(result[0] == true);
            assertTrue(result[1] == true);
            assertTrue(deferred.isResolved());

        } catch (InterruptedException e) {
            assertTrue(false);
        }

    }

    /*
        @param T value to resolve the deferred value
        @pre callbackList.size = 0
        @post isResolved() = false
    */
    @Test
    public void resolveCheckDontWakeUpTwice() {
        final boolean[] result = {false, false};
        deferred.whenResolved(() -> result[0] = !result[0]);
        deferred.resolve(5);
        deferred.whenResolved(() -> result[1] = !result[1]);

        try {
            Thread.sleep(2000);
            assertTrue(result[0] == true);
            assertTrue(result[1] == true);
        } catch (InterruptedException e) {
            assertTrue(false);
        }


    }

    /*
        @param runnable callback
        @pre isResolved() = true
        @pre callbackList.size = 0
        @post callbackList.size = 0
    */
    @Test
    public void whenResolveAddCallbackAfterSolution() {
        deferred.resolve(1);
        final boolean[] result = {false};
        deferred.whenResolved(() -> result[0] = !result[0]);
        try {
            Thread.sleep(2000);
            assertTrue(result[0]);
        } catch (InterruptedException e)

        {
            assertTrue(false);
        }
    }
}
