package uk.ac.cam.db538.dexter.tests;


public class Test_Const implements SourceTest {

    @Override
    public int generate() {
        return 0xDEC0DED;
    }

	@Override
	public String getName() {
		return "0xDEC0DED source";
	}

	@Override
	public String getDescription() {
		return "Only for unit testing. The constant should get tainted";
	}
}
