package uk.ac.cam.db538.dexter.aux;

import java.lang.ref.WeakReference;

public class SafeHashMap<K, V> {
  
  private Entry<K, V>[] table; // hash table
  private int mask; // module mask

  public SafeHashMap() {
	  init(1024); 
  }
  
  SafeHashMap(int size) {
	  init(size);
  }
  
  @SuppressWarnings("unchecked")
  private final void init(int size) {
	// size must be a power of two
    this.mask = size - 1;
    this.table = new Entry[size];
  }

  public final Object get(K obj) {
    if (obj == null)
      return 0;

    // generate hash code and table index
    int objTableIndex = obj.getClass().hashCode() & mask;

    synchronized (table) {
      Entry<K, V> currentEntry = table[objTableIndex];
      Entry<K, V> previousEntry = null;
      while (currentEntry != null) {
        // retrieve reference from the entry
        Object entryObj = currentEntry.key.get();

        if (entryObj == null) {
          // it has been GCed
          // remove it from the entry list
          if (previousEntry == null)
            table[objTableIndex] = currentEntry.next;
          else
            previousEntry.next = currentEntry.next;

          // don't update previousEntry, just move to the next one
          currentEntry = currentEntry.next;
        } else if (entryObj == obj) {
          // found it, move to front and return

          // move it to the beginning of the list (temporal locality)
          if (previousEntry != null) {
            previousEntry.next = currentEntry.next;
            currentEntry.next = table[objTableIndex];
            table[objTableIndex] = currentEntry;
          }

          return currentEntry.value;
        } else {
          // move to another entry
          previousEntry = currentEntry;
          currentEntry = currentEntry.next;
        }
      }
    }

    return null;
  }

  public final void set(K key, V value) {
    if (key == null)
      return;

    // generate hash code and table index
    int objTableIndex = key.getClass().hashCode() & mask;

    synchronized (table) {
      // try to update existing entry
      Entry<K, V> currentEntry = table[objTableIndex];
      Entry<K, V> previousEntry = null;
      while (currentEntry != null) {
        // retrieve reference from the entry
        Object entryObj = currentEntry.key.get();

        if (entryObj == null) {
          // it has been GCed
          // remove it from the entry list
          if (previousEntry == null)
            table[objTableIndex] = currentEntry.next;
          else
            previousEntry.next = currentEntry.next;

          // don't update previousEntry, just move to the next one
          currentEntry = currentEntry.next;
        } else if (entryObj == key) {
          // found it, update
          currentEntry.value = value;

          // move it to the beginning of the list (temporal locality)
          if (previousEntry != null) {
            previousEntry.next = currentEntry.next;
            currentEntry.next = table[objTableIndex];
            table[objTableIndex] = currentEntry;
          }

          // stop searching
          break;
        } else {
          // move to next entry
          previousEntry = currentEntry;
          currentEntry = currentEntry.next;
        }
      }

      if (currentEntry == null) {
        // object not in the map
        // create new entry and put it at the beginning of the list
        Entry<K, V> newEntry = new Entry<K, V>();
        newEntry.key = new WeakReference<K>(key);
        newEntry.value = value;
        newEntry.next = table[objTableIndex];
        table[objTableIndex] = newEntry;
      }
    }
  }

  static class Entry<K, V> {
    public WeakReference<K> key;
    public V value;
    public Entry<K, V> next;
  }
}
