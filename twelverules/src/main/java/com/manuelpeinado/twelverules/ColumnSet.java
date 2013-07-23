package com.manuelpeinado.twelverules;

import java.util.ArrayList;
import java.util.List;

public class ColumnSet {
	
	private List<String> all = new ArrayList<String>();

	public ColumnSet(DatabaseColumn col) {
		and(col);
	}
	
	public ColumnSet and(DatabaseColumn col) {
		all.add(col.getName());
		return this;
	}

	public String[] get() {
		String[] aux = new String[0];
		return all.toArray(aux);
	}
}
