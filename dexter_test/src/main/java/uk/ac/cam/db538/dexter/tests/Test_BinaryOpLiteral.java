package uk.ac.cam.db538.dexter.tests;

public class Test_BinaryOpLiteral implements PropagationTest {

    @Override
    public int propagate(int arg1) {
        return arg1 + 1234;
    }

	@Override
	public String getName() {
		return "BinaryOpLiteral";
	}

	@Override
	public String getDescription() {
		return "Value used in 'add-int/lit rX, <-->, #1234'";
	}
}
