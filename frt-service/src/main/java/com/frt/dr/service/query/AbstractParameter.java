package com.frt.dr.service.query;

import java.util.Map;

public abstract class AbstractParameter implements SearchParameter {
	public Modifier getModifier(String sm) {
		return MODIFIERMAP.get(sm);
	}
	public Comparator getComparator(String sc) {
		return COMPARATORMAP.get(sc);
	}
	public Comparator checkComparator(String value, String[] comparator) {
		Comparator c = null;
		for (Map.Entry<String, Comparator> e: COMPARATORMAP.entrySet()) {
			if (value.startsWith(e.getKey())) {
				comparator[0] = e.getKey();
				comparator[1] = value.substring(e.getKey().length());
				c = e.getValue();
				break;
			}
		}
		return c;
	}
}
