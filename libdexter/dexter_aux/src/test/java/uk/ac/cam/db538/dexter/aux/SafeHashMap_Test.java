package uk.ac.cam.db538.dexter.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.junit.Test;

import uk.ac.cam.db538.dexter.aux.SafeHashMap.Entry;

public class SafeHashMap_Test {

  @SuppressWarnings("unchecked")
private static Entry<Object, Integer>[] getMap(SafeHashMap<Object, Integer> obj) {
    try {
      Field fH = obj.getClass().getDeclaredField("table");
      fH.setAccessible(true);
      return (Entry<Object, Integer>[]) fH.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Couldn't get the internal H field of ObjectTaintStorage");
      return null;
    }
  }

  private static void forceGC() {
    Object obj = new Object();
    WeakReference<Object> ref = new WeakReference<Object>(obj);
    obj = null;
    while(ref.get() != null) {
      System.gc();
    }
  }

  @Test
  public void testInit() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);
    Entry<Object, Integer>[] H = getMap(map);
    assertEquals(16, H.length);
  }

  @Test
  public void testSet_Null() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);
    map.set(null, 1);

    // expect nothing set

    for (Entry<Object, Integer> e : getMap(map))
      assertTrue(e == null);
  }

  @Test
  public void testSet_Single() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj = new Object();
    map.set(obj, 32);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single entry
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single entry");
      }

    assertEquals(obj, entry.key.get());
    assertEquals(Integer.valueOf(32), entry.value);
    assertEquals(null, entry.next);
  }

  @Test
  public void testSet_DoubleSameType() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();

    map.set(obj1, 32);
    map.set(obj2, 64);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot, but two entries
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj2, entry.key.get());
    assertEquals(Integer.valueOf(64), entry.value);

    assertEquals(obj1, entry.next.key.get());
    assertEquals(Integer.valueOf(32), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testSet_DoubleDifferentType() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj1 = new Object();
    Object obj2 = new Integer(1);

    map.set(obj1, 32);
    map.set(obj2, 64);

    Entry<Object, Integer>[] H = getMap(map);

    // expect two occupied slots, each with one entry
    Entry<Object, Integer> entry1 = null;
    Entry<Object, Integer> entry2 = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry1 == null)
          entry1 = e;
        else if (entry2 == null)
          entry2 = e;
        else
          fail("should only have two slots occupied");
      }

    if (entry2 != null) {
      // the allocation depends on the hashing (random),
      // so swap if they are the other way round
      if (entry1.key.get() == obj2) {
        Entry<Object, Integer> temp = entry2;
        entry2 = entry1;
        entry1 = temp;
      }

      assertEquals(obj1, entry1.key.get());
      assertEquals(Integer.valueOf(32), entry1.value);
      assertEquals(null, entry1.next);

      assertEquals(obj2, entry2.key.get());
      assertEquals(Integer.valueOf(64), entry2.value);
      assertEquals(null, entry2.next);
    } else {
      // might have happened that the hashes collide
      assertEquals(obj2, entry1.key.get());
      assertEquals(Integer.valueOf(64), entry1.value);

      assertEquals(obj1, entry1.next.key.get());
      assertEquals(Integer.valueOf(32), entry1.next.value);
      assertEquals(null, entry1.next.next);
    }
  }

  @Test
  public void testSet_UpdateFirst() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();

    map.set(obj1, 32);
    map.set(obj2, 64);
    map.set(obj2, 1);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot, but two entries
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj2, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(obj1, entry.next.key.get());
    assertEquals(Integer.valueOf(32), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testSet_UpdateSecond() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();

    map.set(obj1, 32);
    map.set(obj2, 64);
    map.set(obj1, 1);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot, but two entries
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj1, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(obj2, entry.next.key.get());
    assertEquals(Integer.valueOf(64), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testSet_RemoveGCed_First() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj3, 128);
    map.set(obj2, 64);
    map.set(obj1, 32);

    // remove first object
    obj1 = null;
    forceGC();

    // update second object
    map.set(obj2, 1);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj2, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(obj3, entry.next.key.get());
    assertEquals(Integer.valueOf(128), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testSet_RemoveGCed_Second_DontMove() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj3, 128);
    map.set(obj2, 64);
    map.set(obj1, 32);

    // remove first object
    obj2 = null;
    forceGC();

    // update first object
    // (doesn't remove obj2)
    map.set(obj1, 1);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj1, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(null, entry.next.key.get());
    assertEquals(Integer.valueOf(64), entry.next.value);

    assertEquals(obj3, entry.next.next.key.get());
    assertEquals(Integer.valueOf(128), entry.next.next.value);
    assertEquals(null, entry.next.next.next);
  }

  @Test
  public void testSet_RemoveGCed_Second_Move() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(16);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj3, 128);
    map.set(obj2, 64);
    map.set(obj1, 32);

    // remove first object
    obj2 = null;
    forceGC();

    // update third object
    // (removes obj2)
    map.set(obj3, 1);

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj3, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(obj1, entry.next.key.get());
    assertEquals(Integer.valueOf(32), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testGet_Null() {
	SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);
    assertEquals(0, map.get(null));
  }

  @Test
  public void testGet_NotFound_Empty() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);
    assertEquals(null, map.get(new Object()));
  }

  @Test
  public void testGet_NotFound_NonEmpty() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);
    map.set(new Object(), 32);
    assertEquals(null, map.get(new Object()));
  }

  @Test
  public void testGet_Found_First() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj = new Object();

    map.set(new Object(), 1);
    map.set(new Object(), 2);
    map.set(new Object(), 4);
    map.set(obj, 32);

    assertEquals(32, map.get(obj));

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj, entry.key.get());
    assertEquals(Integer.valueOf(32), entry.value);

    assertEquals(Integer.valueOf(4), entry.next.value);
    assertEquals(Integer.valueOf(2), entry.next.next.value);
    assertEquals(Integer.valueOf(1), entry.next.next.next.value);

    assertEquals(null, entry.next.next.next.next);
  }

  @Test
  public void testGet_Found_Second() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj = new Object();

    map.set(new Object(), 1);
    map.set(new Object(), 2);
    map.set(obj, 32);
    map.set(new Object(), 4);

    assertEquals(32, map.get(obj));

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj, entry.key.get());
    assertEquals(Integer.valueOf(32), entry.value);

    assertEquals(Integer.valueOf(4), entry.next.value);
    assertEquals(Integer.valueOf(2), entry.next.next.value);
    assertEquals(Integer.valueOf(1), entry.next.next.next.value);

    assertEquals(null, entry.next.next.next.next);
  }

  @Test
  public void testGet_RemoveGCed_First() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj1, 1);
    map.set(obj2, 2);
    map.set(obj3, 4);

    obj3 = null;
    forceGC();

    assertEquals(2, map.get(obj2));

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj2, entry.key.get());
    assertEquals(Integer.valueOf(2), entry.value);

    assertEquals(obj1, entry.next.key.get());
    assertEquals(Integer.valueOf(1), entry.next.value);
    assertEquals(null, entry.next.next);
  }

  @Test
  public void testGet_RemoveGCed_Second_DontMove() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj1, 1);
    map.set(obj2, 2);
    map.set(obj3, 4);

    obj2 = null;
    forceGC();

    assertEquals(4, map.get(obj3));

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj3, entry.key.get());
    assertEquals(Integer.valueOf(4), entry.value);

    assertEquals(null, entry.next.key.get());
    assertEquals(Integer.valueOf(2), entry.next.value);

    assertEquals(obj1, entry.next.next.key.get());
    assertEquals(Integer.valueOf(1), entry.next.next.value);
    assertEquals(null, entry.next.next.next);
  }

  @Test
  public void testGet_RemoveGCed_Second_Move() {
    SafeHashMap<Object, Integer> map = new SafeHashMap<Object, Integer>(4);

    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    map.set(obj1, 1);
    map.set(obj2, 2);
    map.set(obj3, 4);

    obj2 = null;
    forceGC();

    assertEquals(1, map.get(obj1));

    Entry<Object, Integer>[] H = getMap(map);

    // expect a single occupied slot
    Entry<Object, Integer> entry = null;
    for (Entry<Object, Integer> e : H)
      if (e != null) {
        if (entry == null)
          entry = e;
        else
          fail("should only have a single slot occupied");
      }

    assertEquals(obj1, entry.key.get());
    assertEquals(Integer.valueOf(1), entry.value);

    assertEquals(obj3, entry.next.key.get());
    assertEquals(Integer.valueOf(4), entry.next.value);
    assertEquals(null, entry.next.next);
  }
}

