package uk.ac.cam.db538.dexter.tests;

/**
 * Created by db538 on 7/22/13.
 */
public class SourceTestExerciser extends TestExerciser {

    private final SourceTest test;

    public SourceTestExerciser(SourceTest test) {
        this.test = test;
    }

    @Override
    public Test getTest() {
        return test;
    }

    @Override
    public boolean run() {
        return TaintChecker.isTainted(test.generate());
    }
}

