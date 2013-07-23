package com.manuelpeinado.twelverules;

import android.content.ContentValues;
import com.manuelpeinado.twelverules.DatabaseColumn;

public class ContentValuesCreator {
	private ContentValues dest;
	
	public ContentValuesCreator() {
		this(null);
	}

	public ContentValuesCreator(ContentValues dest) {
		this.dest = dest == null ? new ContentValues() : dest;
	}
	
	public void put(DatabaseColumn column, String value) {
		column.put(dest, value);
	}
}
