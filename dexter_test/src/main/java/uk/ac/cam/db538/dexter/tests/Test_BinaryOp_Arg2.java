package uk.ac.cam.db538.dexter.tests;

import java.util.Random;


public class Test_BinaryOp_Arg2 implements PropagationTest {

    @Override
    public int propagate(int arg2) {
        int arg1 = (new Random()).nextInt();
        return arg1 + arg2;
    }

	@Override
	public String getName() {
		return "BinaryOp: argB";
	}

	@Override
	public String getDescription() {
		return "Value used in 'add-int rX, rand(), <-->'";
	}
}
