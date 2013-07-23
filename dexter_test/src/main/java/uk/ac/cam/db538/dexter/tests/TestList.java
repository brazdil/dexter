package uk.ac.cam.db538.dexter.tests;

public class TestList {
    private TestList() { }

    private final static TestExerciser[] tests = new TestExerciser[] {
        new SourceTestExerciser(new Test_Const()),
        new PropagationTestExerciser(new Test_BinaryOp())
    };
    
    public static TestExerciser[] getTestList() {
        return tests;
    }
    
    public static int getTestCount() {
    	return tests.length;
    }
    
    public static boolean runTest(int index) {
    	return tests[index].run();
    }
    
    public static String getTestName(int index) {
    	return tests[index].getName();
    }
}
