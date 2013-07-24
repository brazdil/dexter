package uk.ac.cam.db538.dexter.tests;

public class TestList {
    private TestList() { }

    private final static TestExerciser[] tests = new TestExerciser[] {
        new SourceTestExerciser(new Test_Const()),
        new PropagationTestExerciser(new Test_BinaryOp_Arg1()),
        new PropagationTestExerciser(new Test_BinaryOp_Arg2()),
        new PropagationTestExerciser(new Test_BinaryOpLiteral()),
    };
    
    public static TestExerciser[] getTestList() {
        return tests;
    }
    
    public static Integer getTestCount() {
    	return tests.length;
    }
    
    public static Boolean runTest(int index) {
    	return tests[index].run();
    }
    
    public static String getTestName(int index) {
    	return tests[index].getTest().getName();
    }

    public static String getTestDescription(int index) {
    	return tests[index].getTest().getDescription();
    }
}
