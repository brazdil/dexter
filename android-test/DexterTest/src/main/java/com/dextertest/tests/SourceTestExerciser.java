package com.dextertest.tests;

/**
 * Created by db538 on 7/22/13.
 */
public class SourceTestExerciser extends TestExerciser {

    private final SourceTest test;

    public SourceTestExerciser(SourceTest test) {
        this.test = test;
    }

    @Override
    public String getName() {
        return test.getClass().getSimpleName();
    }

    @Override
    public boolean run() {
        return TaintChecker.isTainted(test.generate());
    }
}

