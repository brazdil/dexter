package uk.ac.cam.db538.dexter.aux.struct;

import java.lang.ref.WeakReference;

public class Cache {
  
	private static Entry[] table;
	private static int mask;

	static {
		init(1024);
	}
  
	private static final void init(int size) {
		// size must be a power of two
		mask = size - 1;
		table = new Entry[size];
	}

	static final Taint get(Object key) {
    	// generate hash code and table index
		int objTableIndex = key.getClass().hashCode() & mask;

		synchronized (table) {
			Entry currentEntry = table[objTableIndex];
			Entry previousEntry = null;
			
			while (currentEntry != null) {
				// retrieve reference from the entry
				Object entryObj = currentEntry.key.get();

				if (entryObj == null) {
					// it has been GCed, remove it from the entry list
					if (previousEntry == null)
						table[objTableIndex] = currentEntry.next;
					else
						previousEntry.next = currentEntry.next;

					// don't update previousEntry, just move to the next one
					currentEntry = currentEntry.next;
				} else if (entryObj == key) {
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

	final void set(Object key, Taint value) {
		// generate hash code and table index
		int objTableIndex = key.getClass().hashCode() & mask;

		synchronized (table) {
			// try to update existing entry
			Entry currentEntry = table[objTableIndex];
			Entry previousEntry = null;
			
			while (currentEntry != null) {
				// retrieve reference from the entry
				Object entryObj = currentEntry.key.get();

				if (entryObj == null) {
					// it has been GCed, remove it from the entry list
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
				insert(key, value);
			}
		}
	}
	
	static void insert(Object key, Taint value) {
    	// generate hash code and table index
		int objTableIndex = key.getClass().hashCode() & mask;
		
		// create new entry and put it at the beginning of the list
		Entry newEntry = new Entry();
		newEntry.key = new WeakReference<Object>(key);
		newEntry.value = value;
		newEntry.next = table[objTableIndex];
		table[objTableIndex] = newEntry;
	}

	static class Entry {
		public WeakReference<Object> key;
		public Taint value;
		public Entry next;
	}
}
