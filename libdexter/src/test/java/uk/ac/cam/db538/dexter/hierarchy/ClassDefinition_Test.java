package uk.ac.cam.db538.dexter.hierarchy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClassDefinition_Test extends HierarchyTest {

    @Test
    public void test_ImplementsInterface_List() {
        assertFalse(classObject.implementsInterface(classList));
        assertTrue(classArrayList.implementsInterface(classList));
        assertTrue(classLinkedList.implementsInterface(classList));
        assertFalse(classHashMap.implementsInterface(classList));
    }

    @Test
    public void test_ImplementsInterface_Map() {
        assertFalse(classObject.implementsInterface(classMap));
        assertFalse(classArrayList.implementsInterface(classMap));
        assertFalse(classLinkedList.implementsInterface(classMap));
        assertTrue(classHashMap.implementsInterface(classMap));
    }
}