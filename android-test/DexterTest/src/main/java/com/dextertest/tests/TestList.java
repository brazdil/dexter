package com.dextertest.tests;

/**
 * Created by db538 on 7/22/13.
 */
public class TestList {
    private TestList() { }

    public static TestExerciser[] getTestList() {
        return new TestExerciser[] {
            new SourceTestExerciser(new Test_Const()),
            new PropagationTestExerciser(new Test_BinaryOp())
        };
    }
}
