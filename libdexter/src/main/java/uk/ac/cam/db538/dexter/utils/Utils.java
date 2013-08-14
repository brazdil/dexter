package uk.ac.cam.db538.dexter.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Utils {

	public static <T> List<T> finalList(List<? extends T> list) {
		if (list == null || list.isEmpty())
			return Collections.emptyList();
		else
			return Collections.unmodifiableList(new ArrayList<T>(list));
	}

	public static interface NameAcceptor {
		public boolean accept(String name);
	}
	
	public static String generateName(String oldPrefix, String oldSuffix, NameAcceptor acceptor) {
		long id = 0L;
		String infix = "";
		while (!acceptor.accept(oldPrefix + infix + oldSuffix))
			infix = "$$" + Long.toString(++id);
		
		return oldPrefix + infix + oldSuffix;
	}

	public static <T> List<? extends T> concat(Collection<? extends T> list1, Collection<? extends T> list2) {
		List<T> result = new ArrayList<T>(list1.size() + list2.size());
		result.addAll(list1);
		result.addAll(list2);
		return result;
	}

	public static <T> List<? extends T> concat(Collection<? extends T> list1, T elem) {
		List<T> result = new ArrayList<T>(list1.size() + 1);
		result.addAll(list1);
		result.add(elem);
		return result;
	}
}
