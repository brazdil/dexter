package uk.ac.cam.db538.dexter.tests;

import java.util.Random;

public class Test_BinaryOp implements PropagationTest {

    @Override
    public int propagate(int argA) {
        int argB = (new Random()).nextInt();
        return argA + argB;
    }
}
