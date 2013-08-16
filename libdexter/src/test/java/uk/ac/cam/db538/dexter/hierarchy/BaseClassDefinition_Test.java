package uk.ac.cam.db538.dexter.hierarchy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BaseClassDefinition_Test extends HierarchyTest {

    @Test
    public void test_IsRoot() {
        assertTrue(classObject.isRoot());
        assertFalse(classThrowable.isRoot());
        assertFalse(classException.isRoot());
        assertFalse(classList.isRoot());
    }

    @Test
    public void test_IsAbstract() {
        assertFalse(classException.isAbstract());
        assertTrue(classList.isAbstract());
    }

    @Test
    public void test_IsChildOf_Reflexivity() {
        assertTrue(classObject.isChildOf(classObject));
        assertTrue(classException.isChildOf(classException));
        assertTrue(classThrowable.isChildOf(classThrowable));
        assertTrue(classList.isChildOf(classList));
    }

    @Test
    public void test_IsChildOf() {
        assertTrue(classException.isChildOf(classObject));
        assertFalse(classObject.isChildOf(classException));

        assertTrue(classException.isChildOf(classThrowable));
        assertFalse(classThrowable.isChildOf(classException));

        assertTrue(classList.isChildOf(classObject));
    }

    @Test
    public void test_CommonParent() {
        assertEquals(classThrowable, classException.getCommonParent(classError));
        assertEquals(classThrowable, classError.getCommonParent(classException));

        assertEquals(classThrowable, classException.getCommonParent(classThrowable));
        assertEquals(classThrowable, classThrowable.getCommonParent(classException));

        assertEquals(classObject, classException.getCommonParent(classList));
        assertEquals(classObject, classList.getCommonParent(classException));
    }
}
