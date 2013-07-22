package uk.ac.cam.db538.dexter.tests;

/**
 * Created by db538 on 7/22/13.
 */
public class TaintChecker {
    private TaintChecker() { }

    public static boolean isTainted(int val) {
        // to be overwritten by the instrumentation
        return false;
    }
}
